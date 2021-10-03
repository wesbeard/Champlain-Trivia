package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val CATEGORY = ""

class QuestionActivity : AppCompatActivity() {

    private lateinit var promptText: TextView
    private lateinit var promptImage: ImageView
    var questionNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_question)
        title = "Question $questionNumber"

        val intent = intent
        val category = intent.getStringExtra("category").toString()

        val stream = resources.openRawResource(R.raw.questions)
        val questions = stream.bufferedReader().readText()

        promptText = findViewById(R.id.question_prompt)
        promptImage = findViewById(R.id.question_image)

        when (category) {
            "general" -> {
                promptText.text = category
            }
            "buildings" -> {
                promptText.text = category
            }
            "burlington" -> {
                promptText.text = category
            }
            else -> {
                promptText.text = "category not recognized"
            }
        }
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, QuestionActivity::class.java)
        }
    }
}
