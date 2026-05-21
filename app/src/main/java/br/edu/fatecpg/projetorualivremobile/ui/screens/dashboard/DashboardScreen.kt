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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.ui.components.BottomBar
import br.edu.fatecpg.projetorualivremobile.util.DateFormatter
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaAltoColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaBaixoColor
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
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::carregarDados,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                            value = "${uiState.alagamentosAtivos}",
                            label = "Alagamentos ativos",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = "${uiState.camerasAtivas}",
                            label = "Câmeras ativas",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = "${uiState.alertasHoje}",
                            label = "Alertas hoje",
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
                                text = "Distribuição por nível",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val criticoAlto = (uiState.porNivel[NivelAlagamento.CRITICO] ?: 0) +
                                (uiState.porNivel[NivelAlagamento.ALTO] ?: 0)
                            val medio = uiState.porNivel[NivelAlagamento.MEDIO] ?: 0
                            val baixo = uiState.porNivel[NivelAlagamento.BAIXO] ?: 0
                            val total = criticoAlto + medio + baixo

                            if (total == 0) {
                                Text(
                                    text = "Sem alagamentos registrados.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9999AA)
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    DonutChart(
                                        segments = listOf(
                                            DonutSegment(criticoAlto / total.toFloat(), AlertaAltoColor),
                                            DonutSegment(medio / total.toFloat(), IndigoPrimario),
                                            DonutSegment(baixo / total.toFloat(), AlertaBaixoColor)
                                        ),
                                        modifier = Modifier.size(130.dp)
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(start = 16.dp)
                                    ) {
                                        LegendRow(color = AlertaAltoColor, label = "$criticoAlto Alto/Crítico")
                                        LegendRow(color = IndigoPrimario, label = "$medio Médio")
                                        LegendRow(color = AlertaBaixoColor, label = "$baixo Baixo")
                                    }
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
                                text = "Ocorrências por dia",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val historico = uiState.historico.takeLast(12)
                            if (historico.isEmpty()) {
                                Text(
                                    text = "Sem histórico no período.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9999AA)
                                )
                            } else {
                                val labels = historico.map { DateFormatter.formatShortDate(it.data) }
                                BarChart(
                                    values = historico.map { it.totalOcorrencias.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    labels.forEach { label ->
                                        Text(
                                            text = label,
                                            fontSize = 9.sp,
                                            color = Color(0xFF9999AA),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                    LegendRow(color = IndigoPrimario, label = "Ocorrências de alagamento")
                                }
                            }
                        }
                    }
                }

                // Alagamentos por região
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Alagamentos por região",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A2E)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.porRegiao.isEmpty()) {
                                Text(
                                    text = "Sem alagamentos ativos por região.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9999AA)
                                )
                            } else {
                                uiState.porRegiao.forEach { regiao ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = regiao.regiao,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1A1A2E)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = IndigoPrimario.copy(alpha = 0.10f)
                                        ) {
                                            Text(
                                                text = "${regiao.total}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = IndigoPrimario,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
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
    values: List<Float>,
    modifier: Modifier = Modifier
) {
    val maxVal = values.maxOrNull()?.takeIf { it > 0f } ?: 1f

    Canvas(modifier = modifier) {
        if (values.isEmpty()) return@Canvas
        val slot = size.width / values.size
        val barWidth = slot * 0.6f
        val gap = (slot - barWidth) / 2f

        values.forEachIndexed { i, v ->
            val h = (v / maxVal) * (size.height - 20.dp.toPx())
            drawRect(
                color = IndigoPrimario,
                topLeft = Offset(i * slot + gap, size.height - h),
                size = Size(barWidth, h)
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