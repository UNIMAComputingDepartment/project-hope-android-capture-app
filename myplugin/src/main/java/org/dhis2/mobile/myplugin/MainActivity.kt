package org.dhis2.mobile.myplugin

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import org.dhis2.mobile.myplugin.ui.theme.Dhis2androidcaptureappTheme
import org.dhis2.mobile.myplugin.ui.theme.MainViewModel
import org.dhis2.mobile.myplugin.ui.theme.ProgramItem
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.mobile.ui.designsystem.component.AdditionalInfoItem
import org.hisp.dhis.mobile.ui.designsystem.component.AdditionalInfoItemColor
import org.hisp.dhis.mobile.ui.designsystem.component.CardDetail
import org.hisp.dhis.mobile.ui.designsystem.component.ListCard
import org.hisp.dhis.mobile.ui.designsystem.component.ListCardTitleModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dhis2androidcaptureappTheme {
                val programs = viewModel.programList.observeAsState(listOf())

//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//
                MainScreen(this, viewModel, programs.value)
//                }
            }
        }
    }

    @Composable
    fun MainScreen(
        activity: Activity,
        viewModel: MainViewModel = viewModel(),
        programs: List<ProgramItem>
    ) {

        Scaffold(modifier = Modifier.fillMaxSize()) {
                innerPadding ->

            val programList: List<ProgramItem> = programs

            LazyColumn(
                Modifier.padding(top = 16.dp),
            ) {
                items(programList) {
                    ProgramCard(program = it.program)
                }
            }
            Column(modifier = Modifier.padding(innerPadding)) {
                CardDetail(
                    title = "This is  new content with activity lifecycle and hopefully sdk requests!!!",
                    additionalInfoList = listOf(
                        AdditionalInfoItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Android,
                                    contentDescription = "This is my amazing plugin!!!",
                                    tint = AdditionalInfoItemColor.SUCCESS.color,
                                )
                            },
                            value = "Lets Rock",
                            color = AdditionalInfoItemColor.ERROR.color,
                            isConstantItem = true,
                        ),
                    ),
                )
            }
        }
    }


    @Composable
    fun ProgramCard(program: Program) {
        ListCard(title = ListCardTitleModel(text =  program.displayName() ?: "-"),
            additionalInfoList = getAdditionalInfoList(program),
            onCardClick = { navigateToProgramDetailScreen(program) })
    }

    private fun navigateToProgramDetailScreen(program: Program) {

    }

    private fun getAdditionalInfoList(program: Program): List<AdditionalInfoItem> {
        val detailsList: MutableList<AdditionalInfoItem> = mutableListOf()
        program.programType()?.name.let {
            detailsList.add(AdditionalInfoItem(key = "Type", value = it?: "-"))
        }

        program.trackedEntityType()?.name().let {
            detailsList.add(AdditionalInfoItem(key = "Tracked Entity Type", value = it?: "-"))
        }

        program.displayEventLabel().let {
            detailsList.add(AdditionalInfoItem(key = "Event Label", value = it?: "-"))
        }



        return detailsList
    }
}

@HiltAndroidApp
class CoreApplication: Application()

