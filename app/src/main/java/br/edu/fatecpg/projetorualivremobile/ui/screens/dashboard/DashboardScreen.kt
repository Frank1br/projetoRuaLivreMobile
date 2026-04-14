package br.edu.fatecpg.projetorualivremobile.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.fatecpg.projetorualivremobile.ui.components.BottomBar
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaAltoColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaBaixoColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaMedioColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF3F4F8),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = IndigoPrimario,
                    titleContentColor = Color(0xFF1A1A2E)
                )
            )
        },
        bottomBar = {
            BottomBar(
                currentRoute = "dashboard",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMap = onNavigateToMap,
                onNavigateToDashboard = {},
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = IndigoPrimario) }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            value = "${uiState.totalBairrosAlagados}",
                            label = "Bairros alagados",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = "${uiState.totalRuasAfetadas}",
                            label = "Ruas afetadas",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Distribuição donut chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Distribuição",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DonutChart(
                                    segments = listOf(
                                        DonutSegment(0.10f, AlertaAltoColor),
                                        DonutSegment(0.40f, IndigoPrimario),
                                        DonutSegment(0.50f, AlertaBaixoColor)
                                    ),
                                    modifier = Modifier.size(130.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    LegendRow(color = AlertaAltoColor, label = "10% Alagados")
                                    LegendRow(color = IndigoPrimario, label = "40% Poucos afetados")
                                    LegendRow(color = AlertaBaixoColor, label = "50% Não afetados")
                                }
                            }
                        }
                    }
                }

                // Bar chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Média dos locais de alagamento",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val months = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun")
                            BarChart(
                                months = months,
                                data2025 = listOf(5f, 8f, 12f, 7f, 10f, 15f),
                                data2026 = listOf(3f, 6f, 9f, 11f, 8f, 13f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )

                            // Month labels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                months.forEach { month ->
                                    Text(
                                        text = month,
                                        fontSize = 11.sp,
                                        color = Color(0xFF9999AA),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                LegendRow(color = IndigoPrimario, label = "2025")
                                LegendRow(color = AlertaMedioColor, label = "2026")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = IndigoPrimario
            )
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF666680),
                lineHeight = 18.sp
            )
        }
    }
}

data class DonutSegment(val fraction: Float, val color: Color)

@Composable
private fun DonutChart(segments: List<DonutSegment>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 28.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2f
        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
        val arcSize = Size(radius * 2f, radius * 2f)
        var startAngle = -90f

        segments.forEach { segment ->
            val sweep = segment.fraction * 360f
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweep - 2f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun BarChart(
    months: List<String>,
    data2025: List<Float>,
    data2026: List<Float>,
    modifier: Modifier = Modifier
) {
    val maxVal = (data2025 + data2026).maxOrNull() ?: 1f

    Canvas(modifier = modifier) {
        val barWidth = size.width / (months.size * 3f)
        val gap = barWidth * 0.4f

        months.forEachIndexed { i, _ ->
            val groupStart = i * (barWidth * 3f)

            // 2025 bar
            val h1 = (data2025[i] / maxVal) * (size.height - 20.dp.toPx())
            drawRect(
                color = IndigoPrimario,
                topLeft = Offset(groupStart + gap, size.height - h1),
                size = Size(barWidth, h1)
            )
            // 2026 bar
            val h2 = (data2026[i] / maxVal) * (size.height - 20.dp.toPx())
            drawRect(
                color = AlertaMedioColor,
                topLeft = Offset(groupStart + barWidth + gap * 1.5f, size.height - h2),
                size = Size(barWidth, h2)
            )
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = CircleShape,
            color = color
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 13.sp, color = Color(0xFF444455))
    }
}