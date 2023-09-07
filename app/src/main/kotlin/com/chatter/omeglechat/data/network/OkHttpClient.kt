package com.chatter.omeglechat.data.network

import com.google.gson.JsonArray
import okhttp3.ConnectionPool
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
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

enum class BaseUrls(val url: String) {
    BASE("http://www.omegle.com"),
    EVENTS("https://front36.omegle.com/events"),
    SUB_DOMAIN("https://front36.omegle.com/"),
    DISCONNECT("https://front46.omegle.com/disconnect"),
    SEND("https://front22.omegle.com/send"),
    CHECK("https://waw4.omegle.com/check"),
    COMMON_LIKES("https://front46.omegle.com/stoplookingforcommonlikes")
}

internal val emptyOkHttpHeaders: okhttp3.Headers = okhttp3.Headers.headersOf()
internal val okHttpHeaders: okhttp3.Headers = okhttp3.Headers.headersOf(
    "Referer", BaseUrls.BASE.url,
    "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/109.0",
    "Cache-Control", "no-cache",
    "Origin", BaseUrls.BASE.url,
    "Accept", " application/json",
    "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"
)

val retrofitInstance = Retrofit.Builder()
    .baseUrl(BaseUrls.SUB_DOMAIN.url)
    .addConverterFactory(GsonConverterFactory.create())
    .client(verboseOkHttpClient)
    .build()
    .create(RetrofitResponse::class.java)

internal val DEFAULT_REQUEST_BODY: RequestBody = FormBody.Builder().build()

suspend fun okHttpStuff(
    fullUrl: String,
    headers: okhttp3.Headers,
    requestBody: RequestBody? = null,
): Response {

    val requestBuilder: Request.Builder = Request.Builder()
        .url(fullUrl)
        .headers(headers)

    requestBody?.let {  // The requestBody parameter is made optional and set initially to null, so that I can have one function, without separate overloaded methods.
        requestBuilder.post(requestBody)
    } ?: run {
        requestBuilder.get()
    }

   return okHttpClient.newCall(request = requestBuilder.build()).execute()
}