package com.example.zero_chart

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

data class pieData(
    val data: Int,
    val color: Color
)

@Composable
fun DrawPieChart(
    modifier: Modifier = Modifier,
    pieData: List<pieData>,
    radiusOuter: Float = 90f,
    chartBarWidth: Float = 90f,
    isAnimation: Boolean = true,
    animationDuration: Int = 1000
) {
    Box(modifier = modifier
    ) {
        val totalSum = pieData.sumOf { it.data }
        val dataList = mutableListOf<Float>()

        pieData.forEachIndexed { index, pieData ->
            dataList.add(index, 360f * pieData.data / totalSum)
        }

        var lastValue = 0f

        var isAnimation_ by remember { mutableStateOf(false) }

        val animateSize by animateFloatAsState(
            targetValue = if(isAnimation_) radiusOuter * 2f else 0f,
            animationSpec = tween(
                durationMillis = animationDuration,
                delayMillis = 0,
                easing = LinearOutSlowInEasing
            )
        )
        var chartSize: Float = animateSize

        val animateRotation by animateFloatAsState(
            targetValue = if(isAnimation_) 90f * 11f else 0f,
            animationSpec = tween(
                durationMillis = animationDuration,
                delayMillis = 0,
                easing = LinearOutSlowInEasing
            )
        )
        var chartRotation: Float = animateRotation

        LaunchedEffect(Unit) {
            isAnimation_ = isAnimation
        }
        if(!isAnimation) {
            chartSize = radiusOuter * 2f
            chartRotation = 0f
        }

        Spacer(modifier = Modifier
            .padding(8.dp)
            .align(Alignment.Center)
            .size(chartSize.dp)
            .rotate(chartRotation)
            .drawWithCache {
                onDrawBehind {
                    dataList.forEachIndexed { index, value ->
                        drawArc(
                            color = pieData[index].color,
                            lastValue,
                            value,
                            useCenter = false,
                            style = Stroke(chartBarWidth.toDp().toPx(), cap = StrokeCap.Butt),
                        )
                        lastValue += value
                    }
                }
            }
        )
    }
}