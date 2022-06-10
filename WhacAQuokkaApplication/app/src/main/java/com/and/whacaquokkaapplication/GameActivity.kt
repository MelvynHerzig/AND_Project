package com.and.whacaquokkaapplication

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.gamelogic.Game
import com.and.whacaquokkaapplication.models.Message
import com.google.android.gms.nearby.connection.Payload

abstract class GameActivity : AppCompatActivity() {

    protected lateinit var spawns: Array<ImageView>
    protected lateinit var quokkaScore: TextView
    protected lateinit var whackScore: TextView
    protected lateinit var time: TextView
    protected lateinit var quitButton: TextView
    protected lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
            if(it) {
                game.stopGame()
                showEndPopUp(game.didIWin())
            }
        }

        // ---------------------- Listeners ---------------------------
        quitButton.setOnClickListener {
            finish()
        }

        // ---------------------- Bluetooth ---------------------------
        BluetoothConnectionService.removeListener()

        BluetoothConnectionService.instance.dataListener =
            object : BluetoothConnectionService.DataListener {
                override fun onReceive(
                    endpoint: BluetoothConnectionService.Endpoint?,
                    payload: Payload?
                ) {
                    game.handleMessage(Message.fromPayload(payload!!))
                }
            }
    }

    private fun showEndPopUp(won: Boolean) {
        val endMessage: String =
            if (won) getString(R.string.end_game_message_won)
            else getString(R.string.end_game_message_lost)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.end_game_title))
            .setMessage(endMessage)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                finish()
            }
            .create().show()
    }

    override fun onStop() {
        super.onStop()
        BluetoothConnectionService.disconnectFromAllEndpoints()
        game.stopGame()
    }
}