package com.example.expensetracker.utils

object InputValidator {
    fun isAmountValid(amount: String): Boolean {
        return try {
            amount.toDouble() > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isTitleValid(title: String): Boolean {
        // Защита от XSS - проверяем на наличие HTML/JS тегов
        val xssPattern = "<.*?>|&.*?;|javascript:".toRegex()
        return title.isNotBlank() && !title.contains(xssPattern)
    }

    fun sanitizeInput(input: String): String {
        // Удаляем потенциально опасные символы
        return input.replace("[<>\"'&;]".toRegex(), "")
    }
}