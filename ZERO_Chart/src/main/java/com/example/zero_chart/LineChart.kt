package com.example.zero_chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.drawscope.clipRect

data class lineData(
    val data: List<Float>,
    val lineColor: Color,
    val isFill: Boolean
)

data class pathSet(
    var linePath: Path,
    var filledPath: Path
)

fun GeneratePath(data: List<Float>, size: Size, dataSize: Int, max: Float, min: Float): Path {
    val path = Path()

    val dataHeight = size.height - ((data[0] - min) / (max - min)) * size.height
    path.moveTo(0f, dataHeight)
    for(i: Int in 1..data.size - 1)
        path.lineTo(size.width / (dataSize - 1) * i.toFloat(), size.height - ((data[i] - min) / (max - min)) * size.height)
    return path
}

fun GenerateSmoothPath(data: List<Float>, size: Size, dataSize: Int, max: Float, min: Float): Path {
    val path = Path()

    val max = data.max()
    val min = data.min()

    var formerX: Float = 0f
    var formerY: Float = size.height - ((data[0] - min) / (max - min)) * size.height

    path.moveTo(formerX, formerY)
    for(i: Int in 1..data.size - 1) {
        val controlPoint1x = (formerX + (size.width / (dataSize - 1) * i.toFloat())) / 2f
        val controlPoint1y = formerY

        val controlPoint2x = (formerX + (size.width / (dataSize - 1) * i.toFloat())) / 2f
        val controlPoint2y = size.height - ((data[i] - min) / (max - min)) * size.height

        path.cubicTo(controlPoint1x, controlPoint1y, controlPoint2x, controlPoint2y, size.width / (dataSize - 1) * i.toFloat(), size.height - ((data[i] - min) / (max - min)) * size.height)

        formerX = size.width / (dataSize - 1) * i.toFloat()
        formerY = size.height - ((data[i] - min) / (max - min)) * size.height
    }
    return path
}

@Composable
fun DrawLineChart(
    modifier: Modifier = Modifier,
    lineData: List<lineData> = emptyList(),
    verticalLines: Int,
    horizontalLines: Int,
    lineWidth: Int = 2,
    isSmooth: Boolean = false,
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
                //Generate Paths
                val pathList = MutableList<pathSet>(lineData.size) { pathSet(Path(), Path()) }
                val combinedList = emptyList<Float>().toMutableList()

                //Calculate Max and Min
                for (p: Int in 0..lineData.size - 1) {
                    combinedList += lineData[p].data
                }
                val max = mutableFloatStateOf(0f)
                max.value = combinedList.max()
                val min = mutableFloatStateOf(0f)
                min.value = combinedList.min()

                for (p: Int in 0..lineData.size - 1) {
                    var path = GeneratePath(lineData[p].data, size, lineData[p].data.size, max.value, min.value)
                    if (isSmooth)
                        path = GenerateSmoothPath(lineData[p].data, size, lineData[p].data.size, max.value, min.value)
                    val filledPath = Path()
                    filledPath.addPath(path)
                    filledPath.lineTo(size.width, size.height)
                    filledPath.lineTo(0f, size.height)
                    filledPath.close()

                    pathList[p].linePath = path;
                    pathList[p].filledPath = filledPath
                }

                onDrawBehind {
                    val barWidthPx = 1.dp.toPx()
                    drawRect(Color.White, style = Stroke(barWidthPx))

                    //Vertical
                    val verticalSize = size.width / (verticalLines - 1)
                    repeat(verticalLines) { i ->
                        val startX = verticalSize * (i + 1)
                        drawLine(
                            Color.White,
                            start = Offset(startX, 0f),
                            end = Offset(startX, size.height),
                            strokeWidth = barWidthPx
                        )
                    }

                    //Horizontal
                    val sectionSize = size.height / (horizontalLines - 1)
                    repeat(horizontalLines) { i ->
                        val startY = sectionSize * i
                        drawLine(
                            Color.White,
                            start = Offset(0f, startY),
                            end = Offset(size.width, startY),
                            strokeWidth = barWidthPx
                        )
                    }

                    val brushList = MutableList<Brush>(lineData.size) { Brush.verticalGradient() }
                    for(b: Int in 0..lineData.size - 1) {
                        brushList[b] = Brush.verticalGradient(
                            listOf(
                                lineData[b].lineColor.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    }

                    for (p: Int in 0..lineData.size - 1) {
                        clipRect(right = size.width * progress.value) {
                            drawPath(
                                pathList[p].linePath,
                                lineData[p].lineColor,
                                style = Stroke(lineWidth.dp.toPx())
                            )
                            if(lineData[p].isFill) {
                                drawPath(
                                    pathList[p].filledPath,
                                    brush = brushList[p],
                                    style = Fill,
                                )
//                                drawPath(
//                                    pathList[p].filledPath,
//                                    color = lineData[p].lineColor
//                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

/* 라인 클릭시 정보 표시해주는 예시 코드 */
//@Composable
//fun DrawLineChart(
//    // ... (your existing parameters)
//) {
//    var selectedLineIndex by remember { mutableStateOf(-1) }
//
//    // ... (existing code)
//
//    Spacer(
//        modifier = Modifier
//            .padding(8.dp)
//            .aspectRatio(3 / 2f)
//            .fillMaxSize()
//            .clickable {
//                // Calculate the touched position and find the corresponding line index
//                val touchedX = it.position.x
//                val lineIndex = (touchedX / size.width * verticalLines).toInt()
//                selectedLineIndex = lineIndex
//            }
//            .drawWithCache {
//                // ... (existing code)
//
//                onDrawBehind {
//                    // ... (existing code)
//
//                    for (p: Int in 0 until lineData.size) {
//                        val isSelectedLine = p == selectedLineIndex
//
//                        // Draw the line with different color if it's the selected line
//                        drawPath(
//                            pathList[p].linePath,
//                            if (isSelectedLine) Color.Red else lineData[p].lineColor,
//                            style = Stroke(lineWidth.dp.toPx())
//                        )
//
//                        if (lineData[p].isFill) {
//                            // Draw the filled path with different color if it's the selected line
//                            drawPath(
//                                pathList[p].filledPath,
//                                brush = if (isSelectedLine) Brush.verticalGradient(
//                                    listOf(Color.Red.copy(alpha = 0.4f), Color.Transparent)
//                                ) else brushList[p],
//                                style = Fill,
//                            )
//                        }
//
//                        // Display the value when the line is selected
//                        if (isSelectedLine) {
//                            val value = lineData[p].data[selectedLineIndex]
//                            drawText(
//                                text = "Value: $value",
//                                color = Color.White,
//                                fontSize = 16.sp,
//                                offset = Offset(touchedX, size.height / 2),
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//                }
//            }
//    )
//}