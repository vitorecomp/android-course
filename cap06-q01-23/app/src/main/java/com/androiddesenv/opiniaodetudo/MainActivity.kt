package com.androiddesenv.opiniaodetudo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.androiddesenv.opiniaodetudo.activity.ListActivity
import com.androiddesenv.opiniaodetudo.repository.ReviewRepository

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_list_reviews){
            startActivity(Intent(this, ListActivity::class.java))
            return true
        }
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSave = findViewById<Button>(R.id.save_button)
        val textViewName = findViewById<TextView>(R.id.input_name)
        val textViewReview = findViewById<TextView>(R.id.input_review)

        buttonSave.setOnClickListener{
            val name = textViewName.text
            val review = textViewReview.text
            Toast.makeText(this, "Nome:$name - Opinião:$review", Toast.LENGTH_LONG).show();
            ReviewRepository.instance.save(name.toString(), review.toString())

            startActivity(Intent(this, ListActivity::class.java))
        }

        val mainContainer = findViewById<ConstraintLayout>(R.id.create_review)
        mainContainer.setOnTouchListener{ _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}
