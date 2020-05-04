package ru.mobileinjeneer.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.mobileinjeneer.data.api.module.Resp

interface ApiService {
    @GET("employees")
    suspend fun getData(): Response<Resp>
}