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

    fun save(name: String, review: String, photoPath: String?, thumbnailBytes: ByteArray?): Review {
        val entity = Review(UUID.randomUUID().toString(),
            name, review, photoPath, thumbnailBytes)
        reviewDao.save(entity)
        return entity
    }

    fun save(name: String, review: String) : Review {
        return this.save(name, review, null, null)
    }

    fun listAll(): List<Review> {
        return reviewDao.listAll()
    }

    fun getOne(id:String): Review {
        return reviewDao.getOne(id).get(index = 0)
    }

    fun delete(review : Review){
        return reviewDao.delete(review)
    }

    fun update(id: String, nome: String, texto: String?) {
        return reviewDao.update(Review(id, nome, texto))
    }

    fun update(review: Review) {
        return reviewDao.update(review)
    }

    fun updateLocation(entity: Review, lat: Double?, long: Double?) {
        entity.latitude = lat
        entity.longitude = long
        reviewDao.update(entity)
    }

    fun delete(id: String?) {
        reviewDao.delete(id)
    }

}