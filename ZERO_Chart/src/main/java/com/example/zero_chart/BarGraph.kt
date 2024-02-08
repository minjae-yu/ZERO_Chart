package com.example.zero_chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DrawBarGraph(
    modifier: Modifier = Modifier,
    data: List<Float>,
    barWidth: Float,
    barCornerRadius: Float = 0f,
    horizontalLines: Int,
    isAnimation: Boolean = true,
    animationDuration: Int = 1000
) {
    Box(modifier = modifier
    ) {
        val animationProgress = remember { Animatable(0f) }
        val progress = remember { mutableFloatStateOf(0f) }

        if(isAnimation) {
            LaunchedEffect(Unit) {
                animationProgress.animateTo(1f, tween(animationDuration))
            }
            progress.value = animationProgress.value
        }
        else
            progress.value = 1f

        Spacer(modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    val max = data.max()
                    val min = 0

                    val rectLineWidthPx = 1.dp.toPx()
                    drawRect(Color.White, style = Stroke(rectLineWidthPx))

                    //Horizontal
                    val sectionSize = size.height / (horizontalLines - 1)
                    repeat(horizontalLines) { i ->
                        val startY = sectionSize * i
                        drawLine(
                            Color.White,
                            start = Offset(0f, startY),
                            end = Offset(size.width, startY),
                            strokeWidth = rectLineWidthPx
                        )
                    }

                    for (r: Int in 0..data.size - 1) {
                        val xOffset = (size.width / data.size) * r + ((size.width / data.size) / 2)
                        val dataHeight = ((data[r] - min) / (max - min)) * size.height
                        drawRoundRect(
                            color = Color.Blue,
                            topLeft = Offset(xOffset - barWidth / 2, size.height),
                            size = Size(barWidth.toDp().toPx(), -dataHeight * progress.value),
                            cornerRadius = CornerRadius(barCornerRadius, barCornerRadius),
                        )
                    }
                }
            }
        )
    }
}