package br.edu.fatecpg.projetorualivremobile.navigation

import androidx.lifecycle.ViewModel
import br.edu.fatecpg.projetorualivremobile.util.AuthEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class AppEventsViewModel @Inject constructor(
    bus: AuthEventBus
) : ViewModel() {
    val sessionExpired: SharedFlow<Unit> = bus.sessionExpired
    val errors: SharedFlow<String> = bus.errors
}
