package org.dhis2.usescases.workflowredesign.model

data class RelationshipConfig(
    val teiPrimaryAttribute: String,
    val teiSecondaryAttribute: String,
    val teiTertiaryAttribute: String,
    val monitorProgramEnrollmentStatusId: String?,
    val sourceProgram: String,
    val relationshipDefinitionText: String,
    val canCreateRelationShip: CreateRelationshipConfig?=null
)
