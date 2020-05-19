package ru.mobilengineer.common

import com.google.gson.annotations.SerializedName

class FileUploadResponse {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("file")
    var file: String? = null

    @SerializedName("title")
    var title: String? = null
}