package com.angcassa.stroyapp.config.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.angcassa.stroyapp.R
import com.angcassa.stroyapp.config.response.StoryforDatabase
import com.angcassa.stroyapp.databinding.ItemStoryDetailBinding
import com.angcassa.stroyapp.view.DetailActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class StoryDBAdapter :
    PagingDataAdapter<StoryforDatabase, StoryDBAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bind =
            ItemStoryDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(bind)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val detail = getItem(position)
        if (detail != null) {
            holder.bind(detail)
        }
    }

    class MyViewHolder(private val binding: ItemStoryDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: StoryforDatabase) {
            binding.root.postDelayed({
                binding.progressBar3.visibility = View.GONE
            }, 3000)
            val simpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(detail.createdAt)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.US)
            val date = dateFormat.format(simpleDateFormat!!)
            binding.tvSName.text = detail.name
            binding.tvSDesc.text = detail.description
            binding.tvSCreated.text = date.toString()
            Glide.with(binding.root).load(detail.photoUrl)
                .placeholder(R.drawable.ic_baseline_broken_image_24)
                .error(R.drawable.ic_baseline_broken_image_24).into(binding.ImgStory)


            binding.root.setOnClickListener {
                val pindah =
                    Intent(it.context, DetailActivity::class.java)
                pindah.putExtra(DetailActivity.DETAIL, detail)

                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        it.context as Activity,
                        Pair(binding.ImgStory, "StoryImage"),
                        Pair(binding.tvSName, "nama"),
                        Pair(binding.tvSDesc, "deskripsi"),
                        Pair(binding.tvSCreated, "dibuat")
                    )
                it.context.startActivity(pindah, optionCompat.toBundle())
            }
        }


    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryforDatabase>() {
            override fun areItemsTheSame(
                oldItem: StoryforDatabase,
                newItem: StoryforDatabase
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryforDatabase,
                newItem: StoryforDatabase
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}