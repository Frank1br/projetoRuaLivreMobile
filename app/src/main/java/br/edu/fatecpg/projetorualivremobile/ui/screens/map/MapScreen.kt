package br.edu.fatecpg.projetorualivremobile.ui.screens.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import br.edu.fatecpg.projetorualivremobile.data.model.Alagamento
import br.edu.fatecpg.projetorualivremobile.data.model.Camera
import br.edu.fatecpg.projetorualivremobile.data.model.NivelAlagamento
import br.edu.fatecpg.projetorualivremobile.data.model.StatusCamera
import br.edu.fatecpg.projetorualivremobile.ui.components.BottomBar
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaAltoColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaBaixoColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaMedioColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember(context) {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(14.0)
            controller.setCenter(GeoPoint(-23.5505, -46.6333))
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

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
                actions = {
                    IconButton(onClick = viewModel::carregarDados) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = IndigoPrimario,
                    actionIconContentColor = IndigoPrimario,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Mapa OSMDroid ─────────────────────────────────────────────────
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView }
            ) { mv ->
                mv.overlays.clear()

                // Zonas de alagamento (polígono circular semi-transparente)
                uiState.alagamentos.forEach { alagamento ->
                    val aLat = alagamento.latitude ?: return@forEach
                    val aLon = alagamento.longitude ?: return@forEach
                    val fillColor = when (alagamento.nivel) {
                        NivelAlagamento.CRITICO -> 0x88E53935.toInt()
                        NivelAlagamento.ALTO    -> 0x88F57C00.toInt()
                        NivelAlagamento.MEDIO   -> 0x88FFC107.toInt()
                        NivelAlagamento.BAIXO   -> 0x882E7D32.toInt()
                    }
                    val radiusMeters = when (alagamento.nivel) {
                        NivelAlagamento.CRITICO -> 300.0
                        NivelAlagamento.ALTO    -> 220.0
                        NivelAlagamento.MEDIO   -> 160.0
                        NivelAlagamento.BAIXO   -> 100.0
                    }
                    mv.overlays.add(
                        buildCirclePolygon(
                            GeoPoint(aLat, aLon),
                            radiusMeters,
                            fillColor
                        )
                    )
                }

                // Marcadores de alagamento
                uiState.alagamentos.forEach { alagamento ->
                    val aLat = alagamento.latitude ?: return@forEach
                    val aLon = alagamento.longitude ?: return@forEach
                    val markerColor = when (alagamento.nivel) {
                        NivelAlagamento.CRITICO -> android.graphics.Color.rgb(229, 57, 53)
                        NivelAlagamento.ALTO    -> android.graphics.Color.rgb(245, 124, 0)
                        NivelAlagamento.MEDIO   -> android.graphics.Color.rgb(255, 193, 7)
                        NivelAlagamento.BAIXO   -> android.graphics.Color.rgb(46, 125, 50)
                    }
                    Marker(mv).apply {
                        position = GeoPoint(aLat, aLon)
                        icon = buildCircleDrawable(context, markerColor, 36)
                        title = null
                        infoWindow = null
                        setOnMarkerClickListener(Marker.OnMarkerClickListener { _, _ ->
                            viewModel.selectAlagamento(alagamento)
                            true
                        })
                        mv.overlays.add(this)
                    }
                }

                // Marcadores de câmera
                uiState.cameras.forEach { camera ->
                    val camLat = camera.latitude ?: return@forEach
                    val camLon = camera.longitude ?: return@forEach
                    val camColor = when (camera.status) {
                        StatusCamera.ATIVA      -> android.graphics.Color.rgb(43, 43, 124)
                        StatusCamera.MANUTENCAO -> android.graphics.Color.rgb(245, 124, 0)
                        StatusCamera.INATIVA    -> android.graphics.Color.rgb(150, 150, 160)
                    }
                    Marker(mv).apply {
                        position = GeoPoint(camLat, camLon)
                        icon = buildCameraDrawable(context, camColor)
                        title = null
                        infoWindow = null
                        setOnMarkerClickListener(Marker.OnMarkerClickListener { _, _ ->
                            viewModel.selectCamera(camera)
                            true
                        })
                        mv.overlays.add(this)
                    }
                }

                mv.invalidate()
            }

            // ── Loading ───────────────────────────────────────────────────────
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = IndigoPrimario)
                }
            }

            // ── Legenda (canto inferior esquerdo) ─────────────────────────────
            AnimatedVisibility(
                visible = uiState.selectedCamera == null && uiState.selectedAlagamento == null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                LegendCard()
            }

            // ── Painel câmera selecionada ─────────────────────────────────────
            AnimatedVisibility(
                visible = uiState.selectedCamera != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                uiState.selectedCamera?.let { camera ->
                    CameraInfoPanel(
                        camera = camera,
                        onDismiss = viewModel::clearSelection
                    )
                }
            }

            // ── Painel alagamento selecionado ─────────────────────────────────
            AnimatedVisibility(
                visible = uiState.selectedAlagamento != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                uiState.selectedAlagamento?.let { alagamento ->
                    AlagamentoInfoPanel(
                        alagamento = alagamento,
                        onDismiss = viewModel::clearSelection
                    )
                }
            }
        }
    }
}

// ─── Painel de câmera ─────────────────────────────────────────────────────────

@Composable
private fun CameraInfoPanel(camera: Camera, onDismiss: () -> Unit) {
    val (statusColor, statusText) = when (camera.status) {
        StatusCamera.ATIVA      -> IndigoPrimario to "Ativa"
        StatusCamera.MANUTENCAO -> AlertaMedioColor to "Em manutenção"
        StatusCamera.INATIVA    -> Color(0xFF9E9EAF) to "Inativa"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = IndigoPrimario,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = camera.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A2E)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color(0xFF9E9EAF))
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Bairro: ${camera.bairro ?: "—"}",
                fontSize = 14.sp,
                color = Color(0xFF555566)
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }
        }
    }
}

// ─── Painel de alagamento ─────────────────────────────────────────────────────

@Composable
private fun AlagamentoInfoPanel(alagamento: Alagamento, onDismiss: () -> Unit) {
    val (nivelColor, nivelText) = when (alagamento.nivel) {
        NivelAlagamento.CRITICO -> Color(0xFFE53935) to "Crítico"
        NivelAlagamento.ALTO    -> AlertaAltoColor to "Alto"
        NivelAlagamento.MEDIO   -> AlertaMedioColor to "Médio"
        NivelAlagamento.BAIXO   -> AlertaBaixoColor to "Baixo"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Water,
                        contentDescription = null,
                        tint = nivelColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Nível $nivelText",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = nivelColor
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color(0xFF9E9EAF))
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = alagamento.descricao.ifBlank { "Alagamento detectado" },
                fontSize = 14.sp,
                color = Color(0xFF1A1A2E)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Nível da água: ${"%.0f".format(alagamento.nivelAgua)}%",
                fontSize = 13.sp,
                color = Color(0xFF555566)
            )

            alagamento.bairro?.takeIf { it.isNotBlank() }?.let { bairro ->
                val local = alagamento.municipio
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "$bairro — $it" } ?: bairro
                Text(
                    text = "Local: $local",
                    fontSize = 13.sp,
                    color = Color(0xFF555566)
                )
            }

            if (alagamento.dataRegistro.isNotBlank()) {
                Text(
                    text = "Registrado: ${alagamento.dataRegistro.take(16)}",
                    fontSize = 12.sp,
                    color = Color(0xFF888899)
                )
            }
        }
    }
}

// ─── Legenda ──────────────────────────────────────────────────────────────────

@Composable
private fun LegendCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LegendItem(color = IndigoPrimario, label = "Câmera ativa")
            LegendItem(color = AlertaMedioColor, label = "Manutenção")
            LegendItem(color = Color(0xFF9E9EAF), label = "Inativa")
            LegendItem(color = Color(0xFFE53935), label = "Crítico")
            LegendItem(color = AlertaAltoColor, label = "Alto")
            LegendItem(color = AlertaMedioColor, label = "Médio")
            LegendItem(color = AlertaBaixoColor, label = "Baixo")
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
        Spacer(Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF444455))
    }
}

// ─── Helpers para ícones e overlays OSMDroid ──────────────────────────────────

private fun buildCircleDrawable(context: Context, color: Int, sizeDp: Int): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val px = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    canvas.drawCircle(px / 2f, px / 2f, px / 2f - density, paint)

    val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3 * density
    }
    canvas.drawCircle(px / 2f, px / 2f, px / 2f - density - 1, border)

    return BitmapDrawable(context.resources, bitmap)
}

private fun buildCameraDrawable(context: Context, color: Int): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val px = (28 * density).toInt()
    val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    canvas.drawRoundRect(
        android.graphics.RectF(0f, 0f, px.toFloat(), px.toFloat()),
        6 * density, 6 * density, fill
    )
    val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }
    canvas.drawRoundRect(
        android.graphics.RectF(density, density, px - density, px - density),
        5 * density, 5 * density, border
    )

    return BitmapDrawable(context.resources, bitmap)
}

private fun buildCirclePolygon(center: GeoPoint, radiusMeters: Double, fillColor: Int): Polygon {
    val points = ArrayList<GeoPoint>()
    val steps = 48
    val latRad = Math.toRadians(center.latitude)
    for (i in 0..steps) {
        val angle = 2 * Math.PI * i / steps
        val dLat = radiusMeters / 111320.0 * sin(angle)
        val dLon = radiusMeters / (111320.0 * cos(latRad)) * cos(angle)
        points.add(GeoPoint(center.latitude + dLat, center.longitude + dLon))
    }
    return Polygon().apply {
        this.points = points
        this.fillColor = fillColor
        this.strokeColor = android.graphics.Color.TRANSPARENT
        this.strokeWidth = 0f
    }
}