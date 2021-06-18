package dhis2.org.analytics.charts.data

import org.hisp.dhis.android.core.period.PeriodType
import java.util.Date

data class Graph(
    val title: String,
    val isOnline: Boolean,
    val series: List<SerieData>,
    val periodToDisplay: String,
    val eventPeriodType: PeriodType,
    val periodStep: Long,
    val chartType: ChartType? = ChartType.LINE_CHART,
    val categories: List<String> = emptyList()
) {
    fun numberOfStepsToDate(date: Date): Float {
        return if (baseSeries().isEmpty() || baseSeries().first().coordinates.isEmpty()) {
            0f
        } else {
            val initialDate = baseSeries().first().coordinates.first().eventDate.time
            val dateDiff = date.time - initialDate
            val stepsFromInitialDate = (dateDiff / periodStep).toFloat()
            stepsFromInitialDate
        }
    }

    fun numberOfStepsToLastDate(): Float {
        return if (baseSeries().isEmpty() || baseSeries().first().coordinates.isEmpty()) {
            return 0f
        } else {
            numberOfStepsToDate(baseSeries().first().coordinates.last().eventDate)
        }
    }

    fun dateFromSteps(numberOfSteps: Long): Date? {
        return if (baseSeries().isEmpty() || baseSeries().first().coordinates.isEmpty()) {
            return null
        } else {
            Date(
                baseSeries().first().coordinates.first().eventDate.time +
                        numberOfSteps * periodStep
            )
        }
    }

    fun maxValue(): Float {
        return series.map { it.coordinates.map { points -> points.fieldValue }.max() ?: 0f }.max()
            ?: 0f
    }

    fun minValue(): Float {
        return series.map { it.coordinates.map { points -> points.fieldValue }.min() ?: 0f }.min()
            ?: 0f
    }

    private fun baseSeries(): List<SerieData> = if (chartType == ChartType.NUTRITION) {
        listOf(series.last())
    } else {
        series
    }
}

data class SerieData(
    val fieldName: String,
    val coordinates: List<GraphPoint>
)

data class GraphPoint(
    val eventDate: Date,
    val position: Float? = -1f,
    val fieldValue: Float
)

fun Graph.toChartBuilder(): Chart.ChartBuilder {
    return Chart.ChartBuilder()
}
