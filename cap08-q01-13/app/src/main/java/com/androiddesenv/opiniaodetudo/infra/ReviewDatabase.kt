package com.androiddesenv.opiniaodetudo.infra

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.androiddesenv.opiniaodetudo.infra.dao.ReviewDao
import com.androiddesenv.opiniaodetudo.infra.model.Review

@Database(entities = arrayOf(Review::class), version = 2)
abstract class ReviewDatabase : RoomDatabase(){
    companion object {
        private var instance: ReviewDatabase? = null
        fun getInstance(context: Context): ReviewDatabase {
            if(instance == null) {
                instance = Room
                    .databaseBuilder(context, ReviewDatabase::class.java, "review_database")
                    .build()
            }
            return instance!!
        }
    }

    abstract fun reviewDao(): ReviewDao
}