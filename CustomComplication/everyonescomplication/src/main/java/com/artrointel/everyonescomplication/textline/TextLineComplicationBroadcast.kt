package com.artrointel.everyonescomplication.textline

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class TextLineComplicationBroadcast : BroadcastReceiver() {
    private val TAG = TextLineComplicationBroadcast::class.simpleName

    private var payload: TextLinePayload? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if(payload == null) {
            payload = TextLinePayload(context!!, intent?.extras!!)
        }
        Log.d(TAG, "onReceived from complicationId(${payload!!.complicationId})")

        if(payload?.handleByCommand() == true) {
            val requester = ComplicationDataSourceUpdateRequester.Companion.create(context!!, payload!!.dataSource!!)
            if(payload!!.complicationId != -1) {
                requester.requestUpdate(payload!!.complicationId)
            }
            else {
                requester.requestUpdateAll()
            }

            Log.d(TAG, "complicationId(${payload!!.complicationId}) update requested")
        }
    }
}