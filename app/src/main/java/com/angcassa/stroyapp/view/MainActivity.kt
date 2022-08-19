package com.angcassa.stroyapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.angcassa.stroyapp.R
import com.angcassa.stroyapp.config.adapter.LoadingStateAdapter
import com.angcassa.stroyapp.config.adapter.StoryDBAdapter
import com.angcassa.stroyapp.databinding.ActivityMainBinding
import com.angcassa.stroyapp.viewmodel.*

class MainActivity : AppCompatActivity() {

    private val Context.dataSore: DataStore<Preferences> by preferencesDataStore(name = "Authresponse")
    private lateinit var bind: ActivityMainBinding
    private var count = 0
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.rvStory.layoutManager = LinearLayoutManager(this)
        getDataStory()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btAdd -> {
                startActivity(Intent(this@MainActivity, UploadActivity::class.java))
                true
            }
            R.id.btMaps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }
            R.id.btLogout -> {
                logout()
                true
            }
            else -> true
        }
    }

    override fun onBackPressed() {
        count += 1
        Toast.makeText(this@MainActivity, R.string.close, Toast.LENGTH_SHORT).show()
        if (count == 2) {
            finish()
        }
    }

    private fun logout() {
        val pref = AuthPreferences.getInstance(dataSore)
        val authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(pref)
        )[AuthViewModel::class.java]

        val dialogTitle = "Yakin ingin logout?"
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                authViewModel.logout()
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            }
            .setNegativeButton("Tidak") { _, _ ->

            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getDataStory() {
        val adapter = StoryDBAdapter()
        bind.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                getDataStory()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

}