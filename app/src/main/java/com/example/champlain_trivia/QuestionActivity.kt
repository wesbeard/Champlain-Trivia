package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import androidx.core.view.isVisible
import java.io.OutputStreamWriter
import kotlin.random.Random


class QuestionActivity : AppCompatActivity() {

    // Text Question Views
    private lateinit var promptText: TextView
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
    private lateinit var nameEntry: EditText
    private lateinit var saveScoreButton: Button

    // Game Logic Vars
    private lateinit var selectedAnswer: RadioButton
    private lateinit var category: String
    private var questionNumber = 1
    private val TOTAL_QUESTIONS = 5
    private lateinit var questionSet: List<Question>
    private var score = 0
    private var hintUsed = false
    private var scoreSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_question)
        title = "Question $questionNumber"

        val intent = intent
        category = intent.getStringExtra("category").toString()
        val stream = resources.openRawResource(R.raw.questions)
        val rawQuestions = stream.bufferedReader().readText()
        val gson = Gson()
        val deserializedQuestions = gson.fromJson(rawQuestions, Root::class.java)

        promptText = findViewById(R.id.question_prompt)
        //promptImage = findViewById(R.id.question_image)

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
        // check for question type and set answers up accordingly
        when (questionSet[questionNumber - 1].image) {
            false -> {
                // randomize answer locations
                var answerList = listOf<RadioButton>(findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4))
                answerList = answerList.shuffled()
                correctAnswer = answerList[0]
                incorrect1 = answerList[1]
                incorrect2 = answerList[2]
                incorrect3 = answerList[3]

                // set question prompt
                promptText.text = questionSet[questionNumber - 1].prompt

                // set text question answers
                correctAnswer.text = questionSet[questionNumber - 1].answers.correct
                incorrect1.text = questionSet[questionNumber - 1].answers.incorrect[0]
                incorrect2.text = questionSet[questionNumber - 1].answers.incorrect[1]
                incorrect3.text = questionSet[questionNumber - 1].answers.incorrect[2]
            }
            true -> {
                // randomize answer locations
                var answerList = listOf<RadioButton>(findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4))
                answerList = answerList.shuffled()
                correctAnswer = answerList[0]
                incorrect1 = answerList[1]
                incorrect2 = answerList[2]
                incorrect3 = answerList[3]

                // set question prompt
                promptText.text = questionSet[questionNumber - 1].prompt

                // set text question answers
                correctAnswer.text = questionSet[questionNumber - 1].answers.correct
                incorrect1.text = questionSet[questionNumber - 1].answers.incorrect[0]
                incorrect2.text = questionSet[questionNumber - 1].answers.incorrect[1]
                incorrect3.text = questionSet[questionNumber - 1].answers.incorrect[2]
            }
        }
    }

    private fun nextQuestion() {
        // make sure an answer is selected, if not then don't go to the next question
        if (radioGroup.checkedRadioButtonId == -1)
        {
            makeToast("Please select an answer to conitnue")
            return
        }

        // if currently selected answer is the correct answer then increment score
        selectedAnswer = findViewById(radioGroup.checkedRadioButtonId)
        if (selectedAnswer == correctAnswer) {
            score++
        }

        // clear checked buttons for next question
        radioGroup.clearCheck()

        if (questionNumber >= TOTAL_QUESTIONS) {
            gameOver()
        }
        else {
            questionNumber++
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

        scoreMessage = findViewById(R.id.scoreMessage)
        scoreMessage.text = when((score/TOTAL_QUESTIONS)*100) {
            100 -> "Perfection!"
            in 75..99 -> "Well Done!"
            in 50..74 -> "Not Too Shabby..."
            in 25..49 -> "You Can Do Better!"
            in 0..24 -> "That Was Terrible!"
            else -> "Game Over!"
        }

        nameEntry = findViewById(R.id.name_entry)
        nameEntry.hint = "Name"
        saveScoreButton = findViewById(R.id.save_score)
        saveScoreButton.setOnClickListener {
            saveScore(nameEntry.text.toString())
        }

        backToMenuButton = findViewById(R.id.back_to_menu)
        backToMenuButton.setOnClickListener {
            finish()
        }

        replayButton = findViewById(R.id.replay)
        replayButton.setOnClickListener {
            val intent = newIntent(this@QuestionActivity)
            intent.putExtra("category", "general")
            startActivity(intent)
            finish()
        }
    }

    private fun saveScore(nameText: String) {
        // Make sure they can't save the score twice
        if (scoreSaved) {
            makeToast("This score has already been saved!")
        }
        // If name text is valid then write it to a file, if not show error message
        else if(nameText != null && nameText != "") {
            // Scores are saved as "category-highscores", ex: general-highscores
            val filename = "$category-highscores"
            val fileOutputStream = openFileOutput(filename, MODE_PRIVATE)
            val textOutputStream = OutputStreamWriter(fileOutputStream)

            val toWrite = "$nameText $score"
            textOutputStream.write(toWrite)
            textOutputStream.close()
            fileOutputStream.close()
            scoreSaved = true
            makeToast("Score saved!")
        }
        else {
            makeToast("Please enter a name to save your score")
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}
