package com.pettrack.app.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pettrack.app.domain.model.LabelCount
import com.pettrack.app.domain.model.PeriodCount

@Composable
fun KpiTile(value: String, label: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

/** Horizontal bars: label · proportional bar · count. */
@Composable
fun HorizontalBars(data: List<LabelCount>, modifier: Modifier = Modifier) {
    val max = (data.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.forEach { d ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    d.label,
                    modifier = Modifier.width(96.dp),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(d.count.toFloat() / max)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Text(d.count.toString(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/** Grouped columns per period: perdida / encontrada / en_busqueda. */
@Composable
fun GroupedColumns(data: List<PeriodCount>, modifier: Modifier = Modifier) {
    val max = (data.maxOfOrNull { maxOf(it.perdida, it.encontrada, it.enBusqueda) } ?: 1).coerceAtLeast(1)
    val cPerdida = MaterialTheme.colorScheme.error
    val cEncontrada = MaterialTheme.colorScheme.primary
    val cBusqueda = MaterialTheme.colorScheme.tertiary

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            data.forEach { p ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Bar(p.perdida, max, cPerdida)
                        Bar(p.encontrada, max, cEncontrada)
                        Bar(p.enBusqueda, max, cBusqueda)
                    }
                    Text(
                        p.period.take(7),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(cPerdida, "Perdida")
            LegendDot(cEncontrada, "Encontrada")
            LegendDot(cBusqueda, "En búsqueda")
        }
    }
}

@Composable
private fun Bar(value: Int, max: Int, color: Color) {
    val heightDp = if (value <= 0) 2.dp else (120f * value / max).dp
    Box(
        modifier = Modifier
            .width(14.dp)
            .height(heightDp)
            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
            .background(color),
    )
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
