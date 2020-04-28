package com.androiddesenv.opiniaodetudo

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProviders
import com.androiddesenv.opiniaodetudo.activity.ListActivity
import com.androiddesenv.opiniaodetudo.infra.model.Review
import com.androiddesenv.opiniaodetudo.infra.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.view.EditReviewViewModel

class FormFragment : Fragment() {

    private val fragments = mapOf(LIST_FRAGMENT to ::ListFragment)
    companion object {
        val LIST_FRAGMENT = "listFragment"
    }

    private lateinit var mainView: View

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

            object: AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    val repository = ReviewRepository(activity!!.applicationContext)
                    if(reviewToEdit == null){
                        repository.save(name.toString(), review.toString())
                        (activity as MainActivity).navigateTo("listFragment")
                    }else{
                        repository.update(reviewToEdit.id, name.toString(), review.toString())
                        activity!!.finish()
                    }
                }
            }.execute()
        }

        configureAutoHiddenKeyboard()

        return mainView;
    }

    private fun configureAutoHiddenKeyboard( ) {
        val mainContainer = this.activity!!.findViewById<ConstraintLayout>(R.id.create_review)
        mainContainer.setOnTouchListener{ _, _ ->
            val im = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(this.activity?.currentFocus?.windowToken, 0)
        }
    }
}
