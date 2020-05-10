package ru.mobileinjeneer.data.api

import retrofit2.Response
import retrofit2.http.GET
import ru.mobileinjeneer.data.api.model.Resp

interface ApiService {
    @GET("employees")
    suspend fun getData(): Response<Resp>
}