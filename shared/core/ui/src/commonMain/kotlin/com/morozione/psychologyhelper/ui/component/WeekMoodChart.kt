package com.morozione.psychologyhelper.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.ui.theme.MoodBad
import com.morozione.psychologyhelper.ui.theme.MoodGood
import com.morozione.psychologyhelper.ui.theme.MoodGreat
import com.morozione.psychologyhelper.ui.theme.MoodOkay
import com.morozione.psychologyhelper.ui.theme.MoodTerrible
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun moodValue(mood: Mood): Float = when (mood) {
    Mood.GREAT -> 5f
    Mood.GOOD -> 4f
    Mood.OKAY -> 3f
    Mood.BAD -> 2f
    Mood.TERRIBLE -> 1f
}

private fun moodColor(value: Float): Color = when {
    value >= 4.5f -> MoodGreat
    value >= 3.5f -> MoodGood
    value >= 2.5f -> MoodOkay
    value >= 1.5f -> MoodBad
    else -> MoodTerrible
}

private val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun WeekMoodChart(entries: List<MoodEntry>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val emptyBarColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val labelTextStyle = MaterialTheme.typography.labelSmall.copy(color = labelColor)

    val tz = TimeZone.currentSystemDefault()
    val valuesByDay = Array(7) { mutableListOf<Float>() }
    entries.forEach { entry ->
        val dayOfWeek = Instant.fromEpochMilliseconds(entry.timestamp)
            .toLocalDateTime(tz).dayOfWeek
        val index = when (dayOfWeek) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
            else -> 0
        }
        valuesByDay[index].add(moodValue(entry.mood))
    }
    val avgByDay = valuesByDay.map { vals -> if (vals.isEmpty()) 0f else vals.average().toFloat() }

    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val labelHeight = 20.dp.toPx()
        val maxBarHeight = size.height - labelHeight
        val slotWidth = size.width / 7f
        val barWidth = slotWidth * 0.6f
        val minBarHeight = 6.dp.toPx()

        avgByDay.forEachIndexed { i, avg ->
            val x = i * slotWidth + (slotWidth - barWidth) / 2f
            if (avg == 0f) {
                drawRoundRect(
                    color = emptyBarColor,
                    topLeft = Offset(x, maxBarHeight - minBarHeight),
                    size = Size(barWidth, minBarHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            } else {
                val barH = (avg / 5f) * maxBarHeight
                val clampedH = barH.coerceAtLeast(minBarHeight)
                drawRoundRect(
                    color = moodColor(avg),
                    topLeft = Offset(x, maxBarHeight - clampedH),
                    size = Size(barWidth, clampedH),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }

            val labelResult = textMeasurer.measure(
                text = dayLabels[i],
                style = labelTextStyle
            )
            drawText(
                textLayoutResult = labelResult,
                topLeft = Offset(
                    x + (barWidth - labelResult.size.width) / 2f,
                    maxBarHeight + 4.dp.toPx()
                )
            )
        }
    }
}
