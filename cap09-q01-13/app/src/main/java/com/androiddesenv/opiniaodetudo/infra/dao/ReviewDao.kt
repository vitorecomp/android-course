package com.androiddesenv.opiniaodetudo.infra.dao

import androidx.room.*
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.model.ReviewTableInfo


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