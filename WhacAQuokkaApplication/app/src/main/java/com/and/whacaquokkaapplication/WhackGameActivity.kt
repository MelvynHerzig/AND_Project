package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.google.android.gms.nearby.connection.Payload

import com.and.whacaquokkaapplication.databinding.ActivityWhackGameBinding

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
        game = GameMaster(spawns, binding.scoreQuokka, binding.scoreWhack, binding.time)

        binding.quitImageButton.setOnClickListener {
            finish()
        }
       
        binding.spawn1.setOnClickListener {
            game.hitHole(1)
            BluetoothConnectionService.instance.send(Payload.fromBytes("1".toByteArray()))
        }

        binding.spawn2.setOnClickListener {
            game.hitHole(2)
            BluetoothConnectionService.instance.send(Payload.fromBytes("2".toByteArray()))
        }

        binding.spawn3.setOnClickListener {
            game.hitHole(3)
            BluetoothConnectionService.instance.send(Payload.fromBytes("3".toByteArray()))
        }

        binding.spawn4.setOnClickListener {
            game.hitHole(4)
            BluetoothConnectionService.instance.send(Payload.fromBytes("4".toByteArray()))
        }

        binding.spawn5.setOnClickListener {
            game.hitHole(5)
            BluetoothConnectionService.instance.send(Payload.fromBytes("5".toByteArray()))
        }

        binding.spawn6.setOnClickListener {
            game.hitHole(6)
            BluetoothConnectionService.instance.send(Payload.fromBytes("6".toByteArray()))
        }

        binding.spawn7.setOnClickListener {
            game.hitHole(7)
            BluetoothConnectionService.instance.send(Payload.fromBytes("7".toByteArray()))
        }

        binding.spawn8.setOnClickListener {
            game.hitHole(8)
            BluetoothConnectionService.instance.send(Payload.fromBytes("8".toByteArray()))
        }

        binding.spawn9.setOnClickListener {
            game.hitHole(9)
            BluetoothConnectionService.instance.send(Payload.fromBytes("9".toByteArray()))
        }
        
         BluetoothConnectionService.instance.removeListener();

        // Detecte la déconnexion
        BluetoothConnectionService.instance.endpointListener =
            object : BluetoothConnectionService.EndpointListener {
                override fun onEndpointDiscovered(endpoint: BluetoothConnectionService.Endpoint?) {
                }

                override fun onEndpointConnected(endpoint: BluetoothConnectionService.Endpoint?) {
                }

                override fun onEndpointDisconnected(endpoint: BluetoothConnectionService.Endpoint?) {
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
                    val message = payload!!.asBytes()?.let { String(it) }
                    Toast.makeText(this@WhackGameActivity , message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroy() {
        game.exitGame()
        super.onDestroy()
    }
}