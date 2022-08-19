package com.angcassa.stroyapp.di

import android.content.Context
import com.angcassa.stroyapp.config.ApiConfig
import com.angcassa.stroyapp.config.data.StoryRepository
import com.angcassa.stroyapp.config.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val db = StoryDatabase.getDatabase(context)
        val api = ApiConfig.getApiService()
        return StoryRepository(db,api)
    }
}