package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var discoverButton : Button
    private lateinit var advertButton : Button

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