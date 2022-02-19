package com.artrointel.everyonescomplication.crypto.model

class CryptoInfo(val symbol: String, // Price ex. 44000
                 val price: Float,
                 val convert: String = "usd",
                 val percent_change_1h: Float,
                 val percent_change_24h: Float) {
}