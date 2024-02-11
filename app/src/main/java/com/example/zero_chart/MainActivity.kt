package com.example.zero_chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.zero_chart.ui.theme.ZERO_ChartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZERO_ChartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        DrawLineChart(
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f),
                            lineData = listOf(
                                lineData(listOf(100f, 40f, 20f, 80f, 60f), Color.Red, true),
                                lineData(listOf(60f, 80f, 20f, 40f, 100f), Color.Blue, true),
                                lineData(listOf(20f, 40f, 60f, 80f, 100f), Color.Green, true)
                            ),
                            verticalLines = 5,
                            horizontalLines = 4,
                            lineWidth = 3,
                            isSmooth = true,
                            isAnimation = true,
                            animationDuration = 3000
                        )

                        DrawBarGraph(
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f),
                            data = listOf(100f, 200f, 50f, 80f),
                            barWidth = 100f,
                            barCornerRadius = 20f,
                            horizontalLines = 4,
                            isAnimation = true,
                            animationDuration = 3000
                        )

                        DrawPieChart(
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f),
                            pieData = listOf(
                                pieData(150, Color.Blue),
                                pieData(120, Color.Cyan),
                                pieData(110, Color.Red),
                                pieData(170, Color.Green),
                                pieData(120, Color.Yellow)
                            ),
                            radiusOuter = 90f,
                            chartBarWidth = 90f,
                            isAnimation = true,
                            animationDuration = 1000
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLineChart() {
    DrawLineChart(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
            .fillMaxHeight(1 / 2f),
        lineData = listOf(
            lineData(listOf(100f, 40f, 60f, 20f, 80f), Color.Blue, true),
            lineData(listOf(80f, 20f, 60f, 40f, 100f), Color.Red, true)
        ),
        verticalLines = 4,
        horizontalLines = 4,
        lineWidth = 3,
        isSmooth = true,
        isAnimation = true,
        animationDuration = 3000
    )
}