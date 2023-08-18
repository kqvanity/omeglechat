package com.polendina.lib

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.RequestBody
import retrofit2.awaitResponse
import java.net.URI
import kotlin.math.floor

class NewConnection {

    private val commonInterests: MutableList<String> = mutableListOf<String>()

    private val okHttpHeaders: okhttp3.Headers = okhttp3.Headers.headersOf(
        "Referer", "https://www.omegle.com",
        "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/109.0",
        "Cache-Control", "no-cache",
        "Origin", "http://www.omegle.com",
        "Accept", " application/json",
        "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"
    )
    private val emptyOkHttpHeaders: okhttp3.Headers = okhttp3.Headers.headersOf()

    private val DEFAULT_REQUEST_BODY: RequestBody = FormBody.Builder().build()


    /**
     * Send POST request to obtain events, then parse them.
     * The internal parsing function recursively calls this function for long-polling.
     */
    private suspend fun eventsRemoteRequest(): Unit {
        okHttpStuff(
            fullUrl = "https://front36.omegle.com/events",
            headers = okHttpHeaders,
            requestBody = FormBody.Builder().add("id", this.clientId).build()
        ).apply {
            body?.apply {
                try {
                    parseEvents(Gson().fromJson(this.string(), JsonArray::class.java))
                } catch (exception: JsonSyntaxException) {
                    exception.printStackTrace()
                    apply {
                        try {
                            parseEvents(Gson().fromJson(this.string(), JsonObject::class.java))
                        } catch (exception: IllegalStateException) {
                            exception.printStackTrace()
                        }
                    }
                }
            }
            eventsRemoteRequest()
        }
    }

    /**
     * Send a terminating request to the server, that you no longer want to match with a user with common interests.
     * It's often automatically sent by the web client after 10 seconds, if no matches were found.
     */
    suspend fun stopLookingForCommonInterests() {
        okHttpStuff(
            fullUrl = "https://front46.omegle.com/stoplookingforcommonlikes",
            headers = okHttpHeaders,
            requestBody = FormBody.Builder().add("id", this.clientId).build()
        ).let {
            if (it.code != 200 && it.body?.string() != "win") {
            }
        }
    }

    /**
     * Send a disconnect request to the user.
     * It's not mandatory for the client, as it can generally generate another ID, but the official implementation is so, as it signal the server hence the other user.
     */
    suspend fun disconnect() {
        okHttpStuff(
            fullUrl = "https://front46.omegle.com/disconnect",
            headers = okHttpHeaders,
            requestBody = FormBody.Builder().add("id", this.clientId).build()
        ).let {
            if (it.code != 200 && it.body?.string() != "win") {
//                throw Exception(
//                    message = "Disconnect from user",
//                    cause = Throwable("Invalid user disconnect termination response. Should be 'win'")
//                )
            }
        }
    }

    private var observer: ConnectionObserver? = null

    /**
     * Set the connection observer dictating the behavior of incoming events.
     *
     * @param observer The connection observer class implementing the ConnectionObserver interface.
     */
    fun setObserver(observer: ConnectionObserver) {
        this.observer = observer
    }

    /*
        - Parse user return Object
     */
    private suspend fun parseEvents(response: Any?): Unit {

        var responseString: String = ""

        if (response is JsonObject) {
            responseString = response.getAsJsonArray("events").get(0).asJsonArray.get(0).asString
        } else if (response is JsonArray) {
            if (response.size() != 0) {         // Mitigating empty responses. They take place when the conversation is on a halt for example.
                responseString = response.get(0).asJsonArray.get(0).asString
            }
        }

        when (responseString) {
            ("connected") -> {
                var commonInterests = listOf<String>()
                var speakSameLanguage: Boolean = false
                try {
                    (response as JsonArray).toList().forEach {
                        it.asJsonArray.get(0).asString.apply {
                            if (equals("serverMessage")) {
                                speakSameLanguage = true
                            } else if (equals("commonLikes")) {
                                commonInterests = it.asJsonArray.get(1).asJsonArray.map { it.asString }.toList()
                            }
                        }
                    }
                } catch (exception: IndexOutOfBoundsException) { }

                observer?.onConnected(commonInterests = commonInterests)
            }
            ("typing") -> observer?.onTyping()
            ("stoppedTyping") -> observer?.onStoppedTyping()
            ("recaptchaRequired") -> observer?.onRecaptchaRequired()
            ("gotMessage") -> {
                val userMessage = (response as JsonArray).get(0).asJsonArray.get(1).asString
                observer?.onGotMessage(userMessage)
            }

            ("strangerDisconnected") -> {
                observer?.onUserDisconnected()
            }

            ("waiting") -> {
                observer?.onWaiting()
            }
            ("error") -> observer?.onError()

        }
        observer?.onEvent(responseString)
    }


    /**
     * Send a message to the remote recipient.
     *
     * @param text The text message that's to be sent
     */
    suspend fun sendText(text: String): Unit {
        okHttpStuff(
            fullUrl = "https://front22.omegle.com/send",
            headers = okHttpHeaders,
            // I don't know if there's a better 'compact' solution that breaking up the addition on two 'add' methods
            requestBody = FormBody.Builder()
                .add( "msg", text)
                .add( "id", this.clientId)
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

    private var randomId: String = ""
    /**
     * Generate a random ID used to to connect to another user.
     * It can be used to evade blocking (if the other user is also using the same blocking technique of the same/similar app)
     */
    private fun setRandomId(): String {
        // nao
        val randomId: String = floor(Math.random() * 1000000000 + 1000000000).toInt()
            .toString(36)
            .uppercase()
        return (randomId)
    }

    private var clientId: String = String()
    /**
     * Get the current ID of the remote recipient.
     * It can be used to temporarily add or block certain users by auto-disconnecting.
     *
     * @return The ID of the remote recipient
     */
    fun getClientId(): String {
        return (this.clientId)
    }
    /**
     * Set the ID of the remote recipient.
     *
     * @param clientId The ID to be set.
     */
    private fun setClientId(clientId: String): Unit {
        this.clientId = clientId
    }

    private var ccValue: String = ""
    /**
     * Obtain the CC value. The generic value gets generated at the very first request.
     */
    fun getCCValue(): String {
        return (this.ccValue)
    }
    suspend fun setCCValue(): Unit {
        okHttpStuff(
            fullUrl = "https://waw4.omegle.com/check",
            headers = emptyOkHttpHeaders,
            requestBody = DEFAULT_REQUEST_BODY
        ).body?.let {
            this.ccValue = it.string()
        }
    }

    /**
     * Ignite a new remote session with remote recipient.
     */
    suspend fun initalizeConnection(): Unit {
        val response = retrofitInstance.initializeConnection(mapOf(
            "caps" to "recaptcha2,t3",
            "firstevents" to "1",
            "spid" to "",
            "randid" to this.randomId,
            "cc" to this.ccValue,
            "topics" to URI(null, null, this.commonInterests.map { "\"${it}\"" }.toList().toString(), null).rawPath,
            "lang" to "en"
        )).awaitResponse()
        if (response.isSuccessful) {
            response.body()?.let {
                setClientId(clientId = it.clientID)
                parseEvents(it.events)
                eventsRemoteRequest()
            }
        }
    }

    /**
     * Obtain various metadata about the website e.g., count, antinudeservers, spyQueueTime, antinudepercent, spyeeQueueTime, timestamp, servers
     */
    suspend fun obtainSiteMetadata(): JsonObject? {
        var response: JsonObject? = null;
        okHttpStuff(
            fullUrl = HttpUrl.Builder()
                .scheme("https")
                .host("front36.omegle.com")
                .addPathSegment("status")
                .addQueryParameter("nocache", "7182637182637")
                .addQueryParameter("randid", this.randomId)
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
        this.commonInterests.clear()
        this.commonInterests.addAll(commonInterests)
    }

    /**
     * Ignite a new connection, by setting the random ID and CC value, then initialize a connection with a random recipient.
     */
    suspend fun start() {
        setRandomId()
        CoroutineScope(Dispatchers.IO).launch {
            if (ccValue.isEmpty()) {
                setCCValue()
            }
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.IO).launch {
                initalizeConnection()
            }
        }
    }

}
