package br.edu.fatecpg.projetorualivremobile.ui.screens.editprofile

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.fatecpg.projetorualivremobile.data.model.AvatarPadrao
import br.edu.fatecpg.projetorualivremobile.data.repository.AuthRepository
import br.edu.fatecpg.projetorualivremobile.ui.components.RuaLivreButton
import br.edu.fatecpg.projetorualivremobile.ui.components.RuaLivreTextField
import br.edu.fatecpg.projetorualivremobile.ui.theme.IndigoPrimario
import br.edu.fatecpg.projetorualivremobile.util.ErrorMessages
import coil3.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class EditProfileUiState(
    val nome: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val avataresPadrao: List<AvatarPadrao> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        val u = authRepository.currentUsuario
        if (u != null) {
            _uiState.update {
                it.copy(nome = u.nome, email = u.email, avatarUrl = u.avatarUrl)
            }
        }
        carregarAvatares()
    }

    private fun carregarAvatares() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.getAvataresPadrao().fold(
                onSuccess = { lista -> _uiState.update { it.copy(isLoading = false, avataresPadrao = lista) } },
                onFailure = { _uiState.update { it.copy(isLoading = false) } }
            )
        }
    }

    fun onNomeChange(v: String) = _uiState.update { it.copy(nome = v.take(80), error = null) }

    fun escolherAvatarPadrao(url: String) {
        _uiState.update { it.copy(avatarUrl = url) }
    }

    fun salvar() {
        val s = _uiState.value
        if (s.nome.isBlank() || s.nome.length < 2) {
            _uiState.update { it.copy(error = "Nome inválido") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            authRepository.atualizarPerfil(nome = s.nome.trim(), avatarUrl = s.avatarUrl).fold(
                onSuccess = { _uiState.update { it.copy(isSaving = false, success = true) } },
                onFailure = { e -> _uiState.update { it.copy(isSaving = false, error = ErrorMessages.from(e)) } }
            )
        }
    }

    fun uploadAvatar(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            authRepository.uploadAvatar(baos.toByteArray()).fold(
                onSuccess = { user -> _uiState.update { it.copy(isSaving = false, avatarUrl = user.avatarUrl) } },
                onFailure = { e -> _uiState.update { it.copy(isSaving = false, error = ErrorMessages.from(e)) } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onDone: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.success) { if (state.success) onDone() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp -> bmp?.let(viewModel::uploadAvatar) }

    Scaffold(
        containerColor = Color(0xFFF3F4F8),
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = IndigoPrimario,
                    titleContentColor = Color(0xFF1A1A2E)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Avatar atual (grande)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AvatarBubble(url = state.avatarUrl, size = 96.dp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.email,
                fontSize = 13.sp,
                color = Color(0xFF666680),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Nome
            RuaLivreTextField(
                value = state.nome,
                onValueChange = viewModel::onNomeChange,
                label = "Nome"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Escolha um avatar",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.avataresPadrao.forEach { av ->
                    val selecionado = state.avatarUrl == av.url
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (selecionado) 3.dp else 1.dp,
                                color = if (selecionado) IndigoPrimario else Color(0xFFDDDDE8),
                                shape = CircleShape
                            )
                            .clickable { viewModel.escolherAvatarPadrao(av.url) }
                    ) {
                        AsyncImage(
                            model = av.url,
                            contentDescription = av.nome,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Tirar foto e usar como avatar")
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            RuaLivreButton(
                text = "Salvar alterações",
                onClick = viewModel::salvar,
                isLoading = state.isSaving
            )
        }
    }
}

@Composable
private fun AvatarBubble(url: String?, size: androidx.compose.ui.unit.Dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = IndigoPrimario.copy(alpha = 0.10f)
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "?", color = IndigoPrimario, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
