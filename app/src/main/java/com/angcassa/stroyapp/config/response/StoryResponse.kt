package com.angcassa.stroyapp.config.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<StoryforDatabase>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
