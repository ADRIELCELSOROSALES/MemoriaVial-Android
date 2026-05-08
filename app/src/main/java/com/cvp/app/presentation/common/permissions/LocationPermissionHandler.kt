package com.cvp.app.presentation.common.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

sealed interface LocationPermissionState {
    data object NotRequested : LocationPermissionState
    data object Granted : LocationPermissionState
    data object Denied : LocationPermissionState
    data object PermanentlyDenied : LocationPermissionState
}

@Stable
class LocationPermissionHandler(
    val state: LocationPermissionState,
    val requestPermission: () -> Unit,
    val openAppSettings: () -> Unit,
)

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

@Composable
fun rememberLocationPermissionState(): LocationPermissionHandler {
    val context = LocalContext.current
    val activity = context as? Activity

    var state by remember {
        val alreadyGranted = LOCATION_PERMISSIONS.any { perm ->
            ActivityCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
        mutableStateOf(
            if (alreadyGranted) LocationPermissionState.Granted
            else LocationPermissionState.NotRequested
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val anyGranted = results.values.any { it }
        state = if (anyGranted) {
            LocationPermissionState.Granted
        } else {
            // shouldShowRationale = true means a previous denial — show rationale and try again.
            // false after denial means permanently denied (checked "don't ask again").
            val shouldShowRationale = LOCATION_PERMISSIONS.any { perm ->
                activity?.shouldShowRequestPermissionRationale(perm) == true
            }
            if (shouldShowRationale) LocationPermissionState.Denied
            else LocationPermissionState.PermanentlyDenied
        }
    }

    return remember(state) {
        LocationPermissionHandler(
            state = state,
            requestPermission = { launcher.launch(LOCATION_PERMISSIONS) },
            openAppSettings = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
            },
        )
    }
}
