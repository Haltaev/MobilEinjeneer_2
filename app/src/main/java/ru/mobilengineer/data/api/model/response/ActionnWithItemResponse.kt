package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName

class ActionnWithItemResponse {
    @SerializedName("warehouseId")
    var warehouseId: Int? = null

    @SerializedName("targetWarehouseId")
    var targetWarehouseId: Int? = null

    @SerializedName("movingId")
    var movingId: Int? = null

    @SerializedName("quantity")
    var quantity: Int? = null

    @SerializedName("received")
    var received: String? = null

    @SerializedName("skuId")
    var skuId: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("vendorCode")
    var vendorCode: String? = null
}