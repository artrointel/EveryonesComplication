package com.artrointel.everyonescomplication.crypto.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class CryptoConnection(context: Context) {
    companion object {
        val TAG = CryptoConnection::class.java.simpleName
    }

    val context: Context = context;
    var connectivityManager: ConnectivityManager? = null;

    fun connect() {

        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object: NetworkCallback() {
            override fun onAvailable(network: Network) {
                if(connectivityManager!!.bindProcessToNetwork(network)) {
                    Log.d(TAG, "Network onAvailable with the process.")
                    val conn = network.openConnection(URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=5&convert=USD&CMC_PRO_API_KEY=a00ddd82-efa6-4d34-8707-8eb241b81feb"))
                    conn.connectTimeout = 5000
                    conn.readTimeout = 5000
                    conn.doInput = true
                    conn.doOutput = false
                    Log.d(TAG, "Network onAvailable ready to connect")
                    conn.connect()
                    Log.d(TAG, "Network onAvailable connected")
                    val string = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                    Log.d(TAG, "Network done:$string")
                    //connectivityManager!!.releaseNetworkRequest()
                }
            }
        }

        val request: NetworkRequest = NetworkRequest.Builder().run {
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            build()
        }

        connectivityManager!!.requestNetwork(request, networkCallback)
    }

    class HTTPThread: Thread() {
        override fun run() {
            Log.d(TAG, "HTTP Connection start.")
            val url = URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=5&convert=USD&CMC_PRO_API_KEY=a00ddd82-efa6-4d34-8707-8eb241b81feb")
            val urlConn = url.openConnection()
            val httpConn = urlConn as HttpURLConnection
            httpConn.requestMethod = "GET"
            httpConn.connectTimeout = 5000
            httpConn.readTimeout = 5000
            httpConn.doInput = true
            httpConn.doOutput = false

            Log.d(TAG, "HTTP Connection ready.")
            val string = httpConn.inputStream.bufferedReader().use(BufferedReader::readText)
            Log.d(TAG, string)
            Log.d(TAG, "HTTP Connection output finished.")
            //httpConn.addRequestProperty("CMC_PRO_API_KEY", "a00ddd82-efa6-4d34-8707-8eb241b81feb")
            //ttpConn.connect()
            val resCode = httpConn.responseCode

            if(resCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "HTTP Result OK.")
                val string = httpConn.inputStream.bufferedReader().use(BufferedReader::readText)
                Log.d(TAG, string)
            } else {
                Log.d(TAG, "HTTP Result Failed:$resCode")
            }
            Log.d(TAG, "HTTP Connection end.")
        }
    }
    private val thread = HTTPThread()
    fun openHTTP() {
        thread.start()
    }
}