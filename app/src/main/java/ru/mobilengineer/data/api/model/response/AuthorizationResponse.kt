package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName

class AuthorizationResponse {

    @SerializedName("token")
    var token: String? = null

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("barcode")
    var barcode: String? = null

    @SerializedName("warehouseId")
    var warehouseId: Int? = null

    @SerializedName("photo")
    var photo: String? = null
}