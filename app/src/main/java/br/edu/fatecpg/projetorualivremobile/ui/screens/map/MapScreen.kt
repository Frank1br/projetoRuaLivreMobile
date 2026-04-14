package br.edu.fatecpg.projetorualivremobile.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
fun MapScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF3F4F8),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mapa interativo",
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
                currentRoute = "map",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMap = {},
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Map canvas area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8ECF0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridColor = Color(0xFFD0D8E8)
                        val gridStep = size.width / 6f

                        // Draw grid lines
                        var x = 0f
                        while (x <= size.width) {
                            drawLine(
                                color = gridColor,
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                            x += gridStep
                        }
                        var y = 0f
                        while (y <= size.height) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                            y += gridStep
                        }

                        // Alagado (large red circle) - top left area
                        val r1 = 52.dp.toPx()
                        val c1 = Offset(size.width * 0.28f, size.height * 0.32f)
                        drawCircle(color = AlertaAltoColor.copy(alpha = 0.2f), radius = r1, center = c1)
                        drawCircle(color = AlertaAltoColor, radius = 10.dp.toPx(), center = c1)
                        drawCircle(
                            color = AlertaAltoColor.copy(alpha = 0.5f),
                            radius = r1,
                            center = c1,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Parcial (medium orange circle) - right area
                        val r2 = 34.dp.toPx()
                        val c2 = Offset(size.width * 0.72f, size.height * 0.45f)
                        drawCircle(color = AlertaMedioColor.copy(alpha = 0.2f), radius = r2, center = c2)
                        drawCircle(color = AlertaMedioColor, radius = 8.dp.toPx(), center = c2)
                        drawCircle(
                            color = AlertaMedioColor.copy(alpha = 0.5f),
                            radius = r2,
                            center = c2,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Livre (small green circle) - bottom left area
                        val r3 = 20.dp.toPx()
                        val c3 = Offset(size.width * 0.38f, size.height * 0.70f)
                        drawCircle(color = AlertaBaixoColor.copy(alpha = 0.2f), radius = r3, center = c3)
                        drawCircle(color = AlertaBaixoColor, radius = 6.dp.toPx(), center = c3)
                        drawCircle(
                            color = AlertaBaixoColor.copy(alpha = 0.5f),
                            radius = r3,
                            center = c3,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Legenda",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        LegendItem(color = AlertaAltoColor, label = "Alagado")
                        LegendItem(color = AlertaMedioColor, label = "Parcial")
                        LegendItem(color = AlertaBaixoColor, label = "Livre")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 13.sp, color = Color(0xFF444455))
    }
}