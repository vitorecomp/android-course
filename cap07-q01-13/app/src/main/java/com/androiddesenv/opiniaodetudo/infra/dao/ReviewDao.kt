package com.androiddesenv.opiniaodetudo.infra.dao

import androidx.room.*
import com.androiddesenv.opiniaodetudo.infra.model.Review

object ReviewTableInfo{
    const val TABLE_NAME = "Review"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_REVIEW = "review"
}

@Dao
interface ReviewDao {
    @Insert
    fun save(review: Review)

    @Query("SELECT * from ${ReviewTableInfo.TABLE_NAME}")
    fun listAll():List<Review>

    @Delete
    fun delete(review: Review)

    @Update
    fun update(review: Review)
}