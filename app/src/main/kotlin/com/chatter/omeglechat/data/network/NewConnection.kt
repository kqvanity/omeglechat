package com.chatter.omeglechat.data.network

import com.chatter.omeglechat.domain.model.ConnectionStates
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.HttpUrl
import retrofit2.Response
import retrofit2.awaitResponse
import java.net.URI
import kotlin.math.floor

object NewConnection {

    private val commonInterests: MutableList<String> = mutableListOf()

    /**
     * Send POST request to obtain events.
     *
     * @param clientId The remote user clientID.
     */
    suspend fun eventsRemoteRequest(
        clientId: String
    ): okhttp3.Response {
        return okHttpStuff(
            fullUrl = BaseUrls.EVENTS.url,
            headers = okHttpHeaders,
            requestBody = FormBody.Builder().add("id", clientId).build()
        )
    }

    /**
     * Parse remote events.
     *
     */
    suspend fun parseEventsRemoteRequest(
        response: String
    ) {
        Gson().fromJson(response, JsonElement::class.java).run {
            when(this) {
                is JsonObject -> {
                    getAsJsonArray("events")
                        .get(0)
                        .asJsonArray
                        .get(0)
                        .asString
                        .let { parseEvents(listOf(it)) }
                }
                is JsonArray -> {
                    forEach {
                        (it as JsonArray)
                            .asList()
                            .map { it.asString }
                            .let { parseEvents(it) }
                    }
                }
            }
        }
    }

    /**
     * Send a terminating request to the server, that you no longer want to match with a user with common interests.
     * It's often automatically sent by the web client after 10 seconds, if no matches were found.
     */
    suspend fun stopLookingForCommonInterests() {
        okHttpStuff(
            fullUrl = BaseUrls.COMMON_LIKES.url,
            headers = okHttpHeaders,
            requestBody = FormBody.Builder().add("id", clientId).build()
        ).let {
            if (it.code != 200 && it.body?.string() != "win") { }
        }
    }

    /**
     * Send a disconnect request to the user.
     * It's not mandatory for the client, as it can generally generate another ID, but the official implementation is so, as it signal the server hence the other user.
     */
    suspend fun disconnect() {
        okHttpStuff(
            fullUrl = BaseUrls.DISCONNECT.url,
            headers = okHttpHeaders,
            requestBody = FormBody.Builder().add("id", clientId).build()
        ).let {
            if (it.code != 200 && it.body?.string() != "win") {
//                throw Exception(
//                    message = "Disconnect from user",
//                    cause = Throwable("Invalid user disconnect termination response. Should be 'win'")
//                )
            }
        }
    }

    /**
     * The connection observer dictating the behavior of incoming events.
     *
     */
    var connectionObserver: ConnectionObserver? = null

    /**
     * Parse user return Object
     */
    fun parseEvents(response: List<String>): Unit {
        when (response.first()) {
            ConnectionStates.RECAPTCHA_REQUIRED.state -> connectionObserver?.onRecaptchaRequired()
            ConnectionStates.CONNECTED.state -> {
                var commonInterests = listOf<String>()
                // TODO: Implement functionality for notifying the user(s) that they're speaking the same language
                var speakSameLanguage: Boolean = false
                try {
                    (response as JsonArray).toList().forEach {
                        try {
                            it.asJsonArray.get(0).asString.apply {
                                if (equals("serverMessage")) {
                                    speakSameLanguage = true
                                } else if (equals("commonLikes")) {
                                    commonInterests = it.asJsonArray.get(1).asJsonArray.map { it.asString }.toList()
                                }
                            }
                        } catch (exception: IndexOutOfBoundsException) { }
                    }
                } catch (e: ClassCastException) {}

                connectionObserver?.onConnected(commonInterests = commonInterests)
            }
            ConnectionStates.TYPING.state -> connectionObserver?.onTyping()
            ConnectionStates.STOPPED_TYPING.state -> connectionObserver?.onStoppedTyping()
            ConnectionStates.MESSAGE.state -> {
                connectionObserver?.onGotMessage(response.last())
            }
            ConnectionStates.DISCONNECTED.state -> connectionObserver?.onUserDisconnected()
            ConnectionStates.WAITING.state -> connectionObserver?.onWaiting()
            ConnectionStates.ERROR.state -> connectionObserver?.onError()
            ConnectionStates.CONNECTION_ERROR.state -> connectionObserver?.onConnectionError()
        }
        // I can't rely on direct 'when expression', because of this extraneous (not-so-negligible tho) condition
        connectionObserver?.onEvent(response.first())
    }

    /**
     * Send a message to the remote recipient.
     *
     * @param text The text message that's to be sent
     */
    suspend fun sendText(text: String): Unit {
        okHttpStuff(
            fullUrl = BaseUrls.SEND.url,
            headers = okHttpHeaders,
            // I don't know if there's a better 'compact' solution that breaking up the addition on two 'add' methods
            requestBody = FormBody.Builder()
                .add( "msg", text)
                .add( "id", clientId)
                .build()
//                        MultipartBody.Builder()
//                            .addFormDataPart("msg", text)
//                            .addFormDataPart( "id", this.clientId)
//                            .build()
        ).let {
            if (it.code != 200 && it.body?.string() != "win") {
//                throw Exception(
//                    message = "Outgoing message not delivered",
//                    cause = Throwable("Outgoing message couldn't be delivered!")
//                )
            }
        }
    }

    /**
     * A random ID used to to connect to another user.
     *
     */
    val randomId: String = floor(Math.random() * 1000000000 + 1000000000).toInt()
        .toString(36)
        .uppercase()

    /**
     * The current ID of the remote recipient.
     * It can be used to temporarily add or block certain users by auto-disconnecting.
     *
     */
    var clientId: String = String()

    private var ccValue: String = ""
    /**
     * Obtain the CC value. The generic value gets generated at the very first request.
     */
    fun getCCValue(): String {
        return (ccValue)
    }
    suspend fun setCCValue(): Unit {
        okHttpStuff(
            fullUrl = BaseUrls.CHECK.url,
            headers = emptyOkHttpHeaders,
            requestBody = DEFAULT_REQUEST_BODY
        ).body?.let {
            ccValue = it.string()
        }
    }

    /**
     * Ignite a new remote session with remote recipient.
     */
    suspend fun initalizeConnection(): Response<StartResponse> {
        return retrofitInstance.initializeConnection(mapOf(
            "caps" to "recaptcha2,t3",
            "firstevents" to "1",
            "spid" to "",
            "randid" to randomId,
            "cc" to ccValue,
            "topics" to URI(null, null, commonInterests.map { "\"${it}\"" }.toList().toString(), null).rawPath,
            "lang" to "en"
        )).awaitResponse()
    }

    /**
     * Obtain various metadata about the website e.g., count, antinudeservers, spyQueueTime, antinudepercent, spyeeQueueTime, timestamp, servers
     */
    suspend fun obtainSiteMetadata(): JsonObject? {
        var response: JsonObject? = null;
        okHttpStuff(
            fullUrl = HttpUrl.Builder()
                .scheme("https")
                .host(BaseUrls.SUB_DOMAIN.url)
                .addPathSegment("status")
                .addQueryParameter("nocache", "7182637182637")
                .addQueryParameter("randid", randomId)
                .build()
                .toString(),
            headers = emptyOkHttpHeaders
        ).body?.run {
            response = Gson().fromJson(this.string(), JsonObject::class.java)
        }
        return (response)
    }

    /**
     * Set the common interests topics for the user.
     * These interests gets sent tot he server to find a matching ones, or a subset of the matches.
     * The server responds with a commonLikes response, to signal the interests (if any) that matched up with the remote recipient.
     *
     * @param commonInterests The list of common interests
     */
    fun setCommonInterests(commonInterests: MutableList<String>) {
        NewConnection.commonInterests.clear()
        NewConnection.commonInterests.addAll(commonInterests)
    }

    private val eventResponses: MutableList<okhttp3.Response> = mutableListOf()

    /**
     * Ignite a new connection, by setting the random ID and CC value, then initialize a connection with a random recipient.
     */
    suspend fun start() {
        if (ccValue.isEmpty()) setCCValue()
        initalizeConnection().run {
            if (isSuccessful) body()?.let {
                // When the remote server blocks the IP, it sends an empty JSON object {}, with the text message "Error connecting to server. Please try again." at the webpage.
                if (it.clientID == null) {
                    parseEvents(listOf(ConnectionStates.CONNECTION_ERROR.state))
                } else {
                    clientId = it.clientID
                    parseEvents(listOf(it.events.first().first()))
                }
            }
        }
    }

    suspend fun continueOn() {
        flow {
            // TODO: Might implement plain async/await & while loop, instead of flows.
            while(true) {
                eventsRemoteRequest(clientId).run {
                    emit(this.body?.string())
                }
                println("Request")
            }
        }.collect { response ->
            CoroutineScope(Dispatchers.IO).launch {
                println(response)
                response?.let {
                    println("Value is ${it}")
                    parseEventsRemoteRequest(it)
                }
                println("Collect")
            }
        }
    }

}