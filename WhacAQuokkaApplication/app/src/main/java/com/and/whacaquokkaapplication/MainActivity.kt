package com.and.whacaquokkaapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener


class MainActivity : AppCompatActivity() {

    val STRATEGY: Strategy = Strategy.P2P_POINT_TO_POINT
    val SERVICE_ID = "120002"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.btnDiscover).setOnClickListener {
            if (!Permission.hasPermissions(this)) {
                Permission.requestPermissionsDiscovery(this)
            } else {
                startDiscovery()
            }
        }

        findViewById<Button>(R.id.btnAdvert).setOnClickListener {
            if (!Permission.hasPermissions(this)) {
                Permission.requestPermissionsAdvertising(this)
            } else {
                startAdvertising()
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
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // On all success start advertising
        if(requestCode == Permission.ADVERTISING_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all{ it == PackageManager.PERMISSION_GRANTED}) {
                startAdvertising()
            }
        }

        // On all success start discovery
        if(requestCode == Permission.DISCOVERY_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all{ it == PackageManager.PERMISSION_GRANTED}) {
                startDiscovery()
            }
        }
    }

    /*******************************************************************************************
     *                                     Bluetooth                                           *
     *******************************************************************************************/

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(this)
            .startAdvertising(
                Settings.System.getString(contentResolver, Settings.Global.DEVICE_NAME), SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(this, "start advert success listener", Toast.LENGTH_SHORT).show()
                    })
            .addOnFailureListener(
                OnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
    }


    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(this)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                Toast.makeText(this, "start discover success listener", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                Nearby.getConnectionsClient(this@MainActivity)
                    .requestConnection(Settings.System.getString(contentResolver, Settings.Global.DEVICE_NAME), endpointId, connectionLifecycleCallback)
                    .addOnSuccessListener(
                        OnSuccessListener {
                            Toast.makeText(this@MainActivity, "discover callback success listener", Toast.LENGTH_SHORT).show()
                        })
                    .addOnFailureListener(
                        OnFailureListener {
                            Toast.makeText(this@MainActivity, "discover callback failure listener", Toast.LENGTH_SHORT).show()
                        })
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
            }
        }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(this@MainActivity).acceptConnection(endpointId, ReceiveBytesPayloadListener())
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {Toast.makeText(this@MainActivity, "on connection result OK", Toast.LENGTH_SHORT).show()}
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {Toast.makeText(this@MainActivity, "on connection result REJECTED", Toast.LENGTH_SHORT).show()}
                    ConnectionsStatusCodes.STATUS_ERROR -> {Toast.makeText(this@MainActivity, "on connection result ERROR", Toast.LENGTH_SHORT).show()}
                    else -> {}
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
            }
        }

    internal class ReceiveBytesPayloadListener : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // This always gets the full data of the payload. Is null if it's not a BYTES payload.
            if (payload.type == Payload.Type.BYTES) {
                val receivedBytes = payload.asBytes()
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }
}