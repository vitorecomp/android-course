package com.androiddesenv.opiniaodetudo.fragment

import EditDialogFragment
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.view.EditReviewViewModel
import java.lang.ref.WeakReference

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
        listView.setOnItemLongClickListener { _, view, position, _ ->
            val popupMenu = PopupMenu(activity!!, view)
            popupMenu.inflate(R.menu.list_review_item_menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.item_list_delete -> askForDelete(reviews[position])
                    R.id.item_list_edit -> this.openItemForEdition(reviews[position])
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    private fun openItemForEdition(item: Review) {
        val reviewViewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
        val data = reviewViewModel.data
        data.value = item
        EditDialogFragment().show(fragmentManager!!, "edit_dialog")
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
                    textViewName.text = item.name
                    textViewReview.text = item.review
                    return itemView
                }
            }
            listView.adapter = adapter
        }
    }

    private fun askForDelete(item: Review) {
        AlertDialog.Builder(activity)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.string_ok) { _, _ ->
                this.delete(item)
            }
            .setNegativeButton(R.string.string_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun delete(item: Review) {
        object: AsyncTask<Void, Void, Review>(){
            override fun doInBackground(vararg params: Void?) : Review{
                ReviewRepository(this@ListFragment.activity!!.applicationContext)
                    .delete(item)
                reviews.remove(item)
                return item
            }
            override fun onPostExecute(item: Review?) {
                val listView = this@ListFragment.rootView.findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.remove(item)
            }
        }.execute()
    }

    override fun onResume() {
        super.onResume()
        object : AsyncTask<Unit, Void, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                this@ListFragment.reviews.clear()
                this@ListFragment.reviews.addAll(ReviewRepository(activity!!.applicationContext).listAll())
            }
            override fun onPostExecute(result: Unit?) {
                val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    private fun configureListObserver() {
        val reviewViewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
        reviewViewModel.data.observe(this@ListFragment.activity!!, Observer {
            onResume()
        })
    }
}