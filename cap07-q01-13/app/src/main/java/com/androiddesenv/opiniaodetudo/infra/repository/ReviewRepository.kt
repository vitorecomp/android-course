package com.androiddesenv.opiniaodetudo.infra.repository

import android.content.Context
import com.androiddesenv.opiniaodetudo.infra.ReviewDatabase
import com.androiddesenv.opiniaodetudo.infra.dao.ReviewDao
import com.androiddesenv.opiniaodetudo.infra.model.Review
import java.util.*

class ReviewRepository{
    private val reviewDao: ReviewDao
    constructor(context: Context){
        val reviewDatabase = ReviewDatabase.getInstance(context)
        reviewDao = reviewDatabase.reviewDao()
    }
    fun save(name: String, review: String) {
        reviewDao.save(Review(UUID.randomUUID().toString(), name, review))
    }
    fun listAll(): List<Review> {
        return reviewDao.listAll()
    }

    fun delete(review : Review){
        return reviewDao.delete(review)
    }

    fun update(id: String, nome: String, texto: String) {
        return reviewDao.update(Review(id, nome, texto))
    }
}