package com.and.whacaquokkaapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.and.whacaquokkaapplication.databinding.ActivityQuokkaGameBinding
import com.and.whacaquokkaapplication.Game
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.and.whacaquokkaapplication.models.Message
import com.google.android.gms.nearby.connection.Payload


class QuokkaGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuokkaGameBinding

    private lateinit var game : GameClient

    @SuppressLint("ClickableViewAccessibility")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuokkaGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spawns = arrayOf(
            binding.spawn1,  binding.spawn2,  binding.spawn3,
            binding.spawn4,  binding.spawn5, binding.spawn6,
            binding.spawn7,  binding.spawn8, binding.spawn9
        )

        game = GameClient(spawns, binding.scoreWhack, binding.scoreWhack, binding.time)

        binding.quitImageButton.setOnClickListener {
            finish()
        }

        binding.spawn1Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(1)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(1)
            }
            true
        }

        binding.spawn2Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(2)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(2)
            }
            true
        }

        binding.spawn3Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(3)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(3)
            }
            true
        }

        binding.spawn4Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(4)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(4)
            }
            true
        }

        binding.spawn5Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(5)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(5)
            }
            true
        }

        binding.spawn6Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(6)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(6)
            }
            true
        }

        binding.spawn7Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(7)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(7)
            }
            true
        }

        binding.spawn8Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(8)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(8)
            }
            true
        }

        binding.spawn9Button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                game.quokkaAppear(9)
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Released
                game.quokkaDisappear(9)
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