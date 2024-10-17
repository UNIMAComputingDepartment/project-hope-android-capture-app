package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model

data class ExternalEnrollmentModel(
    val orgUnit: String,
    val program: String,
    val tei: String,
    val relationshipType: String
)