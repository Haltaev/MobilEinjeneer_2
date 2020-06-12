package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName

class InstallCartridgeResponse {
    @SerializedName("mcId")
    var mcId: Long? = null

    @SerializedName("error")
    var error: InstallInDeviceError? = null

    @SerializedName("response")
    var response: ActionnWithItemResponse? = null
}

class InstallInDeviceError {
    var status: Int? = null
    var title: String? = null
}