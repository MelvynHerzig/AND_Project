package com.and.whacaquokkaapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.and.whacaquokkaapplication.databinding.ActivityQuokkaGameBinding
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.gamelogic.GameClient
import com.and.whacaquokkaapplication.models.Message
import com.and.whacaquokkaapplication.models.QuokkaStatus
import com.google.android.gms.nearby.connection.Payload


class QuokkaGameActivity : GameActivity() {

    private lateinit var binding: ActivityQuokkaGameBinding


    @SuppressLint("ClickableViewAccessibility")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityQuokkaGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spawns = arrayOf(
            binding.spawn1, binding.spawn2, binding.spawn3,
            binding.spawn4, binding.spawn5, binding.spawn6,
            binding.spawn7, binding.spawn8, binding.spawn9
        )
        game = GameClient()

        // ---------------------- Set Game values ----------------------
        super.spawns = spawns
        super.quokkaScore = binding.scoreQuokka
        super.whackScore = binding.scoreWhack
        super.time = binding.time
        super.quitButton = binding.quitImageButton
        super.game = game

        super.onCreate(savedInstanceState)


        // ---------------------- Listeners ---------------------------
        binding.spawn1Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(0, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(0, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn2Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(1, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(1, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn3Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(2, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(2, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn4Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(3, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(3, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn5Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(4, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(4, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn6Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(5, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(5, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn7Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(6, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(6, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn8Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(7, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(7, QuokkaStatus.HIDE)
            }
            true
        }

        binding.spawn9Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                game.setQuokkaVisiblity(8, QuokkaStatus.SHOW)
            } else if (event.action == MotionEvent.ACTION_UP) {
                game.setQuokkaVisiblity(8, QuokkaStatus.HIDE)
            }
            true
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
                    game.stopGame()
                    Toast.makeText(this@QuokkaGameActivity, "Disconnected", Toast.LENGTH_SHORT)
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


}