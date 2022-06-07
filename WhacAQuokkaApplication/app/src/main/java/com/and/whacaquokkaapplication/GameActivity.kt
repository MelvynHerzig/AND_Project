package com.and.whacaquokkaapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.and.whacaquokkaapplication.gamelogic.Game

abstract class GameActivity constructor(
    private var spawns: Array<ImageView>,
    private var quokkaScore: TextView,
    private var whackScore: TextView,
    private var time: TextView,
    private var quitButton: TextView,
    private var game: Game
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        game.startGame()

        // ---------------------- Game notifications ------------------

        game.scoreQuokka.observe(this) {
            quokkaScore.text = it.toString()
        }

        game.scoreWhack.observe(this) {
            whackScore.text = it.toString()
        }

        game.timer.observe(this) {
            time.text = it.toString()
        }

        game.updateHoleNumber.observe(this) {
            game.updateHolesView(spawns, it)
        }

        game.gameOver.observe(this){
            game.stopGame()
            showEndPopUp(game.didIWin())
        }

        // ---------------------- Listeners ---------------------------
        quitButton.setOnClickListener {
            finish()
        }
    }

    private fun showEndPopUp(won: Boolean) {
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