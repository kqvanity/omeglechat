package com.polendina.lib

import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
//import okio.IOException
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

fun okHttpStuff(
    fullUrl: String,
    headers: okhttp3.Headers,
    requestBody: RequestBody? = null,
    callback: (Response) -> Unit
): Unit {

    val requestBuilder: Request.Builder = Request.Builder()
        .url(fullUrl)
        .headers(headers)

    requestBody?.let {  // The requestBody parameter is made optional and set initially to null, so that I can have one function, without separate overloaded methods.
        requestBuilder.post(requestBody)
    } ?: run {
        requestBuilder.get()
    }

    val request: Request = requestBuilder.build()

    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, exception: IOException) {
            // TODO
        }
        override fun onResponse(call: Call, response: Response) {
            callback(response)
        }
    })

}
