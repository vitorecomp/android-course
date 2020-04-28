package com.androiddesenv.opiniaodetudo.infra

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.androiddesenv.opiniaodetudo.infra.dao.ReviewDao
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.model.ReviewTableInfo


@Database(entities = arrayOf(Review::class), version = 3)
abstract class ReviewDatabase : RoomDatabase(){
    companion object {
        private var instance: ReviewDatabase? = null
        fun getInstance(context: Context): ReviewDatabase {
            if(instance == null) {
                instance = Room
                    .databaseBuilder(context, ReviewDatabase::class.java, "review_database")
                    .addMigrations(migration_1_2)
                    .addMigrations(migration_2_3)
                    .build()
            }
            return instance!!
        }

        private var migration_1_2 = object: Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_PHOTO_PATH} TEXT")
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_THUMBNAIL} BLOB")
            }
        }

        private var migration_2_3 = object: Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_LONGITUDE} REAL")
                database.execSQL("ALTER TABLE ${ReviewTableInfo.TABLE_NAME} " +
                        "ADD COLUMN ${ReviewTableInfo.COLUMN_LATITUDE} REAL")
            }
        }
    }

    abstract fun reviewDao(): ReviewDao
}