package com.artrointel.everyonescomplication.example

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO
import com.artrointel.customcomplication.utils.ComplicationDataCreator

// Refer to
// https://android.googlesource.com/platform/frameworks/support/+/dc52bff1c11947ed0dae6f55bb7089fe6b5758cb/wear/watchface/watchface-complications-data-source-samples/src/main/java/androidx/wear/watchface/complications/datasource/samples/BackgroundDataSourceService.kt

class CounterComplicationService : ComplicationDataSourceService() {
    private val TAG = javaClass.simpleName

    var updateRequester: ComplicationDataSourceUpdateRequester? = null

    override fun onCreate() {
        if(updateRequester == null) {
            updateRequester = ComplicationDataSourceUpdateRequester.create(this, ComponentName(this, CounterComplicationService::class.java))
        }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return null
    }

    fun test() {
        Log.d(TAG, "test");
        updateRequester?.requestUpdateAll()
        Handler(Looper.getMainLooper()).postDelayed(this::test, 1000)
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        Log.d(TAG, "onComplicationActivated id:" + complicationInstanceId + ", Type:" + type)
        //test()
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.d(TAG, "on2ComplicationRequest id:" + request.complicationInstanceId + ", Type:" + request.complicationType)

        var dataSource = ComponentName(this, CounterComplicationService::class.java)
        Log.d(TAG, "KWH dataSource String name : " + CounterComplicationService::class.java)

        var complicationId = request.complicationInstanceId
        var complicationData: ComplicationData? = null
        var preferenceDAO = SharedPreferenceDAO(this, "default")
        var counter = preferenceDAO.reader().getInt("counter", -1)

        var intent = Intent(this, CounterComplicationBroadcast::class.java)
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