package com.and.whacaquokkaapplication.bluetoothmanager

import android.util.Log
import com.google.android.gms.nearby.connection.*
import android.os.Build

/**
 * Inspiré par :
 * - https://medium.com/@TusharKhattar/google-nearby-connection-persisting-connection-across-different-activities-1ffbc428df5
 * - https://github.com/googlearchive/android-nearby/blob/master/connections/walkietalkie/app/src/main/java/com/google/location/nearby/apps/walkietalkie/ConnectionsActivity.java
 */
class BluetoothConnectionService private constructor() {


    interface AdvertisingListener {
        /** Called when advertising successfully starts. Override this method to act on the event.  */
        fun onAdvertisingStarted()

        /** Called when advertising fails to start. Override this method to act on the event.  */
        fun onAdvertisingFailed()
    }

    interface ConnectionListener {
        /**
         * Called when a pending connection with a remote endpoint is created. Use [ConnectionInfo]
         * for metadata about the connection (like incoming vs outgoing, or the authentication token). If
         * we want to continue with the connection, call [.acceptConnection]. Otherwise,
         * call [.rejectConnection].
         */
        fun onConnectionInitiated(
            endpoint: Endpoint?,
            connectionInfo: ConnectionInfo?
        )

        /**
         * Called when a connection with this endpoint has failed. Override this method to act on the
         * event.
         */
        fun onConnectionFailed(endpoint: Endpoint?)
    }

    interface DiscoveringListener {
        /** Called when discovery successfully starts. Override this method to act on the event.  */
        fun onDiscoveryStarted()

        /** Called when discovery fails to start. Override this method to act on the event.  */
        fun onDiscoveryFailed()
    }

    interface EndpointListener {
        /**
         * Called when a remote endpoint is discovered. To connect to the device, call [ ][.connectToEndpoint].
         */
        fun onEndpointDiscovered(endpoint: Endpoint?)

        /** Called when someone has connected to us. Override this method to act on the event.  */
        fun onEndpointConnected(endpoint: Endpoint?)

        /** Called when someone has disconnected. Override this method to act on the event.  */
        fun onEndpointDisconnected(endpoint: Endpoint?)
    }

    interface DataListener {
        /**
         * Someone connected to us has sent us data. Override this method to act on the event.
         *
         * @param endpoint The sender.
         * @param payload The data.
         */
        fun onReceive(endpoint: Endpoint?, payload: Payload?)
    }



    /** Listener for data received events.  */
    var dataListener: DataListener? = null

    /** Listener for connection events.  */
    var connectionListener: ConnectionListener? = null

    /** Listener for endpoint discovery events.  */
    var endpointListener: EndpointListener? = null

    /** Listener for advertising events.  */
    var advertisingListener: AdvertisingListener? = null

    /** Listener for discovery events.  */
    var discoveringListener: DiscoveringListener? = null


    /** Our handler to Nearby Connections.  */
    var mConnectionsClient: ConnectionsClient? = null

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
    private val mEstablishedConnections: MutableMap<String, Endpoint?> =
        HashMap()
    /** Returns `true` if we're currently attempting to connect to another device.  */
    /**
     * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
     * device.
     */
    var isConnecting = false
        private set
    /** Returns `true` if currently discovering.  */
    /** True if we are discovering.  */
    var isDiscovering = false
        private set
    /** Returns `true` if currently advertising.  */
    /** True if we are advertising.  */
    var isAdvertising = false
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
                connectionListener?.onConnectionInitiated(endpoint, connectionInfo)
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
                    connectionListener?.onConnectionFailed(
                        mPendingConnections.remove(
                            endpointId
                        )
                    )
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
            dataListener?.onReceive(mEstablishedConnections[endpointId], payload)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            String.format(
                "onPayloadTransferUpdate(endpointId=%s, update=%s)", endpointId, update
            )
        }
    }


    private fun connectedToEndpoint(endpoint: Endpoint?) {
        mEstablishedConnections[endpoint!!.id] = endpoint
        endpointListener?.onEndpointConnected(endpoint)
    }

    private fun disconnectedFromEndpoint(endpoint: Endpoint?) {
        mEstablishedConnections.remove(endpoint!!.id)
        endpointListener?.onEndpointDisconnected(endpoint)
    }

    private fun send(payload: Payload, endpoints: Set<String>) {
        mConnectionsClient!!
            .sendPayload(ArrayList(endpoints), payload)
            .addOnFailureListener { e -> println(e) }
    }


    /** Returns the client's name. Visible to others when connecting.  */

    var name: String = "${Build.BRAND} ${Build.MODEL}"

    /**
     * Returns the service id. This represents the action this connection is for. When discovering,
     * we'll verify that the advertiser has the same service id before we consider connecting to them.
     */
    private val serviceId: String = "120002"

    /**
     * Returns the strategy we use to connect to other devices. Only devices using the same strategy
     * and service id will appear when discovering. Stragies determine how many incoming and outgoing
     * connections are possible at the same time, as well as how much bandwidth is available for use.
     */
    private val strategy: Strategy = Strategy.P2P_POINT_TO_POINT

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
        val instance = BluetoothConnectionService()

        fun startAdvertising() {
            instance.isAdvertising = true

            val localEndpointName = instance.name
            val advertisingOptions = AdvertisingOptions.Builder()
            advertisingOptions.setStrategy(instance.strategy)
            instance.mConnectionsClient!!
                .startAdvertising(
                    localEndpointName,
                    instance.serviceId,
                    instance.mConnectionLifecycleCallback,
                    advertisingOptions.build()
                )
                .addOnSuccessListener {
                    instance.advertisingListener?.onAdvertisingStarted()
                }
                .addOnFailureListener {
                    instance.isAdvertising = false
                    instance.advertisingListener?.onAdvertisingFailed()
                }
        }

        /** Stops advertising.  */
        fun stopAdvertising() {
            instance.isAdvertising = false
            instance.mConnectionsClient!!.stopAdvertising()
        }

        /** Accepts a connection request.  */
        fun acceptConnection(endpoint: Endpoint) {
            Log.println(Log.INFO, "BluetoothConnectionService", "acceptConnection($endpoint)")
            instance.mConnectionsClient!!
                .acceptConnection(endpoint.id, instance.mPayloadCallback)
                .addOnFailureListener { e -> println(e) }
        }

        /** Rejects a connection request.  */
        fun rejectConnection(endpoint: Endpoint) {
            instance.mConnectionsClient!!
                .rejectConnection(endpoint.id)
                .addOnFailureListener { e -> println(e) }
        }

        /**
         * Sets the device to discovery mode. It will now listen for devices in advertising mode. Either
         * [.onDiscoveryStarted] or [.onDiscoveryFailed] will be called once we've found
         * out if we successfully entered this mode.
         */
        fun startDiscovering() {
            instance.isDiscovering = true
            instance.mDiscoveredEndpoints.clear()
            val discoveryOptions = DiscoveryOptions.Builder()
            discoveryOptions.setStrategy(instance.strategy!!)
            instance.mConnectionsClient!!
                .startDiscovery(
                    instance.serviceId,
                    object : EndpointDiscoveryCallback() {
                        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                            String.format(
                                "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                                endpointId, info.serviceId, info.endpointName
                            )
                            if (instance.serviceId == info.serviceId) {
                                val endpoint =
                                    Endpoint(endpointId, info.endpointName)
                                instance.mDiscoveredEndpoints[endpointId] = endpoint
                                instance.endpointListener?.onEndpointDiscovered(endpoint)
                            }
                        }

                        override fun onEndpointLost(endpointId: String) {
                        }
                    },
                    discoveryOptions.build()
                )
                .addOnSuccessListener { instance.discoveringListener?.onDiscoveryStarted() }
                .addOnFailureListener { e ->
                    instance.isDiscovering = false
                    instance.discoveringListener?.onDiscoveryFailed()
                }
        }

        /** Stops discovery.  */
        fun stopDiscovering() {
            instance.isDiscovering = false
            instance.mConnectionsClient!!.stopDiscovery()
        }


        /** Disconnects from the given endpoint.  */
        fun disconnect(endpoint: Endpoint) {
            instance.mConnectionsClient!!.disconnectFromEndpoint(endpoint.id)
            instance.mEstablishedConnections.remove(endpoint.id)
        }

        /** Disconnects from all currently connected endpoints.  */
        fun disconnectFromAllEndpoints() {
            for (endpoint in instance.mEstablishedConnections.values) {
                instance.mConnectionsClient!!.disconnectFromEndpoint(endpoint!!.id)
            }
            instance.mEstablishedConnections.clear()
        }

        /** Resets and clears all state in Nearby Connections.  */
        fun stopAllEndpoints() {
            instance.mConnectionsClient!!.stopAllEndpoints()
            instance.isAdvertising = false
            instance.isDiscovering = false
            instance.isConnecting = false
            instance.mDiscoveredEndpoints.clear()
            instance.mPendingConnections.clear()
            instance.mEstablishedConnections.clear()
        }

        /**
         * Sends a connection request to the endpoint. Either [.onConnectionInitiated] or [.onConnectionFailed] will be called once we've found out
         * if we successfully reached the device.
         */
        fun connectToEndpoint(endpoint: Endpoint) {
            // Mark ourselves as connecting so we don't connect multiple times
            instance.isConnecting = true

            // Ask to connect
            instance.mConnectionsClient!!
                .requestConnection(instance.name!!, endpoint.id, instance.mConnectionLifecycleCallback)
                .addOnFailureListener {
                    instance.isConnecting = false
                    instance.connectionListener?.onConnectionFailed(endpoint)
                }
        }

        /**
         * Remove all listeners.
         */
        fun removeListener(){
            instance.connectionListener = null
            instance.dataListener = null
            instance.endpointListener = null
            instance.advertisingListener = null
            instance.discoveringListener = null
        }



        /** Returns a list of currently connected endpoints.  */
        val discoveredEndpoints: Set<Endpoint>
            get() = HashSet(instance.mDiscoveredEndpoints.values)

        /** Returns a list of currently connected endpoints.  */
        val connectedEndpoints: Set<Endpoint?>
            get() = HashSet(instance.mEstablishedConnections.values)

        /**
         * Sends a [Payload] to all currently connected endpoints.
         *
         * @param payload The data you want to send.
         */
        fun send(payload: Payload) {
            instance.send(payload, instance.mEstablishedConnections.keys)
        }

        fun stopAll(){
            stopAllEndpoints()
            stopAdvertising()
            stopDiscovering()
        }
    }
}