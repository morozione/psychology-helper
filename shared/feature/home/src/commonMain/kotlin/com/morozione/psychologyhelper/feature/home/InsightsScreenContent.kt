package com.morozione.psychologyhelper.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import com.morozione.psychologyhelper.ui.component.InsightCard
import com.morozione.psychologyhelper.ui.component.SectionTitle
import com.morozione.psychologyhelper.ui.component.StatCard
import com.morozione.psychologyhelper.ui.component.WeekMoodChart
import com.morozione.psychologyhelper.ui.theme.Dimens

@Composable
fun InsightsScreenContent(userId: String) {
    val screenModel = koinScreenModel<InsightsScreenModel>()
    val state by screenModel.state.collectAsState()

    LaunchedEffect(userId) {
        screenModel.onIntent(InsightsIntent.Load(userId))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.spaceLg)
    ) {
        item { Spacer(Modifier.height(Dimens.spaceXxl)) }

        item { SectionTitle("This Week") }

        item {
            WeekMoodChart(
                entries = state.weekEntries,
                modifier = Modifier.padding(vertical = Dimens.spaceLg)
            )
        }

        item { Spacer(Modifier.height(Dimens.spaceLg)) }

        item { SectionTitle("AI Insight") }

        item {
            InsightCard(
                insight = state.weeklyInsight,
                isLoading = state.isLoadingInsight
            )
        }

        item { Spacer(Modifier.height(Dimens.spaceLg)) }

        item { SectionTitle("Mood Stats") }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                StatCard(
                    label = "Total Entries",
                    value = state.totalEntries.toString(),
                    emoji = "📝",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Best Mood",
                    value = state.bestMoodLabel,
                    emoji = "😄",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "This Week",
                    value = "${state.weekEntries.size} days",
                    emoji = "📅",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { Spacer(Modifier.height(Dimens.spaceLg)) }
    }
}
