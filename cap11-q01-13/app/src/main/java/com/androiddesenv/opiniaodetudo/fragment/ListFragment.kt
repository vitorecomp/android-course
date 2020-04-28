package com.androiddesenv.opiniaodetudo.fragment

import EditDialogFragment
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.androiddesenv.opiniaodetudo.MainActivity
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.view.EditReviewViewModel
import java.lang.ref.WeakReference
import java.util.*

class ListFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var reviews : MutableList<Review>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{
        rootView = inflater.inflate(R.layout.list_review_layout, null)

        reviews = emptyList<Review>().toMutableList()
        LoadList(this).execute()

        val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
        configureLongClick(listView)
        configureListObserver()

        return rootView
    }

    private fun configureLongClick(listView : ListView){
        listView.setOnItemClickListener { parent, view, position, id ->
            val reviewViewModel =
                ViewModelProviders.of(requireActivity()).get(EditReviewViewModel::class.java)
            val data = reviewViewModel.data
            data.value = reviews[position]
            (requireActivity() as MainActivity).navigateWithBackStack(ShowReviewFragment())
        }
    }


    private inner class LoadList
    internal constructor(context: ListFragment) : AsyncTask<Void, Void, List<Review>>() {

        override fun doInBackground(vararg params: Void?): List<Review> {
            return ReviewRepository(activity!!.applicationContext).listAll()
        }

        override fun onPostExecute(dbReviews: List<Review>) {
            //activity
            reviews.clear()
            reviews.addAll(dbReviews)

            // modify the activity's UI
            val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
            val adapter = object : ArrayAdapter<Review>(activity!!, -1, reviews ){
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val itemView = layoutInflater.inflate(R.layout.review_list_item_layout, null)
                    val item = reviews[position]
                    val textViewName = itemView.findViewById<TextView>(R.id.item_name)
                    val textViewReview = itemView.findViewById<TextView>(R.id.item_review)

                    if(item.thumbnail != null){
                        val thumbnail = itemView.findViewById<ImageView>(R.id.thumbnail)
                        val bitmap = BitmapFactory.decodeByteArray(item.thumbnail, 0, item.thumbnail.size)
                        thumbnail.setImageBitmap(bitmap)
                    }

                    textViewName.text = item.name
                    textViewReview.text = item.review
                    return itemView
                }
            }
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        object : AsyncTask<Unit, Void, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                if(activity != null) {
                    reviews?.clear()
                    reviews?.addAll(ReviewRepository(requireActivity().applicationContext).listAll())
                }
            }
            override fun onPostExecute(result: Unit?) {
                val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    private fun configureListObserver() {
        val reviewViewModel = ViewModelProviders.of(requireActivity()).get(EditReviewViewModel::class.java)
        reviewViewModel.data.observe(requireActivity(), Observer {
            onResume()
        })
    }
}