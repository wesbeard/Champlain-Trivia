package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val CATEGORY = ""

class QuestionActivity : AppCompatActivity() {

    private lateinit var promptText: TextView
    private lateinit var promptImage: ImageView
    private lateinit var submitButton: Button
    private lateinit var hintButton: Button
    private lateinit var correctAnswer: RadioButton
    private lateinit var incorrect1: RadioButton
    private lateinit var incorrect2: RadioButton
    private lateinit var incorrect3: RadioButton

    var questionNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_question)
        title = "Question $questionNumber"

        promptText = findViewById(R.id.question_prompt)
        promptImage = findViewById(R.id.question_image)
        submitButton = findViewById(R.id.submit)
        hintButton = findViewById(R.id.hint)

        // * These need to be dynamic *
        correctAnswer = findViewById(R.id.answer1)
        incorrect1 = findViewById(R.id.answer2)
        incorrect2 = findViewById(R.id.answer3)
        incorrect3 = findViewById(R.id.answer4)

        val intent = intent
        val category = intent.getStringExtra("category").toString()

        val stream = resources.openRawResource(R.raw.questions)
        val questions = stream.bufferedReader().readText()

        promptText.text = "> question goes here <"
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, QuestionActivity::class.java)
        }
    }
}
