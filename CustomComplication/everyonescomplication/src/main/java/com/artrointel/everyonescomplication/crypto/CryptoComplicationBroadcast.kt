package com.artrointel.everyonescomplication.crypto

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class CryptoComplicationBroadcast : BroadcastReceiver() {
    private val TAG = CryptoComplicationBroadcast::class.simpleName

    private var payload: CryptoPayload? = null

    private var updateIntervalInMillis: Long = 0L

    private val updateHandler = Handler(Looper.getMainLooper())

    override fun onReceive(context: Context?, intent: Intent?) {
        if(payload == null) {
            payload = CryptoPayload(context!!, intent?.extras!!)
        }
        Log.d(TAG, "onReceived from complicationId(${payload!!.complicationId})")


        if(payload?.handleByCommand() == true) {
            // Request update the complication directly.
            if(payload!!.complicationId != -1) {
                val requester = ComplicationDataSourceUpdateRequester.Companion.create(context!!, payload!!.dataSource!!)
                requester.requestUpdate(payload!!.complicationId)
                Log.d(TAG, "complicationId(${payload!!.complicationId}) update requested")
            }
            else {
                Log.e(TAG, "Invalid complication Id.")
            }

        }
    }
}