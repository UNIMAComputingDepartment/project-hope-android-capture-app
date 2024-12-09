// VenoAppService.kt
package org.dhis2.usescases.venoapp

import android.content.Context
import com.globalid.venoapp.IPseudonymService

interface VenoAppService {
    fun launchVenoApp(context: Context, pseudonym: String, activityName: String)
    fun bindToService(
        context: Context,
        onServiceConnected: (IPseudonymService) -> Unit,
        onServiceDisconnected: () -> Unit
    )
    fun unbindService(context: Context)

    fun updateBiometryActivity(context: Context, pseudonym: String, onLaunchComplete: () -> Unit)
    fun authenticateBiometryActivity(
        context: Context,
        pseudonym: String,
        onLaunchComplete: () -> Unit
    )
    fun requestPseudonym(
        context: Context,
        onPseudonymReceived: (String) -> Unit,
        onGenerationError: (String) -> Unit
    )
    fun deletePseudonym(context: Context, pseudonym: String)
    fun isPseudoAssociated(
        context: Context,
        pseudonym: String,
        onResult: (Boolean, String) -> Unit
    )

    fun createBiometryActivity(context: Context, pseudonym: String, onLaunchComplete: () -> Unit)
}