package br.edu.fatecpg.projetorualivremobile.util

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Traduz exceções (HTTP, rede, etc.) para mensagens amigáveis em pt-BR.
 * Evita que o usuário veja "HTTP 401", stack traces ou jargão técnico.
 */
object ErrorMessages {

    fun from(t: Throwable?): String = when (t) {
        is HttpException -> fromHttp(t)
        is UnknownHostException,
        is ConnectException,
        is SocketTimeoutException,
        is IOException ->
            "Sem conexão com o servidor. Verifique sua internet e tente novamente."
        null -> GENERICO
        else -> {
            // Mensagens nossas (lançadas com texto amigável) passam direto;
            // mensagens técnicas (ex.: "HTTP 500 ...") caem no genérico.
            t.message
                ?.takeIf { it.isNotBlank() && !it.startsWith("HTTP ") }
                ?: GENERICO
        }
    }

    private fun fromHttp(e: HttpException): String {
        // A API devolve {"detail": "..."} — preferimos essa mensagem.
        val detail = runCatching {
            e.response()?.errorBody()?.string()?.let { body ->
                JsonParser.parseString(body).asJsonObject.get("detail").asString
            }
        }.getOrNull()
        if (!detail.isNullOrBlank()) return detail

        return when (e.code()) {
            400 -> "Não foi possível concluir. Confira os dados informados."
            401 -> "Sua sessão expirou. Entre novamente."
            403 -> "Você não tem permissão para esta ação."
            404 -> "Não encontramos o que você procura."
            413 -> "O arquivo é grande demais."
            422 -> "Alguns campos estão inválidos. Revise e tente de novo."
            429 -> "Muitas tentativas em pouco tempo. Aguarde um instante."
            in 500..599 -> "O servidor está com problemas. Tente novamente em instantes."
            else -> GENERICO
        }
    }

    private const val GENERICO = "Algo deu errado. Tente novamente."
}
