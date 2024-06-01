package com.example.garasee.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity (tableName = "History")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null,

    @ColumnInfo(name = "imageUrl")
    var imageUrl: String? = null,

    @ColumnInfo(name = "result")
    var result: String? = null,

    ) : Parcelable