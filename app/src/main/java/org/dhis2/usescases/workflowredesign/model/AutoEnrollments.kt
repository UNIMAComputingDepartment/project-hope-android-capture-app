package org.dhis2.usescases.workflowredesign.model

import org.dhis2.usescases.workflowredesign.model.TargetProgsItem

data class AutoEnrollments(
    val disableManualEnrollement: List<String>,
    val sourceProgramEntity: String,
    val targetPrograms:ArrayList<TargetProgsItem>
)