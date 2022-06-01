package com.and.whacaquokkaapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Utility class that provide static methods for permissions handling.
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class Permission {
    companion object {

        /**
         * Code used when requestPermissionsDiscovery call requestPermissions.
         */
        val DISCOVERY_CODE = 1

        /**
         * Code used when requestPermissionsAdvertising call requestPermissions.
         */
        val ADVERTISING_CODE = 2

        /**
         * Permissions required by WhacAQuokkaApplication
         */
        private val PERMISSIONS = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        )

        /**
         * Asks for permissions with discovery code.
         */
        fun requestPermissionsDiscovery(activity: Activity) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, DISCOVERY_CODE);
        }

        /**
         * Asks for permissions with advertising code.
         */
        fun requestPermissionsAdvertising(activity: Activity) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, ADVERTISING_CODE);
        }

        /**
         * Check if all required permissions are granted.
         */
        fun hasPermissions(context: Context): Boolean = PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    }
}