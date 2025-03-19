package com.macieandrz.securitycamera.ui.element

import android.annotation.SuppressLint
import android.content.Context
import com.macieandrz.securitycamera.data.models.CrimeStatItem
import io.data2viz.charts.chart.chart
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.bar
import io.data2viz.charts.chart.quantitative
import io.data2viz.geom.Size
import io.data2viz.viz.VizContainerView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Locale

@Composable
fun CrimeStatChart(crimeStats: List<CrimeStatItem>) {
    AndroidView(
        factory = { context ->
            CrimeStatChartView(context).apply {
                updateChart(crimeStats)
            }
        },
        update = { view ->
            view.updateChart(crimeStats)
        }
    )
}

class CrimeStatChartView(context: Context) : VizContainerView(context) {

   private var mSize = Size(500.0, 500.0)
    private var w: Int = 0
    private var h: Int = 0

    init {
       // Get screen size
        val displayMetrics = context.resources.displayMetrics
         w = displayMetrics.widthPixels
         h = displayMetrics.heightPixels

    }

    @SuppressLint("SuspiciousIndentation")
    fun updateChart(crimeStats: List<CrimeStatItem>) {

        val points = prepareChartPoints(crimeStats)

        chart(points) {
            size = if (w > h) {
                Size(w.toDouble() / 2, h.toDouble() / 2.1)
            } else {
                Size(w.toDouble() / 1.2, h.toDouble() / 2.4)
            }
            title = "Categories of crimes"

            val category = discrete({ domain.category }) {
                name = "Categories of crimes"
                formatter = { formatCategoryName(this) }
            }

            val count = quantitative({ domain.count }) {
                name = "Number of crimes"
            }

            bar(category, count)
        }

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Scaling the chart based on screen size
        val size = if (w > h) {
            Size(w.toDouble() / 2, h.toDouble() / 2.1)
        } else {
            Size(w.toDouble() / 1.2, h.toDouble() / 2.4)
        }
        mSize = size
    }

    private fun prepareChartPoints(crimeStats: List<CrimeStatItem>): List<CategoryToCount> {
        // Grouping and counting crimes by category
        val categoryCounts = mutableMapOf<String, Int>()

        crimeStats.forEach { crime ->
            val category = crime.category
            categoryCounts[category] = (categoryCounts[category] ?: 0) + 1
        }

        // Converting to a list of CategoryToCount objects
        return categoryCounts.map { (category, count) ->
            CategoryToCount(category, count.toDouble())
        }
    }

    private fun formatCategoryName(category: String): String {
        // Converting names with hyphens to Title Case format
        return category.split("-")
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
    }

    data class CategoryToCount(val category: String, val count: Double)
}
