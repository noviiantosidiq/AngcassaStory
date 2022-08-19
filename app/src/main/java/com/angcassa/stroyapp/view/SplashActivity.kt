package com.angcassa.stroyapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.angcassa.stroyapp.databinding.ActivitySplashBinding
import com.angcassa.stroyapp.viewmodel.AuthPreferences
import com.angcassa.stroyapp.viewmodel.AuthViewModel
import com.angcassa.stroyapp.viewmodel.AuthViewModelFactory


var Atoken: String = ""

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySplashBinding
    private val Context.dataSore: DataStore<Preferences> by preferencesDataStore(name = "Authresponse")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySplashBinding.inflate(layoutInflater)
        val pref = AuthPreferences.getInstance(dataSore)
        val authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]
        setContentView(bind.root)
        authViewModel.getToken().observe(this)
            {
                Atoken = it.toString()
                bind.root.postDelayed({
                    if (Atoken.length > 10) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                        finish()
                    }
                }, 5000)
            }


    }
}