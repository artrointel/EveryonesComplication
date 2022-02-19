package com.artrointel.everyonescomplication.crypto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class CryptoComplicationBroadcast : BroadcastReceiver() {
    private val TAG = "CryptoComplicationBroadcast"

    private var payloadManager: CryptoPayloadManager? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if(payloadManager == null) {
            payloadManager = CryptoPayloadManager(context!!, intent?.extras!!)
            payloadManager!!.onCryptoDataUpdated = Runnable {
                val updateRequester =
                    ComplicationDataSourceUpdateRequester.create(
                        context!!,
                        payloadManager!!.dataSource!!
                    )
                updateRequester.requestUpdate(payloadManager!!.complicationId)
                Log.d(TAG, "complicationId(${payloadManager!!.complicationId}) update requested")
            }
        }
        Log.d(TAG, "onReceived from complicationId(${payloadManager!!.complicationId})")

        payloadManager?.handleByCommand()
    }
}