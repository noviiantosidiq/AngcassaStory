package com.angcassa.stroyapp.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val prefName = stringPreferencesKey("nama_user")
    private val prefToken = stringPreferencesKey("token_user")
    private val prefUid = stringPreferencesKey("user_uid")

    fun getToken(): Flow<String?> {
        return dataStore.data.map { pref ->
            pref[prefToken]
        }
    }

    suspend fun logout() {
        dataStore.edit { pref ->
            pref[prefName] = ""
            pref[prefToken] = ""
            pref[prefUid] = ""
        }
    }

    suspend fun saveAuthresponse(nama: String, token: String, uid: String) {
        dataStore.edit { pref ->
            pref[prefName] = nama
            pref[prefToken] = token
            pref[prefUid] = uid
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }
}