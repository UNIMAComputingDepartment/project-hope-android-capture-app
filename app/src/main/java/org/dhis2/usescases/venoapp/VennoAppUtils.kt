package org.dhis2.usescases.venoapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dhis2.IPseudoAssociatedCallback
import org.dhis2.IPseudonymDeleteCallback
import org.dhis2.IPseudonymGenerationCallback
import org.dhis2.IPseudonymService
import timber.log.Timber

const val packageName = "com.example.venoapp"
private const val SERVICE_CLASS_NAME = "$packageName.PseudonymService"
const val createBiometryActivityName = "com.example.venoapp.activities.CreateBiometryActivity"
const val updateBiometryActivityName = "com.example.venoapp.activities.UpdateBiometryActivity"
const val authenticateBiometryActivityName =
    "com.example.venoapp.activities.AuthenticateBiometryActivity"

private var pseudonymService: IPseudonymService? = null
private var serviceConnection: ServiceConnection? = null
fun launchVenoApp(context: Context, pseudonym: String, activityName: String) {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        component = ComponentName(packageName, activityName)
        putExtra("Pseudonym", pseudonym)
        putExtra("clientPackageName", clientPackageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    context.startActivity(intent)
}

fun bindToService(
    context: Context,
    onServiceConnected: (IPseudonymService) -> Unit,
    onServiceDisconnected: () -> Unit
) {
    if (pseudonymService != null) {
        onServiceConnected(pseudonymService!!)
        return
    }

    val serviceIntent = Intent().apply {
        component = ComponentName(packageName, SERVICE_CLASS_NAME)
    }

    serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            pseudonymService = IPseudonymService.Stub.asInterface(service)
            onServiceConnected(pseudonymService!!)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("VenoAppBinderService", "Service disconnected unexpectedly")
            pseudonymService = null
            onServiceDisconnected()
        }
    }

    try {
        context.packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES)
        if (!context.bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)) {
            Log.e("VenoAppBinderService", "Failed to bind service")
            Toast.makeText(context, "Unable to bind to the service. Please try again.", Toast.LENGTH_LONG).show()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("VenoAppBinderService", "Required app not installed: ${e.message}")
        Toast.makeText(context, "The required app is not installed. Please install it and try again.", Toast.LENGTH_LONG).show()
    }
}

fun unbindService(context: Context) {
    if (serviceConnection != null) {
        context.unbindService(serviceConnection!!)
        serviceConnection = null
        pseudonymService = null
    }
}

fun CreateBiometryActivity(
    context: Context,
    pseudonym: String,
    onLaunchComplete: () -> Unit
) {
    if (pseudonym.isEmpty()) {
        requestPseudonym(
            context,
            onPseudonymReceived = { receivedPseudonym ->


//                CoroutineScope(Dispatchers.Main).launch {
//                    try {
//                        val success = storePseudonym(receivedPseudonym)
//                        if (success) {
//                            launchVenoApp(context, receivedPseudonym, createBiometryActivityName)
//                        } else {
//                            deletePseudonym(context, receivedPseudonym)
//                        }
//                        onLaunchComplete()
//                    } catch (err: Exception) {
//                        deletePseudonym(context, receivedPseudonym)
//                        Log.e("createBiometryActivity", "Error in callback: ${err.message}")
//                        onLaunchComplete()
//                    }
//                }
            },
            onGenerationError = {
                Log.e("createBiometryActivity", "Error generating pseudonym: $it")
                onLaunchComplete()
            }
        )
    } else {
        launchVenoApp(context, pseudonym, createBiometryActivityName)
        onLaunchComplete()
    }
}

fun updateBiometryActivity(context: Context, pseudonym: String, onLaunchComplete: () -> Unit) {
    if (pseudonym.isEmpty()) {
        Toast.makeText(context, "Pseudonym is empty", Toast.LENGTH_LONG).show()
        onLaunchComplete()
        return
    }
    launchVenoApp(context, pseudonym, updateBiometryActivityName)
    onLaunchComplete()
}

fun authenticateBiometryActivity(
    context: Context,
    pseudonym: String,
    onLaunchComplete: () -> Unit
) {
    isPseudoAssociated(
        context,
        pseudonym,
        onResult = { isAssociated, message ->
            if (isAssociated) {
                launchVenoApp(context, pseudonym, authenticateBiometryActivityName)
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            onLaunchComplete()
        }
    )
}

fun requestPseudonym(
    context: Context,
    onPseudonymReceived: (String) -> Unit,
    onGenerationError: (String) -> Unit
) {
    bindToService(context, { service ->
        try {
            service.requestPseudonym(object : IPseudonymGenerationCallback.Stub() {
                override fun onPseudonymGenerated(pseudonym: String) {
                    onPseudonymReceived(pseudonym)
                }

                override fun onGenerateError(error: String?) {
                    onGenerationError(error ?: "Unknown error")
                }
            })
        } catch (e: RemoteException) {
            Log.e("VenoAppBinderService", "Error requesting pseudonym: ${e.message}")
            Toast.makeText(context, "Error requesting pseudonym", Toast.LENGTH_SHORT).show()
        }
    }, {})
}

fun deletePseudonym(context: Context, pseudonym: String) {
    bindToService(context, { service ->
        try {
            service.deletePseudonym(pseudonym, object : IPseudonymDeleteCallback.Stub() {
                override fun onPseudonymDeleted(isDeleted: Boolean, message: String) {}
            })
        } catch (e: RemoteException) {
            Log.e("VenoAppBinderService", "Error deleting pseudonym: ${e.message}")
        }
    }, {})
}

fun isPseudoAssociated(
    context: Context,
    pseudonym: String,
    onResult: (Boolean, String) -> Unit
) {
    bindToService(context, { service ->
        try {
            service.isPseudoAssociated(pseudonym, object : IPseudoAssociatedCallback.Stub() {
                override fun onPseudonymAssociatedResult(isAssociated: Boolean, message: String?) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onResult(isAssociated, message ?: "No message available")
                    }
                }
            })
        } catch (e: RemoteException) {
            Log.e("VenoAppBinderService", "Error checking associated pseudonym: ${e.message}")
            Toast.makeText(context, "Error checking pseudonym association", Toast.LENGTH_SHORT).show()
        }
    }, {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Failed to bind to pseudonym service", Toast.LENGTH_SHORT).show()
        }
    })
}