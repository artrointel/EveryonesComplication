package com.artrointel.everyonescomplication.test

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.wear.complications.data.ComplicationData
import androidx.wear.complications.data.ComplicationType
import androidx.wear.complications.datasource.ComplicationDataSourceService
import androidx.wear.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO
import com.artrointel.customcomplication.utils.ComplicationDataCreator
import com.artrointel.everyonescomplication.R

class CounterComplicationService : ComplicationDataSourceService() {
    private val TAG = this.javaClass.simpleName


    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return null
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.d(TAG, "on2ComplicationRequest id:" + request.complicationInstanceId + ", Type:" + request.complicationType)

        var dataSource = ComponentName(this, "CounterComplicationService")
        Log.d(TAG, "KWH dataSource String name : " + "CounterComplicationService")

        var complicationId = request.complicationInstanceId
        var complicationData: ComplicationData? = null
        var preferenceDAO = SharedPreferenceDAO(this)
        var counter = preferenceDAO.reader().getInt("counter", -1)

        var intent = Intent()
        intent.putExtra("dataSource", dataSource)
        intent.putExtra("complicationId", complicationId)

        var pendingIntent = PendingIntent.getBroadcast(
            this, complicationId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        when(request.complicationType){
            ComplicationType.LONG_TEXT -> {
                complicationData = ComplicationDataCreator.longText(counter.toString(), counter.toString(), pendingIntent)
            }
            ComplicationType.SHORT_TEXT -> {
                complicationData = ComplicationDataCreator.shortText(counter.toString(), counter.toString(), pendingIntent)

            }
        }
        listener.onComplicationData(complicationData!!)
    }
}