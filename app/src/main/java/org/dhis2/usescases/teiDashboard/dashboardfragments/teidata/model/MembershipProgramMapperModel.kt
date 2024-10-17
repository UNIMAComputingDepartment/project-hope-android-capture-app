package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model

import org.dhis2.usescases.workflowredesign.model.CreateRelationshipConfig

data class MembershipProgramMapperModel(val  program:String,
                                         val members:List<MembershipModel>,
                                         val relationshipDescription:String,
                                         val isTrackingEnrollmentDefined :Boolean,
                                         val trackingProgramName :String,
                                         val canCreateRelationshipConfig : CreateRelationshipConfig?=null)