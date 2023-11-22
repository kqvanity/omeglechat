package com.chatter.omeglechat.data.network

import com.polendina.lib.ConnectionObserver
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
    .followRedirects(true)
    .retryOnConnectionFailure(true)
    .connectTimeout(5000000, TimeUnit.MILLISECONDS)
    .readTimeout(
        10000000,
        TimeUnit.MILLISECONDS
    )       // I guess this resolves the socket timeout problem, but I'm not sure what it is for :(
    .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
    .connectionPool(ConnectionPool())
    .build()

data class StartResponse (
    val events: List<List<String>>,
    val clientID: String
)
interface RetrofitResponse {
    @GET("start")
    fun initializeConnection(@QueryMap(encoded = true) querys: Map<String, String>): retrofit2.Call<StartResponse>
}

val verboseOkHttpClient = OkHttpClient()
    .newBuilder()
    .addInterceptor (
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    )
    .build()

// TODO: This should be used for other services at the website, like the number of available users and whatnot!
enum class BaseUrls(val url: String) {
    BASE("http://www.chatall.com"),
}

internal val emptyOkHttpHeaders: okhttp3.Headers = okhttp3.Headers.headersOf()
// TODO: I should read the current device's user agent
internal val okHttpHeaders: okhttp3.Headers = okhttp3.Headers.headersOf(
    "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/109.0",
    "Origin", BaseUrls.BASE.url,
    "Accept", " application/json",
    "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"
)

val retrofitInstance = Retrofit.Builder()
    .baseUrl(BaseUrls.BASE.url)
    .addConverterFactory(GsonConverterFactory.create())
    .client(verboseOkHttpClient)
    .build()
    .create(RetrofitResponse::class.java)

interface Connection {
    fun sendText(message: String)
    fun start()
    val commonInterests: MutableList<String>
    fun disconnect(): Unit
    var connectionObserver: ConnectionObserver
    var clientId: String
}

class ConnectionImpl: Connection {
    override fun sendText(message: String) {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override val commonInterests: MutableList<String>
        get() = TODO("Not yet implemented")

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override var connectionObserver: ConnectionObserver
        get() = TODO("Not yet implemented")
        set(value) = TODO("Not yet implemented")
    override var clientId: String
        get() = TODO("Not yet implemented")
        set(value) = TODO("Not yet implemented")
}