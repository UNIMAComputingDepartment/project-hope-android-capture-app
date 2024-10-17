package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata

import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model.DreamsTeiModel

interface OnDreamsTeiSelectionListener {
    fun onSelected(tei:DreamsTeiModel)

    fun onDeselected(tei: DreamsTeiModel)
}