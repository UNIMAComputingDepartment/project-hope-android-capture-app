// VenoAppServiceImpl.kt
package org.dhis2.usescases.venoapp

import android.content.*
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.globalid.venoapp.IPseudoAssociatedCallback
import com.globalid.venoapp.IPseudonymDeleteCallback
import com.globalid.venoapp.IPseudonymGenerationCallback
import com.globalid.venoapp.IPseudonymService
import timber.log.Timber

class VenoAppServiceImpl : VenoAppService {

    companion object {
        const val packageName = "com.globalid.venoapp"
        private const val SERVICE_CLASS_NAME = "$packageName.PseudonymService"
        const val createBiometryActivityName = "com.globalid.venoapp.activities.CreateBiometryActivity"
        const val updateBiometryActivityName = "com.globalid.venoapp.activities.UpdateBiometryActivity"
        const val authenticateBiometryActivityName = "com.globalid.venoapp.activities.AuthenticateBiometryActivity"
    }

    private var pseudonymService: IPseudonymService? = null
    private var serviceConnection: ServiceConnection? = null

    override fun launchVenoApp(context: Context, pseudonym: String, activityName: String) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(packageName, activityName)
            putExtra("Pseudonym", pseudonym)
            putExtra("clientPackageName", context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    override fun bindToService(
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
            Timber.d(context.packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES).versionName)
            if (!context.bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)) {
                Log.e("VenoAppBinderService", "Failed to bind service")
                Toast.makeText(
                    context,
                    "Unable to bind to the service. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.tag("VenoAppBinderService").e("Required app not installed: " + e.message, e)
            Toast.makeText(
                context,
                "The required app is not installed. Please install it and try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun unbindService(context: Context) {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection!!)
            serviceConnection = null
            pseudonymService = null
        }
    }

    override fun createBiometryActivity(
        context: Context,
        pseudonym: String,
        onLaunchComplete: () -> Unit
    ) {
        if (pseudonym.isEmpty()) {
            requestPseudonym(
                context,
                onPseudonymReceived = { receivedPseudonym ->
                    launchVenoApp(context, receivedPseudonym, createBiometryActivityName)
                    onLaunchComplete()
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

    override fun updateBiometryActivity(
        context: Context,
        pseudonym: String,
        onLaunchComplete: () -> Unit
    ) {
        if (pseudonym.isEmpty()) {
            Toast.makeText(context, "Pseudonym is empty", Toast.LENGTH_LONG).show()
            onLaunchComplete()
            return
        }
        launchVenoApp(context, pseudonym, updateBiometryActivityName)
        onLaunchComplete()
    }

    override fun authenticateBiometryActivity(
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

    override fun requestPseudonym(
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

    override fun deletePseudonym(context: Context, pseudonym: String) {
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

    override fun isPseudoAssociated(
        context: Context,
        pseudonym: String,
        onResult: (Boolean, String) -> Unit
    ) {
        bindToService(context, { service ->
            try {
                service.isPseudoAssociated(pseudonym, object : IPseudoAssociatedCallback.Stub() {
                    override fun onPseudonymAssociatedResult(
                        isAssociated: Boolean,
                        message: String?
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            onResult(isAssociated, message ?: "No message available")
                        }
                    }
                })
            } catch (e: RemoteException) {
                Log.e(
                    "VenoAppBinderService",
                    "Error checking associated pseudonym: ${e.message}"
                )
                Toast.makeText(
                    context,
                    "Error checking pseudonym association",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Failed to bind to pseudonym service",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}