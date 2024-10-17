package org.dhis2.usescases.workflowredesign

import io.reactivex.Flowable
import io.reactivex.Observable
import org.dhis2.commons.data.tuples.Pair
import org.dhis2.usescases.workflowredesign.model.AutoEnrollmentConfig
import org.dhis2.usescases.workflowredesign.model.RelationshipConfig
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue

interface WorkflowRedesignManager {

    fun getCurrentEventDataValues(eventUid: String): Flowable<List<TrackedEntityDataValue>>

    fun getCurrentEventTrackedEntityInstance(eventUid: String): Flowable<String?>?

    fun getAutoEnrollmentConfiguration(): Flowable<AutoEnrollmentConfig>


    fun createEnrollments(
        programIds: List<String>,
        entity: String?,
        orgUnit: String?
    ): Flowable<String>

    fun teIGraduationStatus(tei:String): Observable<List<Pair<RelationshipType?, String>>>?

    fun getRelationshipDefitionConfiguration():Flowable<List<RelationshipConfig>>

    fun createTei()
}