package com.angcassa.stroyapp.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.angcassa.stroyapp.R
import com.angcassa.stroyapp.config.database.StoryDatabase
import com.angcassa.stroyapp.config.response.StoryforDatabase
import com.bumptech.glide.Glide
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ExecutionException


internal class StoryRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private val db: StoryDatabase = StoryDatabase.getDatabase(context)
    private val name = ArrayList<String>()
    private val photo = ArrayList<String>()
    private val detail = ArrayList<StoryforDatabase>()


    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        val story = db.StoryDao().getWStory()
        story.map {
            detail.add(it)
            photo.add(it.photoUrl)
            name.add(it.name)
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = photo.size

    override fun getViewAt(position: Int): RemoteViews {
        val bool = doInBackground()
        val path = photo[position]
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        val build = Glide.with(context).asBitmap().placeholder(R.drawable.ic_baseline_broken_image_24)
            .error(R.drawable.ic_baseline_broken_image_24).load(path).centerCrop().into(640, 320)
        if(bool){
            try {
                rv.setImageViewBitmap(R.id.Img_widget, build.get())
                rv.setTextViewText(R.id.tvWUser, "Uploaded by : " + name[position])
            } catch (e: ExecutionException) {
                Log.e("error", e.toString())
            }
        }else {
            rv.setImageViewResource(
                R.id.Img_widget,
                R.drawable.ic_baseline_broken_image_24
            )
            rv.setTextViewText(R.id.tvWUser, context.getText(R.string.error))
        }

        val fillInIntent = Intent()
        fillInIntent.putExtra(StoryWidget.DETAIL, detail[position].name)

        rv.setOnClickFillInIntent(R.id.Img_widget, fillInIntent)
        return rv
    }

    private fun doInBackground(): Boolean {
        return try {
            val sock = Socket()
            sock.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }

    }



    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}