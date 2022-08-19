package com.angcassa.stroyapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.angcassa.stroyapp.R
import com.angcassa.stroyapp.config.ApiConfig
import com.angcassa.stroyapp.config.response.LoginResponse
import com.angcassa.stroyapp.config.response.RegisResponse
import com.angcassa.stroyapp.databinding.ActivityAuthBinding
import com.angcassa.stroyapp.viewmodel.AuthPreferences
import com.angcassa.stroyapp.viewmodel.AuthViewModel
import com.angcassa.stroyapp.viewmodel.AuthViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthActivity : AppCompatActivity() {

    private lateinit var bind: ActivityAuthBinding
    private val Context.dataSore: DataStore<Preferences> by preferencesDataStore(name = "Authresponse")
    private var count = 0
    private var login = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(bind.root)
        showLoading(false)
        playAnimate()

        val pref = AuthPreferences.getInstance(dataSore)
        val authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        bind.tvReg.setOnClickListener {
            if (bind.tvReg.text == "Login") {
                showLogin(true)
            } else {
                showLogin(false)
            }
        }

        bind.btLogin.setOnClickListener {
            val nama = bind.edName.text.toString()
            val email = bind.edMail.text.toString()
            val pass = bind.edPass.text.toString()
            showLoading(true)
            bind.edMail.isEnabled = false
            bind.edPass.isEnabled = false
            bind.btLogin.isEnabled = false
            if (login) {
                val client = ApiConfig.getApiService().loginUser(email, pass)
                client.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val token = response.body()?.loginResult?.token.toString()
                        val name = response.body()?.loginResult?.name.toString()
                        val uid = response.body()?.loginResult?.userId.toString()
                        Atoken = token
                        showLoading(false)
                        if (response.body()?.error == false) {
                            authViewModel.saveAuthresponse(name, token, uid)
                            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                            finish()
                        } else {
                            showLoading(false)
                            Toast.makeText(this@AuthActivity, R.string.err_Login, Toast.LENGTH_SHORT)
                                .show()
                            bind.edMail.isEnabled = true
                            bind.edPass.isEnabled = true
                            bind.btLogin.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@AuthActivity, t.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                        showLoading(false)
                        Log.e("fail", t.message.toString())
                        bind.edMail.isEnabled = true
                        bind.edPass.isEnabled = true
                        bind.btLogin.isEnabled = true
                    }

                })
            } else {
                bind.edMail.isEnabled = false
                bind.edName.isEnabled = false
                bind.edPass.isEnabled = false
                bind.btLogin.isEnabled = false
                val client = ApiConfig.getApiService().registerUser(nama, email, pass)
                client.enqueue(object : Callback<RegisResponse> {
                    override fun onResponse(
                        call: Call<RegisResponse>,
                        response: Response<RegisResponse>
                    ) {
                        if (response.body()?.error == false) {
                            Toast.makeText(
                                this@AuthActivity,
                                R.string.sc_reg,
                                Toast.LENGTH_SHORT
                            ).show()
                            showLogin(true)
                            showLoading(false)
                        } else {
                            showLoading(false)
                            Toast.makeText(
                                this@AuthActivity,
                                R.string.err_reg,
                                Toast.LENGTH_SHORT
                            ).show()
                            bind.edMail.isEnabled = true
                            bind.edPass.isEnabled = true
                            bind.edName.isEnabled = true
                            bind.btLogin.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<RegisResponse>, t: Throwable) {
                        Toast.makeText(this@AuthActivity, t.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                        showLogin(false)
                        showLoading(false)
                        bind.edMail.isEnabled = true
                        bind.edPass.isEnabled = true
                        bind.edName.isEnabled = true
                        bind.btLogin.isEnabled = true
                    }

                })
            }
        }

    }

    override fun onBackPressed() {
        count += 1
        Toast.makeText(this@AuthActivity, R.string.close, Toast.LENGTH_SHORT).show()
        if (count == 2) {
            finish()
        }
    }

    private fun playAnimate() {
        AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(bind.tvAuth, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(bind.edName, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(bind.edMail, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(bind.edPass, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(bind.btLogin, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(bind.lsgin, View.ALPHA, 1f).setDuration(500)
            )
            start()
        }
    }

    private fun showLogin(auth: Boolean) {
        if (auth) {
            login = true
            bind.tvReg.setText(R.string.reg)
            bind.btLogin.setText(R.string.login)
            bind.textView2.setText(R.string.regQ)
            bind.edName.visibility = View.GONE
            bind.edMail.setText("")
            bind.edMail.error = null
            bind.edPass.setText("")
            bind.edPass.error = null
        } else {
            login = false
            bind.edMail.setText("")
            bind.edMail.error = null
            bind.edPass.setText("")
            bind.edPass.error = null
            bind.edName.setText("")
            bind.edName.error = null
            bind.tvReg.setText(R.string.login)
            bind.btLogin.setText(R.string.reg)
            bind.textView2.setText(R.string.loginQ)
            bind.edName.visibility = View.VISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            bind.progressBar2.visibility = View.VISIBLE
        } else {
            bind.progressBar2.visibility = View.GONE
        }
    }
}