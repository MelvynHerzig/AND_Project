package com.and.whacaquokkaapplication

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.gms.nearby.connection.*
import android.widget.TextView
import com.and.whacaquokkaapplication.bluetoothmanager.ConnectionsActivity

class MainActivity : ConnectionsActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<TextView>(R.id.advert_button).setOnClickListener {
            if (!Permission.hasPermissions(this)) {
                Permission.requestPermissionsDiscovery(this)
            } else {
                startAdvertising()
            }
        }

        findViewById<TextView>(R.id.discover_button).setOnClickListener {
            if (!Permission.hasPermissions(this)) {
                Permission.requestPermissionsAdvertising(this)
            } else {
                startDiscovering()
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
                startAdvertising()
            }
        }

        // On all success start discovery
        if (requestCode == Permission.DISCOVERY_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startDiscovering()
            }
        }
    }

    override fun onConnectionInitiated(endpoint: Endpoint?, connectionInfo: ConnectionInfo?) {


        val dialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Connexion à un partenaire de jeu")
            .setMessage("Voulez-vous vous connecter à " + connectionInfo!!.endpointName)
            .setCancelable(false) // dialog cannot be closed without doing a choice
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                rejectConnection(endpoint!!)
            }
            .setPositiveButton(android.R.string.yes) { _, _ ->
                acceptConnection(endpoint!!)
            }
            .create()
        dialog.show()

    }

    override fun onEndpointDiscovered(endpoint: Endpoint?) {
        Log.println(Log.INFO, "MainActivity", "Endpoint discovered")
        connectToEndpoint(endpoint!!)
    }

    override fun onEndpointConnected(endpoint: Endpoint?) {
        Log.println(Log.INFO, "MainActivity", "Endpoint connected")
        send(Payload.fromBytes("hello world".toByteArray()))
        val intent = Intent(this, QuokkaGameActivity::class.java)
        startActivity(intent)
    }


    override fun onReceive(endpoint: Endpoint?, payload: Payload?) {
        val bytes = payload!!.asBytes()
        Log.println(Log.INFO, "MainActivity", "Received bytes: " + bytes?.let { String(it) })
    }

}