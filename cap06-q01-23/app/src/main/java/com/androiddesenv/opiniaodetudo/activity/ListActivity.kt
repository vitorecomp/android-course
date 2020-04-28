package com.androiddesenv.opiniaodetudo.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.repository.ReviewRepository

class ListActivity : AppCompatActivity(){
//    val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //botao de voltar
        val supportActionBar = this.supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //fazendo o set do layout
        setContentView(R.layout.list_review_layout)
        val listView = findViewById<ListView>(R.id.list_recyclerview)

        //carrengando a lista de itens
        val reviews = ReviewRepository.instance.listAll()
        val adapter = object : ArrayAdapter<Review>(this, -1, reviews ){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemView = getLayoutInflater().inflate(R.layout.review_list_item_layout, null)
                val item = reviews[position]
                val textViewName = itemView.findViewById<TextView>(R.id.item_name)
                val textViewReview = itemView.findViewById<TextView>(R.id.item_review)
                textViewName.text = item.name
                textViewReview.text = item.review
                return itemView
            }
        }

        listView.adapter = adapter
    }

    //action do botao de voltar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}