package ru.mobilengineer.data.api.model.response

import com.google.gson.annotations.SerializedName

class MovingCreateResponse {
    @SerializedName("mcId")
    var mcId: Long? = null

    @SerializedName("response")
    var response: ActionnWithItemResponse? = null
}