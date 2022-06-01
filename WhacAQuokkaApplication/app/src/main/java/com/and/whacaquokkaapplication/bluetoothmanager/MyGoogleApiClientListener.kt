package com.and.whacaquokkaapplication.bluetoothmanager

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.Payload


interface MyGoogleApiClientListener {
    fun onConnected()
    fun onConnectionFailed()
    fun onConnectionSuspended(reason: Int)
}

interface MyNearbyAdvertisingListener {
    fun onAdvertisingStarted()
    fun onAdvertisingFailed()
}

interface MyNearbyConnectionListener {
    fun onConnectionInitiated(
        endpoint: ConnectionsActivity.Endpoint?,
        connectionInfo: ConnectionInfo?
    )

    fun onConnectionFailed(endpoint: ConnectionsActivity.Endpoint?)
}

interface MyNearbyDataListener {
    fun onReceive(endpoint: ConnectionsActivity.Endpoint?, payload: Payload?)
}

interface MyNearbyDiscoveringListener {
    fun onDiscoveryStarted()
    fun onDiscoveryFailed()
}

interface MyNearbyEndpointListener {
    fun onEndpointDiscovered(endpoint: ConnectionsActivity.Endpoint?)
    fun onEndpointConnected(endpoint: ConnectionsActivity.Endpoint?)
    fun onEndpointDisconnected(endpoint: ConnectionsActivity.Endpoint?)
}
