package com.androiddesenv.opiniaodetudo.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.androiddesenv.opiniaodetudo.MainActivity
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import java.lang.ref.WeakReference

class ListActivity : AppCompatActivity(){
//    val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var reviews : MutableList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //botao de voltar
        val supportActionBar = this.supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //fazendo o set do layout
        setContentView(R.layout.list_review_layout)
        LoadList(this).execute()

        val listView = findViewById<ListView>(R.id.list_recyclerview)
        configureLongClick(listView)
    }

    private fun configureLongClick(listView : ListView){
        listView.setOnItemLongClickListener { _, view, position, _ ->
            val popupMenu = PopupMenu(this@ListActivity, view)
            popupMenu.inflate(R.menu.list_review_item_menu)
            popupMenu.setOnMenuItemClickListener {
                val item = reviews[position]
                when(it.itemId){
                    R.id.item_list_delete -> this@ListActivity.delete(reviews[position])
                    R.id.item_list_edit -> this@ListActivity.openItemForEdition(reviews[position])
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    private fun openItemForEdition(review: Review) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("item", review)
        startActivity(intent)
    }


    private class LoadList
    internal constructor(context: ListActivity) : AsyncTask<Void, Void, List<Review>>() {

        private val activityReference: WeakReference<ListActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Void?): List<Review> {
            val activity = activityReference.get()
            if (activity == null || activity.isFinishing) return emptyList()

            return ReviewRepository(activity.applicationContext).listAll()
        }

        override fun onPostExecute(reviews: List<Review>) {

            val activity = activityReference.get()
            if (activity == null || activity.isFinishing) return

            //activity
            activity.reviews = reviews.toMutableList()

            // modify the activity's UI
            val listView = activity.findViewById<ListView>(R.id.list_recyclerview)
            val layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val adapter = object : ArrayAdapter<Review>(activity, -1, reviews ){
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                    val itemView = layoutInflater.inflate(R.layout.review_list_item_layout, null)
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
    }

    private fun delete(item: Review) {
        object: AsyncTask<Void, Void, Review>(){
            override fun doInBackground(vararg params: Void?) : Review{
                ReviewRepository(this@ListActivity.applicationContext)
                    .delete(item)
                reviews.remove(item)
                return item
            }
            override fun onPostExecute(item: Review?) {
//                val listView = findViewById<ListView>(R.id.list_recyclerview)
//                val adapter = listView.adapter as ArrayAdapter<Review>
//                adapter.notifyDataSetChanged()

                val listView = findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.remove(item)
            }
        }.execute()
    }

    override fun onRestart() {
        super.onRestart()
        object : AsyncTask<Unit, Void, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                this@ListActivity.reviews = ReviewRepository(this@ListActivity.applicationContext).listAll().toMutableList()
            }
            override fun onPostExecute(result: Unit?) {
                val listView = findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    //action do botao de voltar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}