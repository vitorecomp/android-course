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

    @Query("SELECT * from ${ReviewTableInfo.TABLE_NAME} WHERE ${ReviewTableInfo.COLUMN_ID} = :id")
    fun getOne(id: String):List<Review>

    @Delete
    fun delete(review: Review)

    @Update
    fun update(review: Review)

    @Query("DELETE FROM ${ReviewTableInfo.TABLE_NAME} WHERE ${ReviewTableInfo.COLUMN_ID} = :id")
    fun delete(id: String?)
}