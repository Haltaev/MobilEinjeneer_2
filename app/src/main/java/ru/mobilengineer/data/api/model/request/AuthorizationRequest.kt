package ru.mobilengineer.data.api.model.request

import com.google.gson.annotations.SerializedName

class AuthorizationRequest {

    @SerializedName("Login")
    var login: String? = null

    @SerializedName("Password")
    var password: String? = null
}