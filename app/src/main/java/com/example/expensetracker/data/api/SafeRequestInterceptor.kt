package com.example.expensetracker.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder

class SafeRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Защита от XSS - кодируем параметры
        val newUrl = originalRequest.url.newBuilder()
            .apply {
                originalRequest.url.queryParameterNames.forEach { name ->
                    val value = originalRequest.url.queryParameter(name) ?: return@forEach
                    setQueryParameter(
                        name,
                        URLEncoder.encode(value, "UTF-8")
                    )
                }
            }
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}