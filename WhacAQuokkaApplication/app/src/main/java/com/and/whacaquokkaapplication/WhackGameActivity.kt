package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class WhackGameActivity : AppCompatActivity() {

    private lateinit var scoreTextView : TextView
    private lateinit var timeTextView : TextView

    private lateinit var quitButton: TextView

    private lateinit var hole1 : ImageView
    private lateinit var hole2 : ImageView
    private lateinit var hole3 : ImageView
    private lateinit var hole4 : ImageView
    private lateinit var hole5 : ImageView
    private lateinit var hole6 : ImageView
    private lateinit var hole7 : ImageView
    private lateinit var hole8 : ImageView
    private lateinit var hole9 : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whack_game)

        scoreTextView = findViewById(R.id.score)
        timeTextView = findViewById(R.id.time)

        quitButton = findViewById(R.id.quit_image_button)

        hole1 = findViewById(R.id.hole_1)
        hole2 = findViewById(R.id.hole_2)
        hole3 = findViewById(R.id.hole_3)
        hole4 = findViewById(R.id.hole_4)
        hole5 = findViewById(R.id.hole_5)
        hole6 = findViewById(R.id.hole_6)
        hole7 = findViewById(R.id.hole_7)
        hole8 = findViewById(R.id.hole_8)
        hole9 = findViewById(R.id.hole_9)

        quitButton.setOnClickListener {
            finish()
        }

        hole1.setOnClickListener {
            hole1.setImageResource(R.drawable.hole)
            // TODO
        }

        hole2.setOnClickListener {
            hole2.setImageResource(R.drawable.hole)
            // TODO
        }

        hole3.setOnClickListener {
            hole3.setImageResource(R.drawable.hole)
            // TODO
        }

        hole4.setOnClickListener {
            hole4.setImageResource(R.drawable.hole)
            // TODO
        }

        hole5.setOnClickListener {
            hole5.setImageResource(R.drawable.hole)
            // TODO
        }

        hole6.setOnClickListener {
            hole7.setImageResource(R.drawable.hole)
            // TODO
        }

        hole8.setOnClickListener {
            hole8.setImageResource(R.drawable.hole)
            // TODO
        }

        hole9.setOnClickListener {
            hole9.setImageResource(R.drawable.hole)
            // TODO
        }
    }
}