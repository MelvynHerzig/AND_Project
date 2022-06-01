package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.google.android.gms.nearby.connection.Payload

class QuokkaGameActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var timeTextView: TextView

    private lateinit var quitButton: TextView

    private lateinit var quokka1: ImageView
    private lateinit var quokka2: ImageView
    private lateinit var quokka3: ImageView
    private lateinit var quokka4: ImageView
    private lateinit var quokka5: ImageView
    private lateinit var quokka6: ImageView
    private lateinit var quokka7: ImageView
    private lateinit var quokka8: ImageView
    private lateinit var quokka9: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quokka_game)

        scoreTextView = findViewById(R.id.score)
        timeTextView = findViewById(R.id.time)

        quitButton = findViewById(R.id.quit_image_button)

        quokka1 = findViewById(R.id.quokka_1)
        quokka2 = findViewById(R.id.quokka_2)
        quokka3 = findViewById(R.id.quokka_3)
        quokka4 = findViewById(R.id.quokka_4)
        quokka5 = findViewById(R.id.quokka_5)
        quokka6 = findViewById(R.id.quokka_6)
        quokka7 = findViewById(R.id.quokka_7)
        quokka8 = findViewById(R.id.quokka_8)
        quokka9 = findViewById(R.id.quokka_9)

        quitButton.setOnClickListener {
            finish()
        }

        quokka1.setOnClickListener {
            quokka1.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("1".toByteArray()))
        }

        quokka2.setOnClickListener {
            quokka2.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("2".toByteArray()))
        }

        quokka3.setOnClickListener {
            quokka3.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("3".toByteArray()))
        }

        quokka4.setOnClickListener {
            quokka4.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("4".toByteArray()))
        }

        quokka5.setOnClickListener {
            quokka5.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("5".toByteArray()))
        }

        quokka6.setOnClickListener {
            quokka6.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("6".toByteArray()))
        }

        quokka7.setOnClickListener {
            quokka7.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("7".toByteArray()))
        }

        quokka8.setOnClickListener {
            quokka8.setImageResource(R.drawable.quokka)
            // TODO
            BluetoothConnectionService.instance.send(Payload.fromBytes("8".toByteArray()))
        }

        quokka9.setOnClickListener {
            quokka9.setImageResource(R.drawable.quokka)
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
                    val message = payload!!.asBytes()?.let { String(it) }
                    Toast.makeText(this@QuokkaGameActivity, message, Toast.LENGTH_SHORT).show()
                }
            }


    }


}