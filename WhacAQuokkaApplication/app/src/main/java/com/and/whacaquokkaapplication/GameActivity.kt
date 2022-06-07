package com.and.whacaquokkaapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.and.whacaquokkaapplication.gamelogic.Game

abstract class GameActivity : AppCompatActivity() {

    protected lateinit var spawns: Array<ImageView>
    protected lateinit var quokkaScore: TextView
    protected lateinit var whackScore: TextView
    protected lateinit var time: TextView
    protected lateinit var quitButton: TextView
    protected lateinit var abstractGame: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        abstractGame.startGame()

        // ---------------------- Game notifications ------------------

        abstractGame.scoreQuokka.observe(this) {
            quokkaScore.text = it.toString()
        }

        abstractGame.scoreWhack.observe(this) {
            whackScore.text = it.toString()
        }

        abstractGame.timer.observe(this) {
            time.text = it.toString()
        }

        abstractGame.updateHoleNumber.observe(this) {
            abstractGame.updateHolesView(spawns, it)
        }

        abstractGame.gameOver.observe(this){
            abstractGame.stopGame()
            showEndPopUp(abstractGame.didIWin())
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