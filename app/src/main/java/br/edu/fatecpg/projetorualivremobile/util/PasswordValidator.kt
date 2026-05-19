package br.edu.fatecpg.projetorualivremobile.util

object PasswordValidator {

    const val MIN_LENGTH = 8
    const val MAX_LENGTH = 128

    private val UPPERCASE_REGEX = Regex("[A-Z]")
    private val LOWERCASE_REGEX = Regex("[a-z]")
    private val DIGIT_REGEX = Regex("[0-9]")
    private val SPECIAL_CHAR_REGEX = Regex("[!@#\$%^&*()\\-_=+\\[\\]{};:'\",.<>/?\\\\|`~]")

    private val COMMON_PASSWORDS = setOf(
        "12345678", "123456789", "1234567890", "password", "password1",
        "senha1234", "senha123", "qwerty123", "abc12345", "iloveyou",
        "admin123", "letmein1", "welcome1", "monkey123", "dragon123",
        "master123", "sunshine", "princess", "football", "passw0rd",
        "trustno1", "superman", "batman123", "chocolate", "starwars1"
    )

    data class PasswordRequirements(
        val hasMinLength: Boolean = false,
        val withinMaxLength: Boolean = true,
        val hasUppercase: Boolean = false,
        val hasLowercase: Boolean = false,
        val hasDigit: Boolean = false,
        val hasSpecialChar: Boolean = false,
        val noLeadingTrailingSpaces: Boolean = true
    ) {
        val allMet: Boolean
            get() = hasMinLength && withinMaxLength && hasUppercase &&
                    hasLowercase && hasDigit && hasSpecialChar && noLeadingTrailingSpaces
    }

    fun checkRequirements(password: String): PasswordRequirements {
        if (password.isEmpty()) return PasswordRequirements()
        return PasswordRequirements(
            hasMinLength = password.length >= MIN_LENGTH,
            withinMaxLength = password.length <= MAX_LENGTH,
            hasUppercase = UPPERCASE_REGEX.containsMatchIn(password),
            hasLowercase = LOWERCASE_REGEX.containsMatchIn(password),
            hasDigit = DIGIT_REGEX.containsMatchIn(password),
            hasSpecialChar = SPECIAL_CHAR_REGEX.containsMatchIn(password),
            noLeadingTrailingSpaces = password == password.trim()
        )
    }

    fun validate(password: String, nome: String = "", email: String = ""): Result<Unit> {
        if (password.isBlank())
            return Result.failure(Exception("A senha não pode estar vazia"))

        if (password != password.trim())
            return Result.failure(Exception("A senha não pode ter espaços no início ou no fim"))

        if (password.length < MIN_LENGTH)
            return Result.failure(Exception("A senha deve ter no mínimo $MIN_LENGTH caracteres"))

        if (password.length > MAX_LENGTH)
            return Result.failure(Exception("A senha deve ter no máximo $MAX_LENGTH caracteres"))

        val reqs = checkRequirements(password)

        if (!reqs.hasUppercase)
            return Result.failure(Exception("A senha deve ter pelo menos uma letra maiúscula (A-Z)"))

        if (!reqs.hasLowercase)
            return Result.failure(Exception("A senha deve ter pelo menos uma letra minúscula (a-z)"))

        if (!reqs.hasDigit)
            return Result.failure(Exception("A senha deve ter pelo menos um número (0-9)"))

        if (!reqs.hasSpecialChar)
            return Result.failure(Exception("A senha deve ter pelo menos um caractere especial (!@#\$...)"))

        // Boas práticas
        val passwordLower = password.lowercase()

        if (COMMON_PASSWORDS.contains(passwordLower))
            return Result.failure(Exception("Esta senha é muito comum. Escolha uma senha mais segura"))

        val emailLocal = email.substringBefore("@").lowercase().trim()
        if (emailLocal.length >= 3 && passwordLower.contains(emailLocal))
            return Result.failure(Exception("A senha não pode conter partes do seu e-mail"))

        val nomeLower = nome.lowercase().trim()
        if (nomeLower.length >= 3 && passwordLower.contains(nomeLower))
            return Result.failure(Exception("A senha não pode conter o seu nome de usuário"))

        return Result.success(Unit)
    }
}