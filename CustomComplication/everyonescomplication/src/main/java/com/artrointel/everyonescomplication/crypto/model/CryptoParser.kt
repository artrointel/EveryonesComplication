package com.artrointel.everyonescomplication.crypto.model

import android.util.Log
import org.json.*

class CryptoParser {
    val TAG = ""
    var size = 0
    var cryptoInfoList: ArrayList<CryptoInfo> = ArrayList()

    fun load(jsonString: String): CryptoParser {
        cryptoInfoList.clear()

        try {
            val json = JSONObject(jsonString)
            val jsonDataArray = json.getJSONArray("data")

            size = jsonDataArray.length()

            for(i: Int in 0 until size) {
                val cryptoData = jsonDataArray.getJSONObject(i)
                val symbol = cryptoData.getString("symbol")
                val priceData = cryptoData.getJSONObject("quote").getJSONObject("USD")
                val price = priceData.getDouble("price")
                val percentChange1h = priceData.getDouble("percent_change_1h")
                val percentChange24h = priceData.getDouble("percent_change_24h")
                cryptoInfoList.add(CryptoInfo(symbol, price.toFloat(), convert = "USD", percentChange1h.toFloat(), percentChange24h.toFloat()))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Cannot Parse data : $jsonString")
            Log.w(TAG, "Putting temporal empty data to crypto info")
            for(i: Int in 0 until 1) {
                cryptoInfoList.add(CryptoInfo("NAME", 0.toFloat(), convert = "USD", 0.toFloat(), 0.toFloat()))
            }
        }

        return this
    }
}