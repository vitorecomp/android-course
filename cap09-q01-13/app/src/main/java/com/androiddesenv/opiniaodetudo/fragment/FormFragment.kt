package com.androiddesenv.opiniaodetudo

import LocationService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class FormFragment : Fragment() {

    private var file: File? = null

    companion object {
        val TAKE_PICTURE_RESULT = 101
    }

    private lateinit var mainView: View

    private var thumbnailBytes: ByteArray? = null

    private fun generateThumbnailBytes(thumbnail: Bitmap, targetSize: Int) {
        val thumbnailOutputStream = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.PNG, targetSize, thumbnailOutputStream)
        thumbnailBytes = thumbnailOutputStream.toByteArray()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{
        mainView = inflater.inflate(R.layout.new_review_form_layout, null)

        val buttonSave = mainView.findViewById<Button>(R.id.save_button)
        val textViewName = mainView.findViewById<TextView>(R.id.input_name)
        val textViewReview = mainView.findViewById<TextView>(R.id.input_review)

        val reviewToEdit = (activity!!.intent?.getParcelableExtra<Review?>("item"))?.also { review ->
            textViewName.text = review.name
            textViewReview.text = review.review
        }

        buttonSave.setOnClickListener{
            val name = textViewName.text
            val review = textViewReview.text

            object: AsyncTask<Void, Void, Review>() {
                override fun doInBackground(vararg params: Void?) : Review {
                    val repository = ReviewRepository(activity!!.applicationContext)
                    return repository.save(name.toString(), review.toString(),
                        file?.toRelativeString(activity!!.filesDir), thumbnailBytes)
                }
                override fun onPostExecute(review: Review) {
                    updateReviewLocation(review)
                    (activity as MainActivity).navigateTo(MainActivity.LIST_FRAGMENT)
                }
            }.execute()
        }

        configureAutoHiddenKeyboard()
        configurePhotoClick()

        return mainView;
    }

    private fun updateReviewLocation(entity: Review) {
        LocationService(activity!!).onLocationObtained{ lat,long ->
            val repository = ReviewRepository(activity!!.applicationContext)
            object: AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    repository.updateLocation(entity, lat, long)
                }
            }.execute()
        }
    }

    private fun configureAutoHiddenKeyboard( ) {
        val mainContainer = this.activity!!.findViewById<ConstraintLayout>(R.id.create_review)
        mainContainer.setOnTouchListener{ _, _ ->
            val im = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(this.activity?.currentFocus?.windowToken, 0)
        }
    }

    private fun configurePhotoClick() {
        mainView.findViewById<ImageView>(R.id.imageView).setOnClickListener {
            val fileName = "${System.nanoTime()}.jpg"
            file = File(activity!!.filesDir, fileName)
            val uri = FileProvider.getUriForFile(activity!!,
                "com.androiddesenv.opiniaodetudo.fileprovider", file!!)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, TAKE_PICTURE_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TAKE_PICTURE_RESULT){
            if(resultCode == Activity.RESULT_OK){
                val photoView = mainView.findViewById<ImageView>(R.id.imageView)
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                val targetSize = 100
                val thumbnail = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    targetSize,
                    targetSize
                )
                photoView.setImageBitmap(thumbnail)

                mainView.findViewById<TextView>(R.id.foto_label).visibility = View.VISIBLE
                generateThumbnailBytes(thumbnail, targetSize)
            }else{
                Toast.makeText(activity, "Erro ao tirar a foto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
