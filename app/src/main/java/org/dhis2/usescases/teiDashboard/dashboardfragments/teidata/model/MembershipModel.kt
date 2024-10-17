package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model

import org.hisp.dhis.android.core.enrollment.EnrollmentStatus

data class MembershipModel(val primaryAttribute:String,
                           val secondaryAttribute:String,
                           val tertiaryAttribute:String,
                           val enrollmentStatus:List<TargetEnrollmentStatus>,
                           val teiId:String,
                           val enrollmentId:String,
                           val programUid:String )