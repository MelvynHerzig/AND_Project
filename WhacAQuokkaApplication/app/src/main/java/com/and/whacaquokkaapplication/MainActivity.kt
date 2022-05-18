package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // TODO: nine patch doesn't work with buttons
    private lateinit var discoverButton : TextView
    private lateinit var advertButton : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        discoverButton = findViewById(R.id.discover_button)
        advertButton = findViewById(R.id.advert_button)

        discoverButton.setOnClickListener {
            // TODO
        }

        advertButton.setOnClickListener {
            // TODO
        }
    }
}