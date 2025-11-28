package com.wise.wisepay.util

data class Currency(
    val code: String,
    val flag: String,
    val symbol: String,
    val rate: Double
)

object CurrencyUtils {
    // Единый список для всех экранов
    val currenciesList = listOf(
        Currency("GBP", "🇬🇧", "£", 0.79),
        Currency("EUR", "🇪🇺", "€", 0.92),
        Currency("USD", "🇺🇸", "$", 1.0),
        Currency("KZT", "🇰🇿", "₸", 450.0)
    )

    fun getFlagForCode(code: String): String {
        return currenciesList.find { it.code == code }?.flag ?: "🇪🇺"
    }

    fun getSymbolForCode(code: String): String {
        return currenciesList.find { it.code == code }?.symbol ?: ""
    }
}