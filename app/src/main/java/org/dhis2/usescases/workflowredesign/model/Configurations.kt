package org.dhis2.usescases.workflowredesign.model

data class Configurations(
    val autoEnrollments: AutoEnrollments,
    val relationshipConfigurations:List<RelationshipConfig>
)