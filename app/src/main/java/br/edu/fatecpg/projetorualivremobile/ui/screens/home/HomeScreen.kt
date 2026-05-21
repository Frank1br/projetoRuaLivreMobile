package br.edu.fatecpg.projetorualivremobile.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.fatecpg.projetorualivremobile.data.model.Alerta
import br.edu.fatecpg.projetorualivremobile.ui.components.BottomBar
import br.edu.fatecpg.projetorualivremobile.util.DateFormatter
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaBaixoColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaMedioColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario

private val BackgroundColor = Color(0xFFF3F4F8)
private val TextDark = Color(0xFF1A1A2E)
private val TextGray = Color(0xFF9999AA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedAlerta by remember { mutableStateOf<Alerta?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            BottomBar(
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToMap = onNavigateToMap,
                onNavigateToDashboard = onNavigateToDashboard,
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Greeting ──────────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Olá, ${uiState.nomeUsuario}",
                                fontSize = 13.sp,
                                color = TextGray,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "Bem-vindo!",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificações",
                                    tint = IndigoPrimario,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // ── Alerta ativo card ──────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val temAlagamento = uiState.bairrosAtingidos > 0
                            val headerColor = if (temAlagamento) AlertaMedioColor else AlertaBaixoColor
                            val headerLabel = if (temAlagamento) "Alerta ativo" else "Tudo tranquilo"

                            // Header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = headerColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = headerLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = headerColor
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Métrica principal: cobertura da cidade
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${uiState.bairrosAtingidos}",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    lineHeight = 44.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                                    if (uiState.totalBairrosMonitorados > 0) {
                                        Text(
                                            text = "de ${uiState.totalBairrosMonitorados} bairros (${uiState.pctAtingidos}%)",
                                            fontSize = 13.sp,
                                            color = TextDark,
                                            fontWeight = FontWeight.Medium
                                        )
                                        val ocorrencias = if (uiState.totalAlagamentos > 0)
                                            " · ${uiState.totalAlagamentos} ocorrência${if (uiState.totalAlagamentos == 1) "" else "s"}"
                                        else ""
                                        Text(
                                            text = "com alagamento ativo$ocorrencias",
                                            fontSize = 12.sp,
                                            color = TextGray
                                        )
                                    } else {
                                        Text(
                                            text = "bairros com alagamento ativo",
                                            fontSize = 13.sp,
                                            color = TextGray
                                        )
                                    }
                                }
                            }

                            if (temAlagamento) {
                                Spacer(modifier = Modifier.height(12.dp))

                                // Distribuição por gravidade — contagens absolutas
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SeverityChip(
                                        count = uiState.nCriticoAlto,
                                        label = "Crítico/Alto",
                                        bgColor = Color(0xFFFFE5E5),
                                        textColor = Color(0xFFB71C1C),
                                        modifier = Modifier.weight(1f)
                                    )
                                    SeverityChip(
                                        count = uiState.nMedio,
                                        label = "Médio",
                                        bgColor = Color(0xFFE8EAF6),
                                        textColor = IndigoPrimario,
                                        modifier = Modifier.weight(1f)
                                    )
                                    SeverityChip(
                                        count = uiState.nBaixo,
                                        label = "Baixo",
                                        bgColor = Color(0xFFE8F5E9),
                                        textColor = Color(0xFF1B5E20),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Acesso rápido ──────────────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 24.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = "Acesso rápido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickAccessCard(
                                icon = Icons.Default.LocationOn,
                                label = "Locais alagados",
                                onClick = onNavigateToMap,
                                modifier = Modifier.weight(1f)
                            )
                            QuickAccessCard(
                                icon = Icons.Default.LocationCity,
                                label = "Bairros afetados",
                                onClick = onNavigateToDashboard,
                                modifier = Modifier.weight(1f)
                            )
                            QuickAccessCard(
                                icon = Icons.Default.Map,
                                label = "Mapa interativo",
                                onClick = onNavigateToMap,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ── Alertas recentes ───────────────────────────────────────
                item {
                    Text(
                        text = "Alertas recentes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 24.dp, bottom = 8.dp)
                    )
                }

                items(uiState.alertas) { alerta ->
                    AlertaItem(
                        alerta = alerta,
                        onClick = { selectedAlerta = alerta },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    selectedAlerta?.let { alerta ->
        ModalBottomSheet(
            onDismissRequest = { selectedAlerta = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Alerta de alagamento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = alerta.mensagem,
                    fontSize = 14.sp,
                    color = TextDark,
                    lineHeight = 20.sp
                )
                alerta.dataEnvio?.let { data ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Recebido ${DateFormatter.formatRelative(data)}",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
        }
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────

@Composable
private fun SeverityChip(
    count: Int,
    label: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.75f),
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = IndigoPrimario.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = IndigoPrimario,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextDark,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AlertaItem(
    alerta: Alerta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val dotColor = AlertaMedioColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Alerta de alagamento",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )

                    Text(
                        text = alerta.mensagem,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                alerta.dataEnvio?.let { data ->
                    Text(
                        text = DateFormatter.formatRelative(data),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFBBBBCC),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}