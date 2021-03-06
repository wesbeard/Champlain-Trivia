package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import androidx.core.view.isVisible
import java.io.OutputStreamWriter
import java.util.*
import kotlin.random.Random


class QuizActivity : AppCompatActivity() {

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
    private lateinit var textAnswerList: List<RadioButton>

    // Image Question Views
    private lateinit var imageTable: TableLayout
    private lateinit var correctImage: ImageButton
    private lateinit var incorrectImage1: ImageButton
    private lateinit var incorrectImage2: ImageButton
    private lateinit var incorrectImage3: ImageButton
    private lateinit var imageAnswerList: List<ImageButton>
    private var isImageSelected = false
    private var isImageAnswerCorrect = false

    // Game Over Views
    private lateinit var scoreDisplay: TextView
    private lateinit var scoreMessage: TextView
    private lateinit var backToMenuButton: Button
    private lateinit var replayButton: Button
    private lateinit var nameEntry: EditText
    private lateinit var saveScoreButton: Button

    // Game Logic Vars
    private var isImageQuestion = false
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
            return Intent(packageContext, QuizActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
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

        // set answer lists
        textAnswerList = listOf<RadioButton>(findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4))
        imageAnswerList = listOf<ImageButton>(findViewById(R.id.imageButton), findViewById(R.id.imageButton2), findViewById(R.id.imageButton3), findViewById(R.id.imageButton4))

        // call function to generate initial question
        setQuestion()

        // need to set up an onclick listener for the image buttons
        // use champlain green for background color of images
    }

    private fun setQuestion() {
        // make sure old question views are removed
        radioGroup.visibility = View.GONE
        imageTable.visibility = View.GONE

        isImageQuestion = questionSet[questionNumber - 1].image

        // check for question type and set answers up accordingly
        if (isImageQuestion) {
            // set image elements to be visible
            imageTable.visibility = View.VISIBLE

            // randomize answer locations
            imageAnswerList = imageAnswerList.shuffled()
            correctImage = imageAnswerList[0]
            incorrectImage1 = imageAnswerList[1]
            incorrectImage2 = imageAnswerList[2]
            incorrectImage3 = imageAnswerList[3]

            // set question prompt
            promptText.text = questionSet[questionNumber - 1].prompt

            // set image question answers
            correctImage.setImageResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.correct, "drawable", packageName))
            incorrectImage1.setImageResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.incorrect[0], "drawable", packageName))
            incorrectImage2.setImageResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.incorrect[1], "drawable", packageName))
            incorrectImage3.setImageResource(resources.getIdentifier(questionSet[questionNumber - 1].answers.incorrect[2], "drawable", packageName))

            // maybe set click listeners here
            correctImage.setOnClickListener {
                selectImage(correctImage, true)
            }
            incorrectImage1.setOnClickListener {
                selectImage(incorrectImage1, false)
            }
            incorrectImage2.setOnClickListener {
                selectImage(incorrectImage2, false)
            }
            incorrectImage3.setOnClickListener {
                selectImage(incorrectImage3, false)
            }
        }
        else {
            // set text elements to be visible
            radioGroup.visibility = View.VISIBLE

            // randomize answer locations
            textAnswerList = textAnswerList.shuffled()
            correctTextAnswer = textAnswerList[0]
            incorrectText1 = textAnswerList[1]
            incorrectText2 = textAnswerList[2]
            incorrectText3 = textAnswerList[3]

            // set question prompt
            promptText.text = questionSet[questionNumber - 1].prompt

            // set text question answers
            correctTextAnswer.text = questionSet[questionNumber - 1].answers.correct
            incorrectText1.text = questionSet[questionNumber - 1].answers.incorrect[0]
            incorrectText2.text = questionSet[questionNumber - 1].answers.incorrect[1]
            incorrectText3.text = questionSet[questionNumber - 1].answers.incorrect[2]
        }
    }

    private fun nextQuestion() {
        var correctSound = MediaPlayer.create(this, R.raw.correct)
        val incorrectSound = MediaPlayer.create(this, R.raw.incorrect)

        // make sure an answer is selected, if not then don't go to the next question
        if (radioGroup.checkedRadioButtonId == -1 && !isImageSelected) // need to add and or for if an image is selected
        {
            makeToast("Please select an answer to continue")
            return
        }

        if (isImageQuestion) {
            if (isImageAnswerCorrect) {
                correctSound.start()
                score++
            }
            else {
                incorrectSound.start()
            }

            // remove background color of image
            correctImage.setBackgroundColor(resources.getColor(R.color.transparent))
            incorrectImage1.setBackgroundColor(resources.getColor(R.color.transparent))
            incorrectImage2.setBackgroundColor(resources.getColor(R.color.transparent))
            incorrectImage3.setBackgroundColor(resources.getColor(R.color.transparent))
            // reset is image vars to false
            isImageAnswerCorrect = false
            isImageSelected = false
        }
        else {
            selectedAnswer = findViewById(radioGroup.checkedRadioButtonId)
            if (selectedAnswer == correctTextAnswer) {
                correctSound.start()
                score++
            }
            else {
                incorrectSound.start()
            }
            // clear checked buttons for next question
            radioGroup.clearCheck()
        }

        if (questionNumber >= TOTAL_QUESTIONS) {
            gameOver()
        }
        else {
            questionNumber++
            title = "Question $questionNumber"
            setQuestion()

            // show all answers if hint has been used
            if (hintUsed) {
                if (isImageQuestion) {
                    incorrectImage1.isVisible = true
                    incorrectImage2.isVisible = true
                    incorrectImage3.isVisible = true
                } else {
                    incorrectText1.isVisible = true
                    incorrectText2.isVisible = true
                    incorrectText3.isVisible = true
                }
            }
        }
    }

    private fun selectImage(selectedImage: ImageButton, correct: Boolean) {
        // remove any prior selected images
        correctImage.setBackgroundColor(resources.getColor(R.color.transparent))
        incorrectImage1.setBackgroundColor(resources.getColor(R.color.transparent))
        incorrectImage2.setBackgroundColor(resources.getColor(R.color.transparent))
        incorrectImage3.setBackgroundColor(resources.getColor(R.color.transparent))
        // then set selected image to have green background
        selectedImage.setBackgroundColor(resources.getColor(R.color.champlain_green))
        isImageSelected = true
        // mark if answer is correct or not
        isImageAnswerCorrect = correct
    }

    private fun getHint() {
        // You get one hint per round so we just need to reset this whenever we go back to home
        if (!hintUsed) {
            if (!isImageQuestion) {
                when (Random.nextInt(0, 3)) {
                    0 -> incorrectText1.isVisible = false
                    1 -> incorrectText2.isVisible = false
                    2 -> incorrectText3.isVisible = false
                }
            } else {
                when (Random.nextInt(0, 3)) {
                    0 -> incorrectImage1.isVisible = false
                    1 -> incorrectImage2.isVisible = false
                    2 -> incorrectImage3.isVisible = false
                }
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
        // calculate score percentage
        scoreMessage.text = when((score.toFloat()/TOTAL_QUESTIONS.toFloat()) * 100.0) {
            100.0 -> "Perfection!"
            in 75.0..99.0 -> "Well Done!"
            in 50.0..74.0 -> "Not Too Shabby..."
            in 25.0..49.0 -> "You Can Do Better!"
            in 0.0..24.0 -> "That Was Terrible!"
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
            val intent = newIntent(this@QuizActivity)
            intent.putExtra("category", category)
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
            val filename = "$category-highscores"

            // Get existing scores from internal storage depending on category
            var existingScores = readScores(filename).toMutableMap()

            // Add current score to existing scores
            existingScores[score] = nameText

            // Write new list of scores to file
            writeScores(filename, existingScores)

            scoreSaved = true
            makeToast("Score saved!")
        }
        else {
            makeToast("Please enter a name to save your score")
        }
    }

    private fun readScores(filename: String): Map<Int, String> {
        var scores = mutableMapOf<Int, String>()
        // Return early if the file doesn't exist yet
        if (!getFileStreamPath(filename).exists()) {
            return scores
        }

        // Read the formatted lines and put into map of score to name
        val lines = openFileInput(filename).bufferedReader().readLines()
        for (line in lines) {
            val score = line.split("/")[0].toInt()
            val name = line.split("/")[1]
            scores[score] = name
        }
        return scores
    }

    private fun writeScores(filename: String, scores: Map<Int, String>) {
        val fileOutputStream = openFileOutput(filename, MODE_PRIVATE)
        val textOutputStream = OutputStreamWriter(fileOutputStream)

        val sortedScores = scores.toSortedMap()
        var toWrite = ""

        for (score in sortedScores) {
            toWrite += "${score.key}/${score.value}\n"
        }

        textOutputStream.write(toWrite)
        textOutputStream.close()
        fileOutputStream.close()
    }

    private fun makeToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}
