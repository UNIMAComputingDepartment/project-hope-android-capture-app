package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model

import org.hisp.dhis.android.core.enrollment.EnrollmentStatus

data class TargetEnrollmentStatus(
                                   val targetProgram:String,
                                   val enrollmentStatus: EnrollmentStatus)