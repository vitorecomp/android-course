package com.androiddesenv.opiniaodetudo.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.androiddesenv.opiniaodetudo.infra.model.Review

class EditReviewViewModel : ViewModel() {
    var data: MutableLiveData<Review> = MutableLiveData()


}