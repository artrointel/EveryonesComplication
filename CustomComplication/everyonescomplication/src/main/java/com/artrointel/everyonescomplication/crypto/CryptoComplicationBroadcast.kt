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

    private fun requestUpdateByInterval(context: Context?, showNext: Boolean = true) {
        val updateRequester = ComplicationDataSourceUpdateRequester.create(context!!, payload!!.dataSource!!)
        if (showNext) {
            payload!!.setNext(true)
        }
        updateRequester.requestUpdate(payload!!.complicationId)

        Log.d(TAG, "request update by interval")
        updateHandler.postDelayed({ (this::requestUpdateByInterval)(context, showNext); }, updateIntervalInMillis)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(payload == null) {
            payload = CryptoPayload(context!!, intent?.extras!!)
        }
        Log.d(TAG, "onReceived from complicationId(${payload!!.complicationId})")


        if(payload?.handleByCommand() == true) {
            // Request update the complication directly.
            val requester = ComplicationDataSourceUpdateRequester.Companion.create(context!!, payload!!.dataSource!!)
            updateIntervalInMillis = payload!!.getInterval() * 1000L

            if(payload!!.complicationId != -1) {
                if(intent!!.getBooleanExtra(CryptoConfigurationActivity.INTENT_FROM_CONFIGURATION, false)) {
                    updateHandler.removeCallbacksAndMessages(null) // TODO not work.
                    requestUpdateByInterval(context, false)
                }
                else {
                    requester.requestUpdate(payload!!.complicationId)
                }
                Log.d(TAG, "complicationId(${payload!!.complicationId}) update requested")
            }
            else {
                Log.e(TAG, "Invalid complication Id.")
            }

        }
    }
}