package com.example.champlain_trivia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private lateinit var generalButton: ImageButton
    private lateinit var buildingsButton: ImageButton
    private lateinit var burlingtonButton: ImageButton
    private lateinit var highscoresButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        generalButton = findViewById(R.id.general_category)
        generalButton.setOnClickListener {
            val intent = QuizActivity.newIntent(this@MainActivity)
            intent.putExtra("category", "general")
            startActivity(intent)
        }

        buildingsButton = findViewById(R.id.buildings_category)
        buildingsButton.setOnClickListener {
            val intent = QuizActivity.newIntent(this@MainActivity)
            intent.putExtra("category", "buildings")
            startActivity(intent)
        }

        burlingtonButton = findViewById(R.id.burlington_category)
        burlingtonButton.setOnClickListener {
            val intent = QuizActivity.newIntent(this@MainActivity)
            intent.putExtra("category", "burlington")
            startActivity(intent)
        }

        highscoresButton = findViewById(R.id.highscores)
        highscoresButton.setOnClickListener {
            val intent = HighscoresActivity.newIntent(this@MainActivity)
            startActivity(intent)
        }
    }
}