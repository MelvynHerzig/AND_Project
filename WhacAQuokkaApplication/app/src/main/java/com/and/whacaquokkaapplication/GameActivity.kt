package com.and.whacaquokkaapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

abstract class GameActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showEndPopUp(won: Boolean) {
        val endMessage: String =
            if (won) getString(R.string.end_game_message_won)
            else getString(R.string.end_game_message_lost)

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.end_game_title))
            .setMessage(endMessage)
            .setCancelable(true)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .create()
        dialog.show()
    }
}