import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.androiddesenv.opiniaodetudo.R
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.view.EditReviewViewModel

class EditDialogFragment : DialogFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_review_form_layout, null)
        populateView(view)
        configureSaveButton(view)
        return view
    }
    private fun configureSaveButton(view: View) {
        val textName = view.findViewById<EditText>(R.id.input_name)
        val textReview = view.findViewById<EditText>(R.id.input_review)
        val button = view.findViewById<Button>(R.id.save_button)
        val viewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
        var review = viewModel.data.value!!
        button.setOnClickListener {
            val review = Review(review.id, textName.text.toString(), textReview.text.toString())

            object: AsyncTask<Void, Void, Review>() {
                override fun doInBackground(vararg params: Void?) : Review {
                    ReviewRepository(activity!!.applicationContext).update(review)
                    return review
                }
                override fun onPostExecute(review: Review) {
                    viewModel.data.value = review
                    this@EditDialogFragment.dismiss()
                }
            }.execute()

        }
    }
    private fun populateView(view: View) {
        val review = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java).data.value
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