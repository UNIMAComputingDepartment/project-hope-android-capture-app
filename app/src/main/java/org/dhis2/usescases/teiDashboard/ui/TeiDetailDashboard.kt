package org.dhis2.usescases.teiDashboard.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.commit
import org.dhis2.R
import org.dhis2.commons.data.EventCreationType
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.TEIDataFragment
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model.MembershipModel
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model.MembershipProgramMapperModel
import org.dhis2.usescases.teiDashboard.ui.model.InfoBarUiModel
import org.dhis2.usescases.teiDashboard.ui.model.TeiCardUiModel
import org.dhis2.usescases.teiDashboard.ui.model.TimelineEventsHeaderModel
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.mobile.ui.designsystem.component.CardDetail
import org.hisp.dhis.mobile.ui.designsystem.component.InfoBar
import org.hisp.dhis.mobile.ui.designsystem.component.InfoBarData
import org.hisp.dhis.mobile.ui.designsystem.theme.Spacing
import org.hisp.dhis.mobile.ui.designsystem.theme.SurfaceColor

@Composable
fun TeiDetailDashboard(
    syncData: InfoBarUiModel?,
    followUpData: InfoBarUiModel?,
    enrollmentData: InfoBarUiModel?,
    card: TeiCardUiModel?,
    timelineEventHeaderModel: TimelineEventsHeaderModel,
    isGrouped: Boolean = true,
    timelineOnEventCreationOptionSelected: (EventCreationType) -> Unit,
    relationshipMembers: MembershipProgramMapperModel?,
    currentProgramId: String?,
    onCreateMemberClick: () -> Unit = {},
    canCreateTeiRelationship: Boolean = false,
    graduatedSessions: Int = 0,
    onGoingSessions: Int = 0
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        if (syncData?.showInfoBar == true) {
            InfoBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .testTag(SYNC_INFO_BAR_TEST_TAG),
                infoBarData =
                InfoBarData(
                    text = syncData.text,
                    icon = syncData.icon,
                    color = syncData.textColor,
                    backgroundColor = syncData.backgroundColor,
                    actionText = syncData.actionText,
                    onClick = syncData.onActionClick,
                ),
            )
            if (followUpData?.showInfoBar == true || enrollmentData?.showInfoBar == true) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (followUpData?.showInfoBar == true) {
            InfoBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .testTag(FOLLOWUP_INFO_BAR_TEST_TAG),
                infoBarData = InfoBarData(
                    text = followUpData.text,
                    icon = followUpData.icon,
                    color = followUpData.textColor,
                    backgroundColor = followUpData.backgroundColor,
                    actionText = followUpData.actionText,
                    onClick = followUpData.onActionClick,
                ),
            )
            if (enrollmentData?.showInfoBar == true) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (enrollmentData?.showInfoBar == true) {
            InfoBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .testTag(STATE_INFO_BAR_TEST_TAG),
                infoBarData = InfoBarData(
                    text = enrollmentData.text,
                    icon = enrollmentData.icon,
                    color = enrollmentData.textColor,
                    backgroundColor = enrollmentData.backgroundColor,
                    actionText = enrollmentData.actionText,
                ),
            )
        }

        card?.let {
            CardDetail(
                title = card.title,
                additionalInfoList = card.additionalInfo,
                avatar = card.avatar,
                actionButton = card.actionButton,
                expandLabelText = card.expandLabelText,
                shrinkLabelText = card.shrinkLabelText,
                showLoading = card.showLoading,
            )
        }

        if (currentProgramId == "JuDBc7Wx3wG") {
            GraduationStatusView(
                completedEnrollments = graduatedSessions,
                activeEnrollments = onGoingSessions)
        }


        relationshipMembers?.let {
            if (it.program == currentProgramId) {
                MemberShipContent(
                    members = it,
                    onCreateMemberClick = onCreateMemberClick,
                    canCreateTeiRelationship = canCreateTeiRelationship
                )
            }
        }

        if (!isGrouped) {
            Spacer(modifier = Modifier.size(Spacing.Spacing16))
            TimelineEventsHeader(
                timelineEventsHeaderModel = timelineEventHeaderModel,
                onOptionSelected = timelineOnEventCreationOptionSelected,
            )
            Spacer(modifier = Modifier.size(Spacing.Spacing8))
        }
    }
}

const val SYNC_INFO_BAR_TEST_TAG = "sync"
const val FOLLOWUP_INFO_BAR_TEST_TAG = "followUp"
const val STATE_INFO_BAR_TEST_TAG = "state"


@Composable
fun MemberShipContent(
    modifier: Modifier = Modifier,
    members: MembershipProgramMapperModel,
    onCreateMemberClick: () -> Unit = {},
    canCreateTeiRelationship: Boolean
) {

    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotationState"
    )

    Column {
        Card(
            modifier = modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )

            ).padding(start = Spacing.Spacing16, end = Spacing.Spacing16)
                .clickable { isExpanded = !isExpanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_groups_24),
                        contentDescription = null,
                        modifier = Modifier.padding(Spacing.Spacing4)
                    )
                    Spacer(modifier = Modifier.padding(Spacing.Spacing4))
                    Text(members.relationshipDescription, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.padding(start = Spacing.Spacing8))
                    IconButton(modifier = Modifier.alpha(ContentAlpha.medium)
                        .rotate(rotationState),
                        onClick = {
                            isExpanded = !isExpanded
                        }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Drop down arrow"
                        )
                    }

                }

                if (canCreateTeiRelationship) {
                    IconButton(modifier = Modifier.alpha(ContentAlpha.medium),
                        onClick = {
                            onCreateMemberClick()
                        }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Drop down arrow",
                            tint = SurfaceColor.Primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

            }
        }

        if (isExpanded) {

            members.members.map {
                MembershipContentItem(
                    it,
                    isTrackingEnrollmentDefined = members.isTrackingEnrollmentDefined,
                    programName = members.trackingProgramName
                )
            }
        }
    }
}

@SuppressLint("CommitTransaction")
@Composable
fun MembershipContentItem(
    member: MembershipModel = MembershipModel(
        "Augustine Simwela",
        "2003-01-12",
        "male",
        listOf(),
        teiId = "tring",
        enrollmentId = "String",
        programUid = "dd"
    ),
    programName: String? = null,
    isTrackingEnrollmentDefined: Boolean = false,
) {
    val completedEnrollments =
        member.enrollmentStatus.filter { it.enrollmentStatus == EnrollmentStatus.COMPLETED }.size
    val activeEnrollmentStatus =
        member.enrollmentStatus.filter { it.enrollmentStatus == EnrollmentStatus.ACTIVE }.size

    val currentContext = LocalContext.current

    val activity = LocalContext.current as? AppCompatActivity ?: return
    Row(
        modifier = Modifier.fillMaxWidth().padding(
            start = Spacing.Spacing16,
            end = Spacing.Spacing16,
            top = Spacing.Spacing8,
            bottom = Spacing.Spacing8
        ).clickable {
            val intent = Intent(currentContext, activity::class.java).apply {
                putExtra("PROGRAM_UID", member.programUid)
                putExtra("TEI_UID", member.teiId)
                putExtra("ENROLLMENT_UID", member.enrollmentId)
            }
            currentContext.startActivity(intent)
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.padding(Spacing.Spacing4),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painterResource(R.drawable.ic_form_person),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(member.secondaryAttribute, fontSize = 10.sp)
            }
            Divider(modifier = Modifier.width(1.dp).height(10.dp))
            Column(modifier = Modifier.padding(Spacing.Spacing4)) {
                Text(text = member.primaryAttribute, fontSize = 18.sp)
                Text(member.tertiaryAttribute, fontSize = 12.sp)
            }
        }

        if (isTrackingEnrollmentDefined) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$programName enrollments",
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (activeEnrollmentStatus > 0 && completedEnrollments > 0) {
                    Row {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(R.drawable.ic_check_circle_36),
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = Color(0xFF387F39)
                            )
                            Text(
                                "$completedEnrollments completed",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.padding(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(R.drawable.ic_unchecked_circle_36),
                                contentDescription = null,
                                modifier = Modifier.size(10.dp)
                            )
                            Text("$activeEnrollmentStatus active", fontSize = 9.sp)
                        }

                    }
                } else if (activeEnrollmentStatus > 0 && completedEnrollments == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.ic_unchecked_circle_36),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = Color(0xFF387F39)
                        )
                        Text(
                            "$activeEnrollmentStatus active",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 9.sp
                        )
                    }
                } else if (completedEnrollments > 0 && activeEnrollmentStatus == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.ic_check_circle_36),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = Color(0xFF387F39)
                        )
                        Text(
                            "$completedEnrollments completed",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 9.sp
                        )
                    }
                } else if (completedEnrollments == 0 && activeEnrollmentStatus == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.ic_warning),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = SurfaceColor.Warning
                        )
                        Text(
                            "not enrolled",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 9.sp
                        )
                    }
                }


            }

        }


    }

    Divider(thickness = Spacing.Spacing1, startIndent = 58.dp)

}

@Composable
@Preview(showBackground = true)
fun GraduationStatusView(completedEnrollments: Int = 0, activeEnrollments: Int = 0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(Spacing.Spacing16)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.ic_check_circle_36),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF387F39)
            )
            Spacer(modifier = Modifier.padding(Spacing.Spacing4))
            Text(
                "$completedEnrollments graduations",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.baseline_graduation_progress),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = SurfaceColor.Warning
            )
            Spacer(modifier = Modifier.padding(Spacing.Spacing4))
            Text(
                "$activeEnrollments ongoing",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )
        }
    }
}
