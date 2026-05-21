package br.edu.fatecpg.projetorualivremobile.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Centraliza a formatação das datas vindas da API (que chegam como string
 * ISO ou no padrão "yyyy-MM-dd HH:mm[:ss]"). Mantém tudo em pt-BR e em
 * tom amigável: "Hoje, 14:30" no lugar de "2026-05-19T14:30:00".
 *
 * Notas:
 *  - Parser é tolerante a múltiplos formatos que a API emite.
 *  - Sem timezone math — o backend grava timestamps "naive" e o app
 *    apresenta como tal. Se um dia migrarmos pra UTC com sufixo Z,
 *    a lógica precisará subtrair o offset do dispositivo.
 */
object DateFormatter {

    private val ptBR = Locale("pt", "BR")

    private val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSSSSS",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd"
    )

    fun parse(raw: String?): Date? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.removeSuffix("Z").trim()
        for (p in patterns) {
            try {
                return SimpleDateFormat(p, ptBR).parse(cleaned)
            } catch (_: Exception) {
                // tenta o próximo
            }
        }
        return null
    }

    /** "Hoje, 14:30" · "Ontem, 14:30" · "19/05, 14:30" · "19/05/2026, 14:30" */
    fun formatRelative(raw: String?): String {
        val date = parse(raw) ?: return raw?.take(16) ?: "—"
        val event = Calendar.getInstance().apply { time = date }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val sameYear = today.get(Calendar.YEAR) == event.get(Calendar.YEAR)

        val time = SimpleDateFormat("HH:mm", ptBR).format(date)
        return when {
            isSameDay(event, today) -> "Hoje, $time"
            isSameDay(event, yesterday) -> "Ontem, $time"
            sameYear -> "${SimpleDateFormat("dd/MM", ptBR).format(date)}, $time"
            else -> "${SimpleDateFormat("dd/MM/yyyy", ptBR).format(date)}, $time"
        }
    }

    /** "em 3h 24min" · "em 23min" · "em menos de 1min" · "expirado" */
    fun formatTimeUntil(raw: String?): String {
        val date = parse(raw) ?: return raw?.take(16) ?: "—"
        val diffMs = date.time - System.currentTimeMillis()
        if (diffMs <= 0) return "expirado"
        val totalMinutes = diffMs / 60_000
        if (totalMinutes < 1) return "em menos de 1min"
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours >= 24 -> {
                val days = hours / 24
                val remH = hours % 24
                if (remH == 0L) "em ${days}d" else "em ${days}d ${remH}h"
            }
            hours > 0 -> if (minutes == 0L) "em ${hours}h" else "em ${hours}h ${minutes}min"
            else -> "em ${minutes}min"
        }
    }

    /** Para eixo de gráfico ou label compacta: "19/05" */
    fun formatShortDate(raw: String?): String {
        val date = parse(raw) ?: return raw?.take(5) ?: ""
        return SimpleDateFormat("dd/MM", ptBR).format(date)
    }

    private fun isSameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
