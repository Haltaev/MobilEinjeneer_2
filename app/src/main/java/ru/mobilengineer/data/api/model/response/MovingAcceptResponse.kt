package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName

class MovingAcceptResponse {
    @SerializedName("mcId")
    var mcId: Long? = null

    @SerializedName("response")
    var response: ActionnWithItemResponse? = null
}