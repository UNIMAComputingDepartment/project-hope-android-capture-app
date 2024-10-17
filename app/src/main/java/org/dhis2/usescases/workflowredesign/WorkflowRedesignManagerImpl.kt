package org.dhis2.usescases.workflowredesign

import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.Observable
import org.dhis2.commons.data.tuples.Pair
import org.dhis2.commons.date.DateUtils
import org.dhis2.usescases.workflowredesign.model.AutoEnrollmentConfig
import org.dhis2.usescases.workflowredesign.model.RelationshipConfig
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.enrollment.EnrollmentAccess
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue

class WorkflowRedesignManagerImpl(private val d2: D2) : WorkflowRedesignManager {
    override fun getCurrentEventDataValues(eventUid: String): Flowable<List<TrackedEntityDataValue>> {
        return d2.trackedEntityModule().trackedEntityDataValues().byEvent().eq(eventUid).get()
            .toFlowable()
    }

    override fun getCurrentEventTrackedEntityInstance(eventUid: String): Flowable<String?>? {
        val currentEnrollment = d2.eventModule().events().uid(eventUid).blockingGet()?.enrollment()
        return d2.enrollmentModule().enrollments().uid(currentEnrollment).get()
            .map { ob -> ob.trackedEntityInstance() }
            .toFlowable()
    }

    override fun getAutoEnrollmentConfiguration(): Flowable<AutoEnrollmentConfig> {
        val ifExists = d2.dataStoreModule().dataStore().byNamespace().eq("workflow_redesign")
            .byKey().eq("configs").one().blockingGet()
        return if (ifExists != null) {
            d2.dataStoreModule().dataStore().byNamespace().eq("workflow_redesign")
                .byKey().eq("configs").one().get()
                .toFlowable()
                .map {
                    Gson().fromJson(
                        it.value(),
                        AutoEnrollmentConfig::class.java
                    )
                }
        } else Flowable.just(
            Gson().fromJson(
                AutoEnrollmentConfig.createDefaultAutoEnrollmentConfigObject(),
                AutoEnrollmentConfig::class.java
            )
        )
    }

    override fun createEnrollments(
        programIds: List<String>,
        entity: String?,
        orgUnit: String?
    ): Flowable<String> {


        return Flowable.fromIterable(programIds).map { id: String? ->
            val hasEnrollmentAccess = d2.enrollmentModule().enrollmentService()
                .blockingGetEnrollmentAccess(entity!!, id!!)
            val enrollementDoesnottExists = d2.enrollmentModule()
                .enrollments().byTrackedEntityInstance()
                .eq(entity).byProgram().eq(id).blockingIsEmpty()


            val orgUnitHasPrgoramAccess =
                d2.programModule().programs().byOrganisationUnitUid(orgUnit!!)
                    .byOrganisationUnitScope(
                        OrganisationUnit.Scope.SCOPE_DATA_CAPTURE
                    ).uid(id).blockingGet()

            if ((hasEnrollmentAccess == EnrollmentAccess.WRITE_ACCESS)
                && enrollementDoesnottExists && orgUnitHasPrgoramAccess != null
            ) {
                val enrollment = d2.enrollmentModule().enrollments()
                    .blockingAdd(
                        EnrollmentCreateProjection.builder()
                            .trackedEntityInstance(entity)
                            .program(id)
                            .organisationUnit(orgUnit)
                            .build()
                    )
                d2.enrollmentModule().enrollments().uid(enrollment)
                    .setEnrollmentDate(DateUtils.getInstance().today)
                d2.enrollmentModule().enrollments().uid(enrollment)
                    .setIncidentDate(DateUtils.getInstance().today)
                d2.enrollmentModule().enrollments().uid(enrollment).setFollowUp(false)
                enrollment
            } else {
                return@map ""
            }
        }
    }

    override fun teIGraduationStatus(teType: String): Observable<List<Pair<RelationshipType?, String>>>? {
        return d2.systemInfoModule().systemInfo().get().toObservable()
            .map<String?> { obj: SystemInfo -> obj.version() }
            .flatMap { version: String? ->
                if (version == "2.29") {
                    return@flatMap d2.relationshipModule().relationshipTypes()
                        .get().toObservable()
                        .flatMapIterable<RelationshipType?> { list: List<RelationshipType?>? -> list }
                        .map<Pair<RelationshipType?, String>> { relationshipType: RelationshipType? ->
                            Pair.create<RelationshipType?, String>(
                                relationshipType!!,
                                teType,
                            )
                        }.toList().toObservable()
                } else {
                    return@flatMap d2.relationshipModule()
                        .relationshipTypes().withConstraints().get()
                        .map<List<Pair<RelationshipType?, String>>> { relationshipTypes: List<RelationshipType> ->
                            val relTypeList: MutableList<Pair<RelationshipType?, String>> =
                                java.util.ArrayList()
                            for (relationshipType in relationshipTypes) {
                                if (relationshipType.fromConstraint() != null && relationshipType.fromConstraint()!!
                                        .trackedEntityType() != null && relationshipType.fromConstraint()!!
                                        .trackedEntityType()!!.uid() == teType
                                ) {
                                    if (relationshipType.toConstraint() != null && relationshipType.toConstraint()!!
                                            .trackedEntityType() != null
                                    ) {
                                        relTypeList.add(
                                            Pair.create(
                                                relationshipType,
                                                relationshipType.toConstraint()!!
                                                    .trackedEntityType()!!.uid(),
                                            ),
                                        )
                                    }
                                } else if (relationshipType.bidirectional()!! && relationshipType.toConstraint() != null && relationshipType.toConstraint()!!
                                        .trackedEntityType() != null && relationshipType.toConstraint()!!
                                        .trackedEntityType()!!
                                        .uid() == teType
                                ) {
                                    if (relationshipType.fromConstraint() != null && relationshipType.fromConstraint()!!
                                            .trackedEntityType() != null
                                    ) {
                                        relTypeList.add(
                                            Pair.create(
                                                relationshipType,
                                                relationshipType.fromConstraint()!!
                                                    .trackedEntityType()!!.uid(),
                                            ),
                                        )
                                    }
                                }
                            }
                            relTypeList.toList()
                        }.toObservable()
                }
            }

    }

    override fun getRelationshipDefitionConfiguration(): Flowable<List<RelationshipConfig>> {
        val configs =
            getAutoEnrollmentConfiguration().blockingFirst().configurations.relationshipConfigurations

        if(configs!=null){
            return Flowable.just(configs)
        }
        return Flowable.empty()

    }

    override fun createTei() {

        d2.trackedEntityModule()
            .trackedEntityTypes()
            .blockingGet().map {type->
                type.trackedEntityTypeAttributes()?.map {

                }
            }

        TODO("Not yet implemented")
    }


}