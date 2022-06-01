package com.and.whacaquokkaapplication.bluetoothmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import java.util.*


/** A class that connects to Nearby Connections and provides convenience methods and callbacks.  */
abstract class ConnectionsActivity : AppCompatActivity() {
    /** Our handler to Nearby Connections.  */
    private var mConnectionsClient: ConnectionsClient? = null

    /** The devices we've discovered near us.  */
    private val mDiscoveredEndpoints: MutableMap<String, Endpoint> = HashMap()

    /**
     * The devices we have pending connections to. They will stay pending until we call [ ][.acceptConnection] or [.rejectConnection].
     */
    private val mPendingConnections: MutableMap<String, Endpoint> = HashMap()

    /**
     * The devices we are currently connected to. For advertisers, this may be large. For discoverers,
     * there will only be one entry in this map.
     */
    private val mEstablishedConnections: MutableMap<String, Endpoint?> = HashMap()
    /** Returns `true` if we're currently attempting to connect to another device.  */
    /**
     * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
     * device.
     */
    protected var isConnecting = false
        private set
    /** Returns `true` if currently discovering.  */
    /** True if we are discovering.  */
    protected var isDiscovering = false
        private set
    /** Returns `true` if currently advertising.  */
    /** True if we are advertising.  */
    protected var isAdvertising = false
        private set

    /** Callbacks for connections to other devices.  */
    private val mConnectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                    String.format(
                        "onConnectionInitiated(endpointId=%s, endpointName=%s)",
                        endpointId, connectionInfo.endpointName
                    )
                val endpoint = Endpoint(endpointId, connectionInfo.endpointName)
                mPendingConnections[endpointId] = endpoint
                this@ConnectionsActivity.onConnectionInitiated(endpoint, connectionInfo)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                String.format(
                    "onConnectionResponse(endpointId=%s, result=%s)",
                    endpointId,
                    result
                )

                // We're no longer connecting
                isConnecting = false
                if (!result.status.isSuccess) {
                    String.format(
                        "Connection failed. Received status %s.",
                        toString(result.status)
                    )
                    onConnectionFailed(mPendingConnections.remove(endpointId))
                    return
                }
                connectedToEndpoint(mPendingConnections.remove(endpointId))
            }

            override fun onDisconnected(endpointId: String) {
                if (!mEstablishedConnections.containsKey(endpointId)) {
                    return
                }
                disconnectedFromEndpoint(mEstablishedConnections[endpointId])
            }
        }

    /** Callbacks for payloads (bytes of data) sent from another device to us.  */
    private val mPayloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            onReceive(mEstablishedConnections[endpointId], payload)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            String.format(
                "onPayloadTransferUpdate(endpointId=%s, update=%s)", endpointId, update
            )
        }
    }

    /** Called when our Activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mConnectionsClient = Nearby.getConnectionsClient(this.applicationContext)
    }



    /** Called when the user has accepted (or denied) our permission request.  */
    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (grantResult in grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Missing permissions", Toast.LENGTH_LONG)
                        .show()
                    finish()
                    return
                }
            }
            recreate()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Sets the device to advertising mode. It will broadcast to other devices in discovery mode.
     * Either [.onAdvertisingStarted] or [.onAdvertisingFailed] will be called once
     * we've found out if we successfully entered this mode.
     */
    protected fun startAdvertising() {
        isAdvertising = true

        val localEndpointName = name
        val advertisingOptions = AdvertisingOptions.Builder()
        advertisingOptions.setStrategy(strategy!!)
        mConnectionsClient!!
            .startAdvertising(
                localEndpointName!!,
                serviceId,
                mConnectionLifecycleCallback,
                advertisingOptions.build()
            )
            .addOnSuccessListener {
                onAdvertisingStarted()
            }
            .addOnFailureListener { e ->
                isAdvertising = false
                onAdvertisingFailed()
            }
    }

    /** Stops advertising.  */
    protected fun stopAdvertising() {
        isAdvertising = false
        mConnectionsClient!!.stopAdvertising()
    }

    /** Called when advertising successfully starts. Override this method to act on the event.  */
    protected open fun onAdvertisingStarted()
    {
        Toast.makeText(this, "Advertising successfully started", Toast.LENGTH_SHORT).show()
    }

    /** Called when advertising fails to start. Override this method to act on the event.  */
    protected open fun onAdvertisingFailed(){
        Toast.makeText(this, "Advertising failed to start", Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when a pending connection with a remote endpoint is created. Use [ConnectionInfo]
     * for metadata about the connection (like incoming vs outgoing, or the authentication token). If
     * we want to continue with the connection, call [.acceptConnection]. Otherwise,
     * call [.rejectConnection].
     */
    protected open fun onConnectionInitiated(endpoint: Endpoint?, connectionInfo: ConnectionInfo?){
        // Automatically accept the connection on both sides.
        acceptConnection(endpoint!!)
    }

    /** Accepts a connection request.  */
    protected fun acceptConnection(endpoint: Endpoint) {
        Log.println(Log.INFO, "ConnectionsActivity", "acceptConnection($endpoint)")
        mConnectionsClient!!
            .acceptConnection(endpoint.id, mPayloadCallback)
            .addOnFailureListener { e -> println(e) }
    }

    /** Rejects a connection request.  */
    protected fun rejectConnection(endpoint: Endpoint) {
        mConnectionsClient!!
            .rejectConnection(endpoint.id)
            .addOnFailureListener { e -> println(e) }
    }

    /**
     * Sets the device to discovery mode. It will now listen for devices in advertising mode. Either
     * [.onDiscoveryStarted] or [.onDiscoveryFailed] will be called once we've found
     * out if we successfully entered this mode.
     */
    protected fun startDiscovering() {
        isDiscovering = true
        mDiscoveredEndpoints.clear()
        val discoveryOptions = DiscoveryOptions.Builder()
        discoveryOptions.setStrategy(strategy!!)
        mConnectionsClient!!
            .startDiscovery(
                serviceId,
                object : EndpointDiscoveryCallback() {
                    override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                            String.format(
                                "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                                endpointId, info.serviceId, info.endpointName
                            )
                        if (serviceId == info.serviceId) {
                            val endpoint = Endpoint(endpointId, info.endpointName)
                            mDiscoveredEndpoints[endpointId] = endpoint
                            onEndpointDiscovered(endpoint)
                        }
                    }

                    override fun onEndpointLost(endpointId: String) {
                    }
                },
                discoveryOptions.build()
            )
            .addOnSuccessListener { onDiscoveryStarted() }
            .addOnFailureListener { e ->
                isDiscovering = false
                onDiscoveryFailed()
            }
    }

    /** Stops discovery.  */
    protected fun stopDiscovering() {
        isDiscovering = false
        mConnectionsClient!!.stopDiscovery()
    }

    /** Called when discovery successfully starts. Override this method to act on the event.  */
    protected open fun onDiscoveryStarted(){
        Toast.makeText(this, "Discovery successfully started", Toast.LENGTH_SHORT).show()
    }

    /** Called when discovery fails to start. Override this method to act on the event.  */
    protected open fun onDiscoveryFailed(){
        Toast.makeText(this, "Discovery failed to start", Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when a remote endpoint is discovered. To connect to the device, call [ ][.connectToEndpoint].
     */
    protected open fun onEndpointDiscovered(endpoint: Endpoint?){
        connectToEndpoint(endpoint!!)
    }

    /** Disconnects from the given endpoint.  */
    protected fun disconnect(endpoint: Endpoint) {
        mConnectionsClient!!.disconnectFromEndpoint(endpoint.id)
        mEstablishedConnections.remove(endpoint.id)
    }

    /** Disconnects from all currently connected endpoints.  */
    protected fun disconnectFromAllEndpoints() {
        for (endpoint in mEstablishedConnections.values) {
            mConnectionsClient!!.disconnectFromEndpoint(endpoint!!.id)
        }
        mEstablishedConnections.clear()
    }

    /** Resets and clears all state in Nearby Connections.  */
    protected fun stopAllEndpoints() {
        mConnectionsClient!!.stopAllEndpoints()
        isAdvertising = false
        isDiscovering = false
        isConnecting = false
        mDiscoveredEndpoints.clear()
        mPendingConnections.clear()
        mEstablishedConnections.clear()
    }

    /**
     * Sends a connection request to the endpoint. Either [.onConnectionInitiated] or [.onConnectionFailed] will be called once we've found out
     * if we successfully reached the device.
     */
    protected fun connectToEndpoint(endpoint: Endpoint) {
        // Mark ourselves as connecting so we don't connect multiple times
        isConnecting = true

        // Ask to connect
        mConnectionsClient!!
            .requestConnection(name!!, endpoint.id, mConnectionLifecycleCallback)
            .addOnFailureListener {
                isConnecting = false
                onConnectionFailed(endpoint)
            }
    }

    private fun connectedToEndpoint(endpoint: Endpoint?) {
        mEstablishedConnections[endpoint!!.id] = endpoint
        onEndpointConnected(endpoint)
    }

    private fun disconnectedFromEndpoint(endpoint: Endpoint?) {
        mEstablishedConnections.remove(endpoint!!.id)
        onEndpointDisconnected(endpoint)
    }

    /**
     * Called when a connection with this endpoint has failed. Override this method to act on the
     * event.
     */
    protected open fun onConnectionFailed(endpoint: Endpoint?){
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show()
    }

    /** Called when someone has connected to us. Override this method to act on the event.  */
    protected open fun onEndpointConnected(endpoint: Endpoint?){
        Toast.makeText(this, "Connected to endpoint", Toast.LENGTH_SHORT).show()
    }

    /** Called when someone has disconnected. Override this method to act on the event.  */
    protected open fun onEndpointDisconnected(endpoint: Endpoint?){
        Toast.makeText(this, "Disconnected from endpoint", Toast.LENGTH_SHORT).show()
    }

    /** Returns a list of currently connected endpoints.  */
    protected val discoveredEndpoints: Set<Endpoint>
        protected get() = HashSet(mDiscoveredEndpoints.values)

    /** Returns a list of currently connected endpoints.  */
    protected val connectedEndpoints: Set<Endpoint?>
        protected get() = HashSet(mEstablishedConnections.values)

    /**
     * Sends a [Payload] to all currently connected endpoints.
     *
     * @param payload The data you want to send.
     */
    protected fun send(payload: Payload) {
        send(payload, mEstablishedConnections.keys)
    }

    private fun send(payload: Payload, endpoints: Set<String>) {
        mConnectionsClient!!
            .sendPayload(ArrayList(endpoints), payload)
            .addOnFailureListener { e -> println(e) }
    }

    /**
     * Someone connected to us has sent us data. Override this method to act on the event.
     *
     * @param endpoint The sender.
     * @param payload The data.
     */
    protected abstract fun onReceive(endpoint: Endpoint?, payload: Payload?)

    /** Returns the client's name. Visible to others when connecting.  */
    protected var name: String = "WhacAQuokka"

    /**
     * Returns the service id. This represents the action this connection is for. When discovering,
     * we'll verify that the advertiser has the same service id before we consider connecting to them.
     */
    protected val serviceId: String = "120002"

    /**
     * Returns the strategy we use to connect to other devices. Only devices using the same strategy
     * and service id will appear when discovering. Stragies determine how many incoming and outgoing
     * connections are possible at the same time, as well as how much bandwidth is available for use.
     */
    protected val strategy: Strategy? = Strategy.P2P_POINT_TO_POINT

    /** Represents a device we can talk to.  */
    class Endpoint constructor(val id: String, val name: String) {

        override fun equals(other: Any?): Boolean {
            if (other is Endpoint) {
                return id == other.id
            }
            return false
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun toString(): String {
            return String.format("Endpoint{id=%s, name=%s}", id, name)
        }
    }

    companion object {
        /**
         * An optional hook to pool any permissions the app needs with the permissions ConnectionsActivity
         * will request.
         *
         * @return All permissions required for the app to properly function.
         */
        /**
         * These permissions are required before connecting to Nearby Connections. Only [ ][Manifest.permission.ACCESS_COARSE_LOCATION] is considered dangerous, so the others should be
         * granted just by having them in our AndroidManfiest.xml
         */

        private const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

        /**
         * Transforms a [Status] into a English-readable message for logging.
         *
         * @param status The current status
         * @return A readable String. eg. [404]File not found.
         */
        private fun toString(status: Status): String {
            return String.format(
                Locale.US,
                "[%d]%s",
                status.statusCode,
                if (status.statusMessage != null) status.statusMessage else ConnectionsStatusCodes.getStatusCodeString(
                    status.statusCode
                )
            )
        }

        }
    }
