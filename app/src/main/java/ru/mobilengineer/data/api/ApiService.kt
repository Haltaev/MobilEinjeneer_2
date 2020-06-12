package ru.mobilengineer.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import ru.mobilengineer.data.api.model.request.*
import ru.mobilengineer.data.api.model.response.*

interface ApiService {

    @POST("users/auth")
    suspend fun authorization(
        @Body body: AuthorizationRequest
    ): Response<AuthorizationResponse>

    @GET("users/self")
    suspend fun getSelfInfo(): Response<SelfInfoResponse>

    @GET("warehouses/stock")
    suspend fun getMyStock(
        @Query("order") order : String
    ): Response<ArrayList<MyStockResponse>>

    @GET("warehouses")
    suspend fun getWarehouses(): Response<ArrayList<WarehousesResponse>>

    @GET("warehouses/stock/{warehouseId}")
    suspend fun getIncommingSkuFromWarehouseOldRequest(
        @Path("warehouseId") warehouseId: String
    ): Response<ArrayList<IncommingSkuFromAnyResponse>>

    @POST("Movings")
    suspend fun movingCreate(
        @Body movingItems: ArrayList<MovingCreateRequest>
    ): Response<ArrayList<MovingCreateResponse>>

    @DELETE("Movings/{movingId}")
    suspend fun movingCancel(
        @Path ("movingId") movingId: String
    ): Response<ResponseBody>

    @PUT("Movings/{movingId}/{quantity}")
    suspend fun movingAccept1(
        @Path("movingId") movingId: String,
        @Path("quantity") quantity: String,
        @Body actionWithItemRequest: ArrayList<MovingAcceptRequest>
    ): Response<MovingAcceptResponse>

    @PUT("Movings/{movingId}")
    suspend fun movingAccept2(
        @Path("movingId") movingId: String,
        @Body actionWithItemRequest: ArrayList<MovingAcceptRequest>
    ): Response<MovingAcceptResponse>

    @GET("movings")
    suspend fun getIncommingSkuFromEngineers(   /// проверить
        @Query("from") fromWho: String
    ): Response<ArrayList<IncommingSkuFromAnyResponse>>

    @GET("movings")
    suspend fun getIncommingSkuFromWarehouse(   /// проверить
        @Query("from") fromWho: String
    ): Response<ArrayList<IncommingSkuFromAnyResponse>>

    @GET("movings")
    suspend fun getIncommingSkuFromAll(
        @Query("order") order : String,
        @Query("from") fromWho: String
    ): Response<ArrayList<IncommingSkuFromAnyResponse>>

    @GET("devices/{serialNumber}")
    suspend fun getDeviceInfo(
        @Path ("serialNumber") serialNumber: String
    ): Response<ArrayList<DeviceResponse>>

    @GET("devices/{serialNumber}")
    suspend fun getDeviceList(): Response<ArrayList<DeviceResponse>>

    @POST("Movings")
    suspend fun installCartridge(
        @Body serialNumber: ArrayList<InstallCartridgeRequest>
    ): Response<ArrayList<InstallCartridgeResponse>>

    @GET("users")
    suspend fun getAnotherEngineers(): Response<ArrayList<AnotherEngineersResponse>>
}