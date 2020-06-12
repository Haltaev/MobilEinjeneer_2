package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName

class WarehousesResponse {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("barcode")
    var barcode: String? = null

    @SerializedName("isMain")
    var isMain: Boolean? = null
}