package com.example.champlain_trivia

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HighscoresActivity : AppCompatActivity() {

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, HighscoresActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscores)
        title = "High Scores"

    }
}