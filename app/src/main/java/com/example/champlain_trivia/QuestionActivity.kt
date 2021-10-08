package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import androidx.core.view.isVisible
import kotlin.random.Random


class QuestionActivity : AppCompatActivity() {

    // Text Question Views
    private lateinit var promptText: TextView
    private lateinit var promptImage: ImageView
    private lateinit var submitButton: Button
    private lateinit var hintButton: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var correctAnswer: RadioButton
    private lateinit var incorrect1: RadioButton
    private lateinit var incorrect2: RadioButton
    private lateinit var incorrect3: RadioButton

    // Game Over Views
    private lateinit var scoreDisplay: TextView
    private lateinit var scoreMessage: TextView
    private lateinit var backToMenuButton: Button
    private lateinit var replayButton: Button

    // Game Logic Vars
    private lateinit var selectedAnswer: RadioButton
    private var questionNumber = 1
    private val TOTAL_QUESTIONS = 5
    private lateinit var questionSet: List<Question>
    private var score = 0
    private var hintUsed = false

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

        radioGroup = findViewById(R.id.radioGroup)

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
    }

    private fun nextQuestion() {
        // make sure an answer is selected, if not then don't go to the next question
        if (radioGroup.checkedRadioButtonId == -1)
        {
            Toast.makeText(applicationContext, "Please select an answer", Toast.LENGTH_SHORT).show()
            return
        }

        // if currently selected answer is the correct answer then increment score
        selectedAnswer = findViewById(radioGroup.checkedRadioButtonId)
        if (selectedAnswer == correctAnswer) {
            score++
        }

        // clear checked buttons for next question
        radioGroup.clearCheck()

        // increment question #, if last question go to game over screen
        questionNumber++
        if (questionNumber >= TOTAL_QUESTIONS) {
            gameOver()
        }
        else {

            title = "Question $questionNumber"
            setQuestion()

            // show all answers if hint has been used
            if (hintUsed) {
                incorrect1.isVisible = true
                incorrect2.isVisible = true
                incorrect3.isVisible = true
            }
        }
    }

    private fun getHint() {
        // You get one hint per round so we just need to reset this whenever we go back to home
        if (!hintUsed) {
            val rg = R.id.radio
            when (Random.nextInt(0, 3)) {
                0 -> incorrect1.isVisible = false
                1 -> incorrect2.isVisible = false
                2 -> incorrect3.isVisible = false
            }
            hintUsed = true
            hintButton.setBackgroundColor(resources.getColor(R.color.disabled))
            hintButton.isClickable = false
        }
    }

    private fun gameOver() {
        setContentView(R.layout.activity_finish)
        title  = "Game Over"
        scoreDisplay = findViewById(R.id.scoreDisplay)
        scoreDisplay.text = "$score/$TOTAL_QUESTIONS"
    }
}
