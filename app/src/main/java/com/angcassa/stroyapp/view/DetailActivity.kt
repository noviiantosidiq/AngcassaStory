package com.angcassa.stroyapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.angcassa.stroyapp.R
import com.angcassa.stroyapp.config.response.StoryforDatabase
import com.angcassa.stroyapp.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var bind: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val detail = intent.getParcelableExtra<StoryforDatabase>(DETAIL) as StoryforDatabase
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(detail.createdAt)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.US)
        val date = dateFormat.format(simpleDateFormat!!)

        bind.tvSName.text = detail.name
        bind.tvSDesc.text = detail.description
        bind.tvSCreated.text = date
        Glide.with(bind.root).load(detail.photoUrl).placeholder(R.drawable.ic_baseline_broken_image_24)
            .error(R.drawable.ic_baseline_broken_image_24).into(bind.ImgDStory)
    }

    companion object {
        const val DETAIL = "DETAIL_STORY"
    }
}