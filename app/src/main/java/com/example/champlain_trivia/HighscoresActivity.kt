package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class HighscoresActivity : AppCompatActivity() {

    private lateinit var generalScoresList: ListView
    private lateinit var buildingsScoresList: ListView
    private lateinit var burlingtonScoresList: ListView

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, HighscoresActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscores)
        title = "High Scores"

        generalScoresList = findViewById(R.id.general_scores)
        buildingsScoresList = findViewById(R.id.buildings_scores)
        burlingtonScoresList = findViewById(R.id.burlington_scores)

        val generalScores = readScores("general-highscores").reversed()
        val buildingsScores = readScores("buildings-highscores").reversed()
        val burlingtonScores = readScores("burlington-highscores").reversed()

        val generalAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, generalScores)
        generalScoresList.adapter = generalAdapter

        val buildingsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, buildingsScores)
        buildingsScoresList.adapter = buildingsAdapter

        val burlingtonAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, burlingtonScores)
        burlingtonScoresList.adapter = burlingtonAdapter
    }

    private fun readScores(filename: String): List<String> {
        var scores = mutableListOf<String>()
        // Return early if the file doesn't exist yet
        if (!getFileStreamPath(filename).exists()) {
            return scores
        }

        // Read the formatted lines and put into map of score to name
        val lines = openFileInput(filename).bufferedReader().readLines()
        for (line in lines) {
            val score = line.split("/")[0]
            val name = line.split("/")[1]
            scores.add("$name: $score")
        }
        return scores
    }
}