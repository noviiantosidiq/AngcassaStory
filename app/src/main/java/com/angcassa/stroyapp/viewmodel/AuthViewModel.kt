package com.angcassa.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: AuthPreferences) : ViewModel() {

    fun getToken(): LiveData<String?> {
        return pref.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun saveAuthresponse(nama: String, token: String, uid: String) {
        viewModelScope.launch {
            pref.saveAuthresponse(nama, token, uid)
        }
    }

}