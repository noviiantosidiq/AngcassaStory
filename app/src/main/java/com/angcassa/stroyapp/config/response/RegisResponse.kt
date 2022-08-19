package com.angcassa.stroyapp.config.response

import com.google.gson.annotations.SerializedName

data class RegisResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
)
