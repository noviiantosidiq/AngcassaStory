package com.angcassa.stroyapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.angcassa.stroyapp.di.Injection
import com.angcassa.stroyapp.config.data.StoryRepository
import com.angcassa.stroyapp.config.response.StoryforDatabase
import java.lang.IllegalArgumentException

class MainViewModel(storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<StoryforDatabase>> =
        storyRepository.getStory().cachedIn(viewModelScope)

}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}