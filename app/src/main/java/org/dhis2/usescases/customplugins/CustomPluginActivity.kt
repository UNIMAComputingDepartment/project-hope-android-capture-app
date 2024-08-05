package org.dhis2.usescases.customplugins

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.dhis2.PluginDownloader
import org.dhis2.PluginManager
import org.dhis2.usescases.general.ActivityGlobalAbstract
import org.hisp.dhis.mobile.ui.designsystem.component.SubTitle
import org.hisp.dhis.mobile.ui.designsystem.component.Title

class CustomPluginActivity : ActivityGlobalAbstract() {

    private var isDownloading = true
    override fun onCreate(savedInstanceState: Bundle?) {
        val pluginDownloader = PluginDownloader(applicationContext)
        val pluginUrl =
            "https://raw.githubusercontent.com/dhis2/dhis2-android-capture-app/ANDROAPP-5502-PoC-Plug-play-modules/myplugin-debug.apk"

        lifecycleScope.launch {
            val downloadedFile = pluginDownloader.downloadPlugin(pluginUrl)

            setContent {
                var isLoading by remember {
                    mutableStateOf(true)
                }

                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .background(Color.White),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                        Title(text = "Downloading Plugin... this may take a while.")
                        SubTitle(text = "Ensure you have a stable internet connection.")
                    }
                }
                if (downloadedFile != null) {
                    val pluginManager = PluginManager(context)
                    isLoading = false
                    val plugin = pluginManager.loadPlugin(downloadedFile)
                    plugin?.Show() ?: Text(text = "Plugin not loaded")
                }

                if (downloadedFile != null) {
                    val pluginManager = PluginManager(context)
                    isLoading = false
                    val plugin = pluginManager.loadPlugin(downloadedFile)
                    plugin?.Show() ?: Text(text = "Plugin not loaded")
                }
            }
        }
        super.onCreate(savedInstanceState)
    }
}

@Composable fun CustomPluginView() {
    Column {
        Text(text = "Custom Plugin")
    }
}
