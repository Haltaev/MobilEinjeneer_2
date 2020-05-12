package ru.mobilengineer.data.api

import retrofit2.Response
import retrofit2.http.GET
import ru.mobilengineer.data.api.model.Resp

interface ApiService {
    @GET("employees")
    suspend fun getData(): Response<Resp>
}