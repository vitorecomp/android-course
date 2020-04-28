package com.androiddesenv.opiniaodetudo

import LocationService
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.google.android.gms.common.util.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FormFragment : Fragment() {

    private var file: File? = null
    private var thumbnailBytes: ByteArray? = null

    companion object {
        const val TAKE_PICTURE_RESULT = 101
        const val NEW_REVIEW_MESSAGE_ID = 4584
    }

    private lateinit var mainView: View

    private var lat : Double? = null
    private var long : Double? = null

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

        handleImageShare()

        //make a logic to get the temp location here
        getTempReviewLocation()

        buttonSave.setOnClickListener{
            val name = textViewName.text
            val review = textViewReview.text

            object: AsyncTask<Void, Void, Review>() {
                override fun doInBackground(vararg params: Void?) : Review {
                    val repository = ReviewRepository(activity!!.applicationContext)
                    val review = repository.save(name.toString(), review.toString(),
                        file?.toRelativeString(activity!!.filesDir), thumbnailBytes)
                    return review
                }
                override fun onPostExecute(review: Review) {
                    showReviewNotification(review)
                    if(lat != null && long != null) {
                        val repository = ReviewRepository(requireActivity())
                        object : AsyncTask<Void, Void, Unit>() {
                            override fun doInBackground(vararg params: Void?) {
                                repository.updateLocation(review, lat, long)
                            }
                        }.execute()
                    }else
                        (activity as MainActivity).updateReviewLocation(review)
                    (activity as MainActivity).selectMenu(MainActivity.LIST_FRAGMENT)
                }
            }.execute()
        }

        configureAutoHiddenKeyboard()
        configurePhotoClick()

        return mainView;
    }

    private fun getTempReviewLocation() {
        LocationService(requireActivity()).onLocationObtained{ lat,long ->
            this.lat = lat
            this.long = long
        }
    }


    private fun handleImageShare() {
        val intentParam = requireActivity().intent
        if(intentParam?.action == Intent.ACTION_SEND) {
            intentParam?.extras!!.get(Intent.EXTRA_SUBJECT)?.let {
                mainView.findViewById<EditText>(R.id.input_name).setText(it as String)
            }
            intentParam?.extras!!.get(Intent.EXTRA_TEXT)?.let {
                mainView.findViewById<EditText>(R.id.input_review).setText(it as String)
            }
            intentParam?.extras!!.get(Intent.EXTRA_STREAM)?.let {
                val fileName = "${System.nanoTime()}.jpg"
                file = File(requireActivity().filesDir, fileName)
                IOUtils.copyStream(
                    requireActivity().contentResolver.openInputStream(it as Uri),
                    FileOutputStream(file)
                )
                val photoView = mainView.findViewById<ImageView>(R.id.imageView)
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                val targetSize = 100

                val thumbnail = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    targetSize,
                    targetSize
                )
                photoView.setImageBitmap(thumbnail)
                generateThumbnailBytes(thumbnail, targetSize)
            }
        }
    }

    private fun configureAutoHiddenKeyboard( ) {
        val mainContainer = requireActivity().findViewById<ConstraintLayout>(R.id.create_review)
        mainContainer.setOnTouchListener{ _, _ ->
            val im = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            im?.hideSoftInputFromWindow(this.activity?.currentFocus?.windowToken, 0) == true
        }
    }

    private fun configurePhotoClick() {
        mainView.findViewById<ImageView>(R.id.imageView).setOnClickListener {
            val fileName = "${System.nanoTime()}.jpg"
            file = File(requireActivity().filesDir, fileName)
            val uri = FileProvider.getUriForFile(requireActivity(),
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

    private fun showReviewNotification(review: Review) {
        val builder = NotificationCompat.Builder(
            requireActivity(),
            MainActivity.PUSH_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_new_foreground)
            .setContentTitle("Nova opinião")
            .setContentText(review.name)
            .setAutoCancel(true)

        val deleteIntent = Intent(requireActivity(), MainActivity::class.java)
        deleteIntent.action = MainActivity.DELETE_NOTIFICATION_ACTION_NAME
        deleteIntent.putExtra(MainActivity.DELETE_NOTIFICATION_EXTRA_NAME, review.id)

        val deletePendingIntent: PendingIntent =
            PendingIntent.getActivity(
                requireActivity(),
                MainActivity.NEW_REVIEW_NOTIFICATION_MESSAGE_REQUEST,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        builder.addAction(0, "Apagar", deletePendingIntent)

        if(review.thumbnail != null){
            val thumbnail =
                BitmapFactory
                    .decodeByteArray(review.thumbnail, 0, review.thumbnail!!.size)
            val photo =
                BitmapFactory
                    .decodeFile(File(requireActivity().filesDir, review.photoPath).absolutePath)
            builder.setLargeIcon(thumbnail)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(photo)
                    .bigLargeIcon(null)
            )
        }else{
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(review.name)
                    .bigText(review.review)
            )
        }
        NotificationManagerCompat
            .from(requireActivity()).notify(NEW_REVIEW_MESSAGE_ID, builder.build())
    }
}
