import android.app.Activity
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
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.androiddesenv.opiniaodetudo.FormFragment
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.view.EditReviewViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class EditDialogFragment : DialogFragment(){
    private lateinit var mainView: View
    private var file: File? = null
    private var thumbnailBytes: ByteArray? = null

    companion object {
        const val TAKE_PICTURE_RESULT = 103
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.new_review_form_layout, null)
        populateView(mainView)
        configureSaveButton(mainView)
        configurePhotoClick()
        return mainView
    }

    private fun configurePhotoClick() {
        mainView.findViewById<ImageView>(R.id.imageView).setOnClickListener {
            val fileName = "${System.nanoTime()}.jpg"
            file = File(requireActivity().filesDir, fileName)
            val uri = FileProvider.getUriForFile(requireActivity(),
                "com.androiddesenv.opiniaodetudo.fileprovider", file!!)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, EditDialogFragment.TAKE_PICTURE_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == EditDialogFragment.TAKE_PICTURE_RESULT){
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

    private fun configureSaveButton(view: View) {
        val textName = view.findViewById<EditText>(R.id.input_name)
        val textReview = view.findViewById<EditText>(R.id.input_review)
        val button = view.findViewById<Button>(R.id.save_button)
        val viewModel = ViewModelProviders.of(requireActivity()).get(EditReviewViewModel::class.java)
        var review = viewModel.data.value!!
        button.setOnClickListener {
            val review = Review(review.id, textName.text.toString(), textReview.text.toString(),
                file?.toRelativeString(requireActivity().filesDir), thumbnailBytes)

            object: AsyncTask<Void, Void, Review>() {
                override fun doInBackground(vararg params: Void?) : Review {
                    ReviewRepository(requireActivity().applicationContext).update(review)
                    return review
                }
                override fun onPostExecute(review: Review) {
                    viewModel.data.value = review
                    this@EditDialogFragment.dismiss()
                }
            }.execute()

        }
    }

    private fun generateThumbnailBytes(thumbnail: Bitmap, targetSize: Int) {
        val thumbnailOutputStream = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.PNG, targetSize, thumbnailOutputStream)
        thumbnailBytes = thumbnailOutputStream.toByteArray()
    }

    private fun populateView(view: View) {
        val review = ViewModelProviders.of(requireActivity()).get(EditReviewViewModel::class.java).data.value
        view.findViewById<EditText>(R.id.input_name).setText(review!!.name)
        view.findViewById<EditText>(R.id.input_review).setText(review!!.review)
    }
    override fun onResume() {
        val params = dialog!!.window!!.attributes.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        dialog!!.window!!.attributes = params
        super.onResume()
    }
}