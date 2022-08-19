package com.angcassa.stroyapp.config.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.angcassa.stroyapp.config.database.StoryDatabase
import com.angcassa.stroyapp.config.response.StoryforDatabase
import com.angcassa.stroyapp.config.services.ApiService

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    fun getStory(): LiveData<PagingData<StoryforDatabase>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.StoryDao().getAllStory()
            }
        ).liveData
    }


}