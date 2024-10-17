package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.dhis2.commons.data.EventViewModel
import org.dhis2.commons.data.StageSection
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model.MembershipProgramMapperModel
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

interface TeiDataRepository {
    fun getTEIEnrollmentEvents(
        selectedStage: StageSection,
        groupedByStage: Boolean,
    ): Single<List<EventViewModel>>

    fun getEnrollment(): Single<Enrollment?>
    fun getEnrollmentProgram(): Single<Program?>
    fun getTrackedEntityInstance(): Single<TrackedEntityInstance?>
    fun enrollingOrgUnit(): Single<OrganisationUnit>
    fun eventsWithoutCatCombo(): Single<List<EventViewModel>>
    fun getOrgUnitName(orgUnitUid: String): String
    fun getTeiProfilePath(): String?
    fun getTeiHeader(): String?
    fun isEventEditable(eventUid: String): Boolean
    fun displayOrganisationUnit(programUid: String): Boolean
    fun enrollmentOrgUnitInCaptureScope(enrollmentOrgUnit: String): Boolean
    fun programOrgListInCaptureScope(programUid: String): List<OrganisationUnit>

    fun getTeiRelationships(): Flowable<MembershipProgramMapperModel>

    fun getCurrentEnrollmentStatus(enrollmentUid: String): Flowable<EnrollmentStatus?>?

    fun getCurrentOrgUnit(enrollmentOrgUnit: String): Single<String>

    fun addClubMembers(members:List<String>): List<Flowable<String>>
}
