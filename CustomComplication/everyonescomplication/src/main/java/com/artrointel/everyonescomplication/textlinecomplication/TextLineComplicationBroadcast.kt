package com.artrointel.everyonescomplication.textlinecomplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class TextLineComplicationBroadcast : BroadcastReceiver() {
    private val TAG = javaClass.simpleName

    private var payload: TextLinePayload? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if(payload == null) {
            payload = TextLinePayload(context!!, intent?.extras!!)
        }
        Log.d(TAG, "onReceive:" + payload!!.complicationId)

        if(payload?.handleByCommand() == true && payload!!.complicationId != -1) {
            var requester = ComplicationDataSourceUpdateRequester.Companion.create(context!!, payload!!.dataSource!!)
            requester.requestUpdate(payload!!.complicationId!!)
            Log.d(TAG, "complication(" + payload!!.complicationId + ") update requested")
        }
    }
}