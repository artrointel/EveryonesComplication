package com.artrointel.everyonescomplication.crypto.model
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class CryptoConnection(
    private val numberOfTopInMarket: Int,
    private val privateKey: String,
    private val onCryptoDataUpdated: OnCryptoDataReceived) {

    companion object {
        val TAG = CryptoConnection::class.java.simpleName
    }

    interface OnCryptoDataReceived: Runnable {
        fun onReceived(result: String)
    }

    class HTTPThread(private val onCryptoDataUpdated: OnCryptoDataReceived): Thread() {
        override fun run() {
            Log.d(TAG, "HTTP Connection start.")
            val url = URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?" +
                    "start=1&limit=1" +
                    "&convert=USD&CMC_PRO_API_KEY=a00ddd82-efa6-4d34-8707-8eb241b81feb")
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
                Handler(Looper.getMainLooper()).post(onCryptoDataUpdated)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun open() {
        val thread = HTTPThread(onCryptoDataUpdated)
        thread.start()
    }
}