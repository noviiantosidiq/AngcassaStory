package com.angcassa.stroyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AuthViewModelFactory(private val pref:AuthPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class:"+ modelClass.name)
    }
}