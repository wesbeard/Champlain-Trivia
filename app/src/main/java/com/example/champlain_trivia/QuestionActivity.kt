package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log
import androidx.core.view.isVisible
import kotlin.random.Random


class QuestionActivity : AppCompatActivity() {

    private lateinit var promptText: TextView
    private lateinit var promptImage: ImageView
    private lateinit var submitButton: Button
    private lateinit var hintButton: Button
    private lateinit var correctAnswer: RadioButton
    private lateinit var incorrect1: RadioButton
    private lateinit var incorrect2: RadioButton
    private lateinit var incorrect3: RadioButton

    private var questionNumber = 1
    private lateinit var questionSet: List<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_question)
        title = "Question $questionNumber"

        val intent = intent
        val category = intent.getStringExtra("category").toString()
        val stream = resources.openRawResource(R.raw.questions)
        val rawQuestions = stream.bufferedReader().readText()
        val gson = Gson()
        val deserializedQuestions = gson.fromJson(rawQuestions, Root::class.java)

        promptText = findViewById(R.id.question_prompt)
        promptImage = findViewById(R.id.question_image)

        submitButton = findViewById(R.id.submit)
        submitButton.setOnClickListener {
            nextQuestion()
        }
        hintButton = findViewById(R.id.hint)
        hintButton.setOnClickListener {
            getHint()
        }

        questionSet = when (category) {
            "general" -> deserializedQuestions.categories.general.questions
            "buildings" -> deserializedQuestions.categories.buildings.questions
            "burlington" -> deserializedQuestions.categories.burlington.questions
            else -> deserializedQuestions.categories.general.questions
        }

        questionSet = questionSet.shuffled()
        // call function to generate initial question
        setQuestion()
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, QuestionActivity::class.java)
        }
    }

    private fun setQuestion() {
        // randomize answer locations
        var answerList = listOf<RadioButton>(findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4))
        answerList = answerList.shuffled()
        correctAnswer = answerList[0]
        incorrect1 = answerList[1]
        incorrect2 = answerList[2]
        incorrect3 = answerList[3]

        // set question prompt
        promptText.text = questionSet[questionNumber - 1].prompt

        // set question answers
        correctAnswer.text = questionSet[questionNumber - 1].answers.correct
        incorrect1.text = questionSet[questionNumber - 1].answers.incorrect[0]
        incorrect2.text = questionSet[questionNumber - 1].answers.incorrect[1]
        incorrect3.text = questionSet[questionNumber - 1].answers.incorrect[2]

        // make sure radio buttons are unselected
        if (correctAnswer.isChecked) {
            correctAnswer.isChecked = false
        }
        if (incorrect1.isChecked) {
            incorrect1.isChecked = false
        }
        if (incorrect2.isChecked) {
            incorrect2.isChecked = false
        }
        if (incorrect3.isChecked) {
            incorrect3.isChecked = false
        }
    }

    private fun nextQuestion() {
        questionNumber++
        title = "Question $questionNumber"
        setQuestion()
    }

    private fun getHint() {
        val rg = R.id.radio
        val randNum = Random.nextInt(0,3)
        when (randNum) {
            0 -> incorrect1.isVisible = false
            1 -> incorrect2.isVisible = false
            2 -> incorrect3.isVisible = false
        }
        // need to adjust this so it only removes one and then also they need to be added back later
        // maybe use radio group earlier so it is easier to check which one is checked for reset purposes
        // then it could also be used for removing I think
    }
}
