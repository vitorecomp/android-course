package com.androiddesenv.opiniaodetudo.fragment

import EditDialogFragment
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.androiddesenv.opiniaodetudo.MainActivity
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.view.EditReviewViewModel
import com.github.chrisbanes.photoview.PhotoView
import java.io.File
import java.util.*

class ShowReviewFragment: Fragment() {

    private lateinit var mainView: View
    private lateinit var Review : MutableList<Review>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View{

        mainView = inflater.inflate(R.layout.show_review, null)

        val reviewViewModel = ViewModelProviders.of(requireActivity()).get(EditReviewViewModel::class.java)
        val review = reviewViewModel?.data?.value

        val imageView = mainView.findViewById<ImageView>(R.id.expandedImage)

        configureMenu(review!!)

        configureView(imageView, review)

        return mainView
    }

    private fun configureView(imageView: ImageView, review: Review) {
        review?.photoPath?.apply {
            val bitmap = BitmapFactory.decodeFile(File(requireActivity().filesDir, this).absolutePath)
            imageView.setImageBitmap(bitmap)
            configureImageClick(imageView, bitmap)
        }

        val toolbar = mainView.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = review?.name

        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val textView = mainView.findViewById<TextView>(R.id.review)
        textView.text = review?.review
    }


    private fun configureMenu(review: Review){
        val listButton = mainView.findViewById<ImageButton>(R.id.item_list_edit)
        listButton.setOnClickListener{
            this.openItemForEdition(review)
        }

        val deleteButton = mainView.findViewById<ImageButton>(R.id.item_list_delete)
        deleteButton.setOnClickListener{
            this.askForDelete(review)
        }

        val mapButton = mainView.findViewById<ImageButton>(R.id.item_list_map)
        mapButton.setOnClickListener{
            this.openMap(review)
        }

        val shareButton = mainView.findViewById<ImageButton>(R.id.item_list_share)
        shareButton.setOnClickListener{
            this.share(review)
        }
    }

    private fun share(review: Review) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/jpg"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Opinião")
        intent.putExtra(Intent.EXTRA_TEXT, "${review.name} = ${review.review}")
        if(review.photoPath != null) {
            val file = File(requireActivity().filesDir, review.photoPath)
            val uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.androiddesenv.opiniaodetudo.fileprovider",
                file
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(intent)
    }

    private fun openMap(review: Review) {
        if(review.latitude == null || review.longitude == null){
            android.app.AlertDialog.Builder(activity)
                .setMessage(R.string.not_location_message)
                .setPositiveButton(R.string.string_ok) { _, _ ->
                }
                .create()
                .show()
        }else{
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val allAddress = geocoder.getFromLocation(review.latitude!!, review.longitude!!, 1)
            for (address in allAddress) {
                Log.d("GEOCODER", address.toString())
            }
            lateinit var uri : Uri
            if(allAddress.size > 0)
                uri = Uri.parse("geo:0,0?q=${allAddress[0].getAddressLine(0)}")
            else
                uri = Uri.parse("geo:${review.latitude},${review.longitude}")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            requireActivity().startActivity(intent)
        }
    }

    private fun askForDelete(item: Review) {
        android.app.AlertDialog.Builder(activity)
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

    private fun configureImageClick(imageView: ImageView, bitmap: Bitmap?) {
        imageView.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.image_dialog, null)
            val photoView : PhotoView = dialogView.findViewById(R.id.imageView)

            photoView.setImageBitmap(bitmap)
            builder.setView(photoView)
            builder.create().show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun openItemForEdition(item: Review) {
        val reviewViewModel = ViewModelProviders.of(requireActivity()).get(EditReviewViewModel::class.java)
        val data = reviewViewModel.data
        data.value = item
        EditDialogFragment().show(requireFragmentManager(), "edit_dialog")
    }

    private fun delete(item: Review) {
        object: AsyncTask<Void, Void, Review>(){
            override fun doInBackground(vararg params: Void?) : Review{
                ReviewRepository(requireActivity().applicationContext)
                    .delete(item)
                return item
            }
            override fun onPostExecute(item: Review?) {
                val fm = requireActivity().supportFragmentManager
                if (fm.backStackEntryCount > 0) {
                    Log.i("MainActivity", "popping backstack")
                    fm.popBackStack()
                }else{
                    Log.i("MainActivity", "popping backstack")
                    (requireActivity() as MainActivity).navigateWithBackStack(ShowReviewFragment())
                }
            }
        }.execute()
    }
}