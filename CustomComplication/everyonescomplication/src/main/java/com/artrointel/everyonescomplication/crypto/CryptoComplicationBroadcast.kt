package com.artrointel.everyonescomplication.crypto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class CryptoComplicationBroadcast : BroadcastReceiver() {
    private val TAG = "CryptoComplicationBroadcast"

    private var payload: CryptoPayload? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if(payload == null) {
            payload = CryptoPayload(context!!, intent?.extras!!)
            payload!!.onCryptoDataUpdated = Runnable {
                val updateRequester =
                    ComplicationDataSourceUpdateRequester.create(
                        context!!,
                        payload!!.dataSource!!
                    )
                updateRequester.requestUpdate(payload!!.complicationId)
                Log.d(TAG, "complicationId(${payload!!.complicationId}) update requested")
            }
        }
        Log.d(TAG, "onReceived from complicationId(${payload!!.complicationId})")

        payload?.handleByCommand()
    }
}