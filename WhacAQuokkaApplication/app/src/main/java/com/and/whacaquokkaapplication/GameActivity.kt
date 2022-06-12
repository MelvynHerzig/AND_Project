package com.and.whacaquokkaapplication

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.gamelogic.Game
import com.and.whacaquokkaapplication.models.Message
import com.and.whacaquokkaapplication.models.enums.GameStatus
import com.google.android.gms.nearby.connection.Payload

/**
 * This abstract activity is used to handle common the game logic.
 *
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
abstract class GameActivity : AppCompatActivity() {

    protected lateinit var spawns: Array<ImageView>
    protected lateinit var quokkaScore: TextView
    protected lateinit var whackScore: TextView
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


        game.updateHoleNumber.observe(this) {
            game.updateHolesView(spawns, it)
        }

        game.gameStatus.observe(this) {
            if (it == GameStatus.OVER) {
                showEndPopUp(game.didIWin())
            }
        }

        // ---------------------- Listeners ---------------------------
        quitButton.setOnClickListener {
            finish()
        }

        // ---------------------- Bluetooth ---------------------------
        BluetoothConnectionService.removeListener()

        // Detect the disconnection
        BluetoothConnectionService.instance.endpointListener =
            object : BluetoothConnectionService.EndpointListener {
                override fun onEndpointDiscovered(endpoint: BluetoothConnectionService.Endpoint?) {
                }

                override fun onEndpointConnected(endpoint: BluetoothConnectionService.Endpoint?) {
                }

                override fun onEndpointDisconnected(endpoint: BluetoothConnectionService.Endpoint?) {
                    game.stop()
                    BluetoothConnectionService.stopAll()
                    Toast.makeText(this@GameActivity, "Disconnected", Toast.LENGTH_SHORT)
                        .show()
                }
            }

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

    /**
     * Show the end pop up.
     */
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
        game.stop()
        BluetoothConnectionService.disconnectFromAllEndpoints()
        super.onStop()
    }
}