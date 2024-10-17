package org.dhis2.usescases.workflowredesign.model

data class AutoEnrollments(
    val disableManualEnrollement: List<String>,
    val sourceProgramEntity: String,
    val targetPrograms:ArrayList<TargetProgsItem>
)