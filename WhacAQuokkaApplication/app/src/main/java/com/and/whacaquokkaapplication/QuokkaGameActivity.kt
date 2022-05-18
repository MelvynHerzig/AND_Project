package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class QuokkaGameActivity : AppCompatActivity() {

    private lateinit var scoreTextView : TextView
    private lateinit var timeTextView : TextView

    private lateinit var quitButton: Button

    private lateinit var quokka1 : ImageView
    private lateinit var quokka2 : ImageView
    private lateinit var quokka3 : ImageView
    private lateinit var quokka4 : ImageView
    private lateinit var quokka5 : ImageView
    private lateinit var quokka6 : ImageView
    private lateinit var quokka7 : ImageView
    private lateinit var quokka8 : ImageView
    private lateinit var quokka9 : ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quokka_game)

        scoreTextView = findViewById(R.id.score)
        timeTextView = findViewById(R.id.time)

        quitButton = findViewById(R.id.quit_image_button)

        quokka1 = findViewById(R.id.quokka_1)
        quokka2 = findViewById(R.id.quokka_2)
        quokka3 = findViewById(R.id.quokka_3)
        quokka4 = findViewById(R.id.quokka_4)
        quokka5 = findViewById(R.id.quokka_5)
        quokka6 = findViewById(R.id.quokka_6)
        quokka7 = findViewById(R.id.quokka_7)
        quokka8 = findViewById(R.id.quokka_8)
        quokka9 = findViewById(R.id.quokka_9)

        quitButton.setOnClickListener {
            finish()
        }

        quokka1.setOnClickListener {
            quokka1.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka2.setOnClickListener {
            quokka2.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka3.setOnClickListener {
            quokka3.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka4.setOnClickListener {
            quokka4.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka5.setOnClickListener {
            quokka5.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka6.setOnClickListener {
            quokka7.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka8.setOnClickListener {
            quokka8.setImageResource(R.drawable.quokka)
            // TODO
        }

        quokka9.setOnClickListener {
            quokka9.setImageResource(R.drawable.quokka)
            // TODO
        }
    }
}