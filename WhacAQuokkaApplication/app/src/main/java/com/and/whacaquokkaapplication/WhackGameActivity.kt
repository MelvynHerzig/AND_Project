package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.google.android.gms.nearby.connection.Payload

import com.and.whacaquokkaapplication.databinding.ActivityWhackGameBinding
import com.and.whacaquokkaapplication.models.GameStatus
import com.and.whacaquokkaapplication.models.GameStatusMessage
import com.and.whacaquokkaapplication.models.Message

class WhackGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhackGameBinding

    private lateinit var game : GameMaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWhackGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spawns = arrayOf(
            binding.spawn1,  binding.spawn2,  binding.spawn3,
            binding.spawn4,  binding.spawn5, binding.spawn6,
            binding.spawn7,  binding.spawn8, binding.spawn9
        )
        game = GameMaster()
        game.startGame()

        // ---------------------- Game notifications ------------------

        game.scoreQuokka.observe(this){
            binding.scoreQuokka.text = it.toString()
        }

        game.scoreWhack.observe(this){
            binding.scoreWhack.text = it.toString()
        }

        game.timer.observe(this){
            binding.time.text = it.toString()
        }

        game.updateHoleNumber.observe(this){
            game.updateHolesView(spawns, it)
        }

        game.gameOver.observe(this){
            if(it) {
                game.stopGame()

                val endMessage: String =
                    if (game.scoreQuokka.value!! < game.scoreWhack.value!!) getString(R.string.end_game_message_won)
                    else getString(R.string.end_game_message_lost)

                val dialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.end_game_title))
                    .setMessage(endMessage)
                    .setCancelable(true)
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                        finish()
                    }
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        finish()
                    }
                    .create()
                dialog.show()
            }
        }

        // ---------------------- Listeners ---------------------------


        binding.quitImageButton.setOnClickListener {
            finish()
        }
       
        binding.spawn1.setOnClickListener {
            game.hitHole(0)
        }

        binding.spawn2.setOnClickListener {
            game.hitHole(1)
        }

        binding.spawn3.setOnClickListener {
            game.hitHole(2)
        }

        binding.spawn4.setOnClickListener {
            game.hitHole(3)
        }

        binding.spawn5.setOnClickListener {
            game.hitHole(4)
        }

        binding.spawn6.setOnClickListener {
            game.hitHole(5)
        }

        binding.spawn7.setOnClickListener {
            game.hitHole(6)
        }

        binding.spawn8.setOnClickListener {
            game.hitHole(7)
        }

        binding.spawn9.setOnClickListener {
            game.hitHole(8)
        }
        
        BluetoothConnectionService.removeListener();

        // Detecte la déconnexion
        BluetoothConnectionService.instance.endpointListener =
            object : BluetoothConnectionService.EndpointListener {
                override fun onEndpointDiscovered(endpoint: BluetoothConnectionService.Endpoint?) {
                }

                override fun onEndpointConnected(endpoint: BluetoothConnectionService.Endpoint?) {
                }

                override fun onEndpointDisconnected(endpoint: BluetoothConnectionService.Endpoint?) {
                    BluetoothConnectionService.stopAll()
                    finish()
                    Toast.makeText(this@WhackGameActivity, "Disconnected", Toast.LENGTH_SHORT)
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

    override fun onStop() {
        super.onStop()
        BluetoothConnectionService.disconnectFromAllEndpoints()
        game.stopGame()
    }
}