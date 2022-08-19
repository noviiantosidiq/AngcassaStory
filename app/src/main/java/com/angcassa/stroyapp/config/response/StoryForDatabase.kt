package com.angcassa.stroyapp.config.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Story")
data class StoryforDatabase(
    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("description")
    val description: String,
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("lon")
    val lon: Double
): Parcelable
