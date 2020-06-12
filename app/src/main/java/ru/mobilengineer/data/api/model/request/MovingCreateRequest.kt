package ru.mobilengineer.data.api.model.request

import com.google.gson.annotations.SerializedName

class MovingCreateRequest {
    @SerializedName("mcId")
    var mcId: Long? = null

    @SerializedName("destinationId")
    var destinationId: Int? = null

    @SerializedName("destinationSide")
    var destinationSide: Int? = null

    @SerializedName("quantity")
    var quantity: Int? = null

    @SerializedName("skuId")
    var skuId: Int? = null
}