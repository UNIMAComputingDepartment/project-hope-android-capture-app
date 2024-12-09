package org.dhis2.usescases.teiDashboard.dashboardfragments.relationships

import org.dhis2.commons.data.RelationshipViewModel
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope

data class RegisterConfig(
    val relationShipType: String,
    val programUid: String,
    val stageUid: String
)

data class ProgramEventModel(
    val programUid: String,
    val eventUid: String,
)

class RegisterManagerImpl : RegisterManager {
    var registerConfig: RegisterConfig = RegisterConfig(
        relationShipType = "Qr4QXrT0JDo",
        programUid = "mLQQMqFKrmv",
        stageUid = "IY6T73tuSVM"
    )
    private var position = 0
    private var events = listOf<ProgramEventModel>()

    override fun setMembers(d2: D2, relationshipViewModels: List<RelationshipViewModel>) {
         events = relationshipViewModels.filter {
            it.relationshipType.uid() == registerConfig.relationShipType
        }.map {
            val event = d2.eventModule().events().byProgramStageUid()
                .eq(registerConfig.stageUid)
                .byTrackedEntityInstanceUids(listOf(it.ownerUid))
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet().first()
            ProgramEventModel(
                programUid = registerConfig.programUid,
                eventUid = event.uid()
            )
        }
    }

    override fun getNextEvent(): ProgramEventModel {
        if (hasNext()) {
            return events[position++]
        }
        throw IndexOutOfBoundsException()
    }

    override fun hasNext() = position < events.size

    override fun hasRegister(): Boolean {
        return true
    }

}

interface RegisterManager {
    fun setMembers(d2: D2, relationshipViewModels: List<RelationshipViewModel>)
    fun getNextEvent(): ProgramEventModel
    fun hasNext(): Boolean
    fun hasRegister(): Boolean
}