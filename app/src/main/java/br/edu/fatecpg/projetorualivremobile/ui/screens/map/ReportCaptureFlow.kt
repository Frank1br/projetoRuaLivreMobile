package br.edu.fatecpg.projetorualivremobile.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import br.edu.fatecpg.projetorualivremobile.ui.theme.AlertaMedioColor
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCaptureFlow(
    viewModel: MapViewModel,
    onLocationError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var descricao by remember { mutableStateOf("") }
    var aguardandoLocalizacao by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap == null) return@rememberLauncherForActivityResult
        capturedBitmap = bitmap
        aguardandoLocalizacao = true
        scope.launch {
            try {
                val loc = viewModel.obterLocalizacaoAtual()
                if (loc != null) {
                    capturedLocation = loc
                } else {
                    capturedBitmap = null
                    onLocationError("Não foi possível obter sua localização. Verifique se o GPS está ligado.")
                }
            } catch (e: SecurityException) {
                capturedBitmap = null
                onLocationError("Permissão de localização negada.")
            } catch (e: Exception) {
                capturedBitmap = null
                onLocationError("Falha ao obter localização: ${e.message ?: "erro desconhecido"}")
            } finally {
                aguardandoLocalizacao = false
            }
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val cam = results[Manifest.permission.CAMERA] == true
        val loc = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (cam && loc) {
            cameraLauncher.launch(null)
        } else if (!cam) {
            onLocationError("Permissão de câmera negada.")
        } else {
            onLocationError("Permissão de localização negada.")
        }
    }

    val onFabClick: () -> Unit = {
        val camGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val locGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (camGranted && locGranted) {
            cameraLauncher.launch(null)
        } else {
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    // Fecha sheet e reseta estado quando o envio termina com sucesso.
    LaunchedEffect(uiState.reportSuccess) {
        if (uiState.reportSuccess) {
            capturedBitmap = null
            capturedLocation = null
            descricao = ""
            viewModel.clearReportFeedback()
        }
    }

    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp),
            containerColor = AlertaMedioColor,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Reportar alagamento"
            )
        }
    }

    if (aguardandoLocalizacao) {
        // Não bloqueia a UI principal; só um indicador discreto.
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IndigoPrimario)
            }
        }
    }

    val bitmap = capturedBitmap
    val loc = capturedLocation
    if (bitmap != null && loc != null) {
        ModalBottomSheet(
            onDismissRequest = {
                capturedBitmap = null
                capturedLocation = null
                descricao = ""
                viewModel.clearReportFeedback()
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Reportar alagamento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto capturada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Localização: %.5f, %.5f".format(loc.first, loc.second),
                    fontSize = 12.sp,
                    color = Color(0xFF555566)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it.take(300) },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !uiState.isSubmittingReport
                )

                uiState.reportError?.let { erro ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = erro,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val bytes = bitmapToJpegBytes(bitmap, quality = 80)
                        viewModel.submitReport(
                            latitude = loc.first,
                            longitude = loc.second,
                            descricao = descricao.trim().ifBlank { null },
                            fotoJpeg = bytes
                        )
                    },
                    enabled = !uiState.isSubmittingReport,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimario)
                ) {
                    if (uiState.isSubmittingReport) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Enviar reporte")
                    }
                }
            }
        }
    }
}

private fun bitmapToJpegBytes(bitmap: Bitmap, quality: Int = 80): ByteArray {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
    return baos.toByteArray()
}
