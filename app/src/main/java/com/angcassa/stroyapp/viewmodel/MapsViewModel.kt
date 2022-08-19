package com.angcassa.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angcassa.stroyapp.config.ApiConfig
import com.angcassa.stroyapp.config.response.StoryResponse
import com.angcassa.stroyapp.config.response.StoryforDatabase
import com.angcassa.stroyapp.view.Atoken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {
    private var _location = MutableLiveData<List<StoryforDatabase>>()
    val location: LiveData<List<StoryforDatabase>> = _location

    private var _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStory() {
        ApiConfig.getApiService().MapStories("Bearer $Atoken")
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    _location.value = response.body()?.listStory
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    _error.value = t.localizedMessage
                }
            })
    }

}