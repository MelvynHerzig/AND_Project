package com.and.whacaquokkaapplication

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.gms.nearby.connection.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.and.whacaquokkaapplication.bluetoothmanager.BluetoothConnectionService
import com.google.android.gms.nearby.Nearby

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        BluetoothConnectionService.instance.mConnectionsClient =
            Nearby.getConnectionsClient(this.applicationContext)





        findViewById<TextView>(R.id.advert_button).setOnClickListener {
            if (!Permission.hasPermissions(this)) {
                Permission.requestPermissionsDiscovery(this)
            } else {
                BluetoothConnectionService.startAdvertising()
            }
        }

        findViewById<TextView>(R.id.discover_button).setOnClickListener {
            if (!Permission.hasPermissions(this)) {
                Permission.requestPermissionsAdvertising(this)
            } else {
                BluetoothConnectionService.startDiscovering()
            }
        }


    }

    override fun onResume() {
        super.onResume()

        BluetoothConnectionService.instance.advertisingListener =
            object : BluetoothConnectionService.AdvertisingListener {
                override fun onAdvertisingStarted() {
                    Toast.makeText(
                        this@MainActivity,
                        "Advertising successfully started",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdvertisingFailed() {
                    Toast.makeText(
                        this@MainActivity,
                        "Advertising failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        BluetoothConnectionService.instance.connectionListener =
            object : BluetoothConnectionService.ConnectionListener {
                override fun onConnectionInitiated(
                    endpoint: BluetoothConnectionService.Endpoint?,
                    connectionInfo: ConnectionInfo?
                ) {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                        .setTitle("Connexion à un partenaire de jeu")
                        .setMessage("Voulez-vous vous connecter à ${endpoint?.name} ?")
                        .setCancelable(false) // dialog cannot be closed without doing a choice
                        .setNegativeButton(android.R.string.cancel) { _, _ ->
                            BluetoothConnectionService.rejectConnection(endpoint!!)
                        }
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            BluetoothConnectionService.acceptConnection(endpoint!!)
                        }
                        .create()
                    dialog.show()
                }

                override fun onConnectionFailed(endpoint: BluetoothConnectionService.Endpoint?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Connection failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        BluetoothConnectionService.instance.discoveringListener =
            object : BluetoothConnectionService.DiscoveringListener {
                override fun onDiscoveryStarted() {
                    Toast.makeText(
                        this@MainActivity,
                        "Discovery successfully started",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDiscoveryFailed() {
                    Toast.makeText(
                        this@MainActivity,
                        "Discovery failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        BluetoothConnectionService.instance.endpointListener =
            object : BluetoothConnectionService.EndpointListener {
                override fun onEndpointDiscovered(endpoint: BluetoothConnectionService.Endpoint?) {
                    BluetoothConnectionService.connectToEndpoint(endpoint!!)
                }

                override fun onEndpointConnected(endpoint: BluetoothConnectionService.Endpoint?) {
                    if(BluetoothConnectionService.instance.isAdvertising){
                        BluetoothConnectionService.stopAdvertising()
                        val intent = Intent(this@MainActivity, WhackGameActivity::class.java)
                        startActivity(intent)
                    }else{
                        BluetoothConnectionService.stopDiscovering()
                        val intent = Intent(this@MainActivity, QuokkaGameActivity::class.java)
                        startActivity(intent)
                    }

                }

                override fun onEndpointDisconnected(endpoint: BluetoothConnectionService.Endpoint?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Disconnected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    /*******************************************************************************************
     *                                    Permissions                                          *
     *******************************************************************************************/

    /**
     * Function called when the user decline a required permission. Displays a the
     * "please accept our permission" dialog.
     * Source: https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // On all success start advertising
        if (requestCode == Permission.ADVERTISING_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                BluetoothConnectionService.startAdvertising()
            }
        }

        // On all success start discovery
        if (requestCode == Permission.DISCOVERY_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                BluetoothConnectionService.startDiscovering()
            }
        }
    }
}