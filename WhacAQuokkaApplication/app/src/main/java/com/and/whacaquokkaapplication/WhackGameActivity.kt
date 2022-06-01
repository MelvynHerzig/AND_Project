package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.google.android.gms.nearby.connection.Payload

class WhackGameActivity : AppCompatActivity() {

    private lateinit var scoreTextView : TextView
    private lateinit var timeTextView : TextView

    private lateinit var quitButton: TextView

    private lateinit var hole1 : ImageView
    private lateinit var hole2 : ImageView
    private lateinit var hole3 : ImageView
    private lateinit var hole4 : ImageView
    private lateinit var hole5 : ImageView
    private lateinit var hole6 : ImageView
    private lateinit var hole7 : ImageView
    private lateinit var hole8 : ImageView
    private lateinit var hole9 : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whack_game)

        scoreTextView = findViewById(R.id.score)
        timeTextView = findViewById(R.id.time)

        quitButton = findViewById(R.id.quit_image_button)

        hole1 = findViewById(R.id.hole_1)
        hole2 = findViewById(R.id.hole_2)
        hole3 = findViewById(R.id.hole_3)
        hole4 = findViewById(R.id.hole_4)
        hole5 = findViewById(R.id.hole_5)
        hole6 = findViewById(R.id.hole_6)
        hole7 = findViewById(R.id.hole_7)
        hole8 = findViewById(R.id.hole_8)
        hole9 = findViewById(R.id.hole_9)

        quitButton.setOnClickListener {
            finish()
        }

        hole1.setOnClickListener {
            hole1.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("1".toByteArray()))
        }

        hole2.setOnClickListener {
            hole2.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("2".toByteArray()))
        }

        hole3.setOnClickListener {
            hole3.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("3".toByteArray()))
        }

        hole4.setOnClickListener {
            hole4.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("4".toByteArray()))
        }

        hole5.setOnClickListener {
            hole5.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("5".toByteArray()))
        }

        hole6.setOnClickListener {
            hole6.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("6".toByteArray()))
        }

        hole7.setOnClickListener {
            hole7.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("7".toByteArray()))
        }

        hole8.setOnClickListener {
            hole8.setImageResource(R.drawable.hole)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("8".toByteArray()))
        }

        hole9.setOnClickListener {
            hole9.setImageResource(R.drawable.hole)
            // TODO
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
}