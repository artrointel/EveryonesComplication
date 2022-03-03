package com.artrointel.everyonescomplication.crypto.model
import android.util.Log
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class CryptoConnection(
    private val numberOfTopInMarket: Int,
    private val privateKey: String,
    private val onCryptoDataUpdated: OnCryptoDataReceived) {

    companion object {
        const val TAG = "CryptoConnection"
    }

    interface OnCryptoDataReceived {
        /**
         * Called in worker thread on data received.
         */
        fun onReceived(result: String)
    }

    class HTTPThread(private val onCryptoDataUpdated: OnCryptoDataReceived,
                     private val numberOfTopInMarket: Int,
                     private val privateKey: String): Thread() {
        override fun run() {
            Log.d(TAG, "HTTP Connection to CoinMarketCap is started.")
            val url = URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?" +
                    "start=1&limit=$numberOfTopInMarket&convert=USD&CMC_PRO_API_KEY=$privateKey")
            val urlConn = url.openConnection()
            val httpConn = urlConn as HttpURLConnection
            httpConn.requestMethod = "GET"
            httpConn.connectTimeout = 30000
            httpConn.readTimeout = 30000
            httpConn.doInput = true
            httpConn.doOutput = false

            try {
                val result = httpConn.inputStream.bufferedReader().use(BufferedReader::readText)
                Log.d(TAG, "Received data: $result")
                onCryptoDataUpdated.onReceived(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun open() {
        val thread = HTTPThread(onCryptoDataUpdated, numberOfTopInMarket, privateKey)
        thread.start()
    }
}