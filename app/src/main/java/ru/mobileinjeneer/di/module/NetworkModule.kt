package ru.mobileinjeneer.di.module

import android.content.Context
import ru.mobileinjeneer.data.api.interceptor.RequestInterceptor
import com.readystatesoftware.chuck.BuildConfig
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mobileinjeneer.common.PreferencesManager
import ru.mobileinjeneer.data.api.ApiService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val REQUEST_TIMEOUT = 120.toLong()
const val MAX_CACHE_SIZE = (10 * 1024 * 1024).toLong()

@Module(includes = [AppModule::class])
class NetworkModule {
    @Provides
    @Singleton
    fun provideInterceptor(preferencesManager: PreferencesManager): RequestInterceptor {
        return RequestInterceptor(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        context: Context,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {
        val cache = Cache(context.cacheDir, MAX_CACHE_SIZE)

        val builder = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addInterceptor(ChuckInterceptor(context))
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .cache(cache)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRestAdapter(okHttpClient: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder()
        builder.client(okHttpClient)
            .baseUrl("http://dummy.restapiexample.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideApiService(restAdapter: Retrofit): ApiService {
        return restAdapter.create<ApiService>(ApiService::class.java)
    }
}