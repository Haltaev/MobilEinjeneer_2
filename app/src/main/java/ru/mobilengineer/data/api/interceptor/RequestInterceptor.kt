package ru.mobilengineer.data.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.mobilengineer.common.PreferencesManager

class RequestInterceptor(val preferencesManager: PreferencesManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        preferencesManager.token?.let { token ->
            request = request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build()
        }

        return chain.proceed(request)
    }
}