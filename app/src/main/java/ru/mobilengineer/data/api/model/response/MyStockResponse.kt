package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName
import ru.mobilengineer.data.api.model.SerialNumber

class MyStockResponse {
    @SerializedName("warehouseId")
    var warehouseId: Int? = null

    @SerializedName("quantity")
    var quantity: Int? = null

    @SerializedName("targetWarehouseId")
    var targetWarehouseId: Int? = null

    @SerializedName("skuId")
    var skuId: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("movingId")
    var movingId: Int? = null

    @SerializedName("vendorCode")
    var vendorCode: String? = null

    @SerializedName("isPicked")
    var isPicked: Boolean = false

    @SerializedName("serialNumber")
    var serialNumber: String? = null

    @SerializedName("received")
    var received: String? = null

    @SerializedName("isEnabled")
    var isEnabled: Boolean = false

    @SerializedName("serialNumbers")
    var serialNumbers: ArrayList<SerialNumber> = arrayListOf()
}