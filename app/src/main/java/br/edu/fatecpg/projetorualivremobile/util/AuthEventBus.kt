package br.edu.fatecpg.projetorualivremobile.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Eventos globais de autenticação e rede. Emitidos pelo interceptor de rede
 * e consumidos pela camada de navegação para reagir a 401 e falhas de rede.
 */
@Singleton
class AuthEventBus @Inject constructor() {

    private val _sessionExpired = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    private val _errors = MutableSharedFlow<String>(
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }

    fun notifyError(message: String) {
        _errors.tryEmit(message)
    }
}
