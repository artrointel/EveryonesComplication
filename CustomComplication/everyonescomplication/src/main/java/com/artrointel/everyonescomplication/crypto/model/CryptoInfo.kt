package com.artrointel.everyonescomplication.crypto.model

class CryptoInfo(symbol: String, price: Float, convert: String = "usd", percent_change_1h: Float, percent_change_24h: Float) {
    val symbol: String = symbol // Symbol ex. BTC
    val price: Float = price // Price ex. 44000
    val convert: String = convert // ex. USD (maybe fixed to the USD)
    val percent_change_1h : Float = percent_change_1h
    val percent_change_24h : Float = percent_change_24h

    fun ascendingIn1H() : Boolean {
        return percent_change_1h > 0
    }

    fun ascendingIn24H() :Boolean {
        return percent_change_24h > 0
    }
}