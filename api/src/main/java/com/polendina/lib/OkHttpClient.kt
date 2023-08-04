package com.polendina.lib

import com.google.gson.JsonArray
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.POST
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
    val events: JsonArray,
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

val retrofitInstance = Retrofit.Builder()
    .baseUrl("https://front36.omegle.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(verboseOkHttpClient)
    .build()
    .create(RetrofitResponse::class.java)

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
