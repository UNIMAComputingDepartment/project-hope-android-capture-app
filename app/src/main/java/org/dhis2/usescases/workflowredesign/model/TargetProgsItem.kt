package org.dhis2.usescases.workflowredesign.model

import org.dhis2.usescases.workflowredesign.model.ConstraintsDataElement

data class TargetProgsItem(
    val constraintsDataElements: List<ConstraintsDataElement>,
    val ids: List<String>
)