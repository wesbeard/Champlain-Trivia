package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import androidx.core.view.isVisible
import java.io.OutputStreamWriter
import kotlin.random.Random


class QuestionActivity : AppCompatActivity() {

    // question views
    private lateinit var promptText: TextView
    private lateinit var submitButton: Button
    private lateinit var hintButton: Button

    // Text Question Views
    private lateinit var radioGroup: RadioGroup
    private lateinit var correctTextAnswer: RadioButton
    private lateinit var incorrectText1: RadioButton
    private lateinit var incorrectText2: RadioButton
    private lateinit var incorrectText3: RadioButton

    // Image Question Views
    private lateinit var imageTable: TableLayout
    private lateinit var correctImage: ImageButton
    private lateinit var incorrectImage1: ImageButton
    private lateinit var incorrectImage2: ImageButton
    private lateinit var incorrectImage3: ImageButton

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

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, QuestionActivity::class.java)
        }
    }

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

        submitButton = findViewById(R.id.submit)
        submitButton.setOnClickListener {
            nextQuestion()
        }
        hintButton = findViewById(R.id.hint)
        hintButton.setOnClickListener {
            getHint()
        }

        // get text question radio group
        radioGroup = findViewById(R.id.radioGroup)
        // get image question table
        imageTable = findViewById(R.id.imageTable)

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

    private fun setQuestion() {
        // make sure old question views are removed
        radioGroup.visibility = View.GONE
        imageTable.visibility = View.GONE

        // check for question type and set answers up accordingly
        when (questionSet[questionNumber - 1].image) {
            false -> {
                // set text elements to be visible
                radioGroup.visibility = View.VISIBLE

                // randomize answer locations
                var answerList = listOf<RadioButton>(findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4))
                answerList = answerList.shuffled()
                correctTextAnswer = answerList[0]
                incorrectText1 = answerList[1]
                incorrectText2 = answerList[2]
                incorrectText3 = answerList[3]

                // set question prompt
                promptText.text = questionSet[questionNumber - 1].prompt

                // set text question answers
                correctTextAnswer.text = questionSet[questionNumber - 1].answers.correct
                incorrectText1.text = questionSet[questionNumber - 1].answers.incorrect[0]
                incorrectText2.text = questionSet[questionNumber - 1].answers.incorrect[1]
                incorrectText3.text = questionSet[questionNumber - 1].answers.incorrect[2]
            }
            true -> {
                // set image elements to be visible
                imageTable.visibility = View.VISIBLE

                // randomize answer locations
                var answerList = listOf<ImageButton>(findViewById(R.id.imageButton), findViewById(R.id.imageButton2), findViewById(R.id.imageButton3), findViewById(R.id.imageButton4))
                answerList = answerList.shuffled()
                correctImage = answerList[0]
                incorrectImage1 = answerList[1]
                incorrectImage2 = answerList[2]
                incorrectImage3 = answerList[3]

                // set question prompt
                promptText.text = questionSet[questionNumber - 1].prompt

                // set image question answers
                correctImage.setBackgroundResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.correct, "drawable", packageName))
                incorrectImage1.setBackgroundResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.incorrect[0], "drawable", packageName))
                incorrectImage2.setBackgroundResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.incorrect[1], "drawable", packageName))
                incorrectImage3.setBackgroundResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.incorrect[2], "drawable", packageName))
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
        if (selectedAnswer == correctTextAnswer) {
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
                incorrectText1.isVisible = true
                incorrectText2.isVisible = true
                incorrectText3.isVisible = true
            }
        }
    }

    private fun getHint() {
        // You get one hint per round so we just need to reset this whenever we go back to home
        if (!hintUsed) {
            when (Random.nextInt(0, 3)) {
                0 -> incorrectText1.isVisible = false
                1 -> incorrectText2.isVisible = false
                2 -> incorrectText3.isVisible = false
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
