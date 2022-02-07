package com.artrointel.everyonescomplication.test

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.complications.datasource.ComplicationDataSourceUpdateRequester
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO

class CounterComplicationBroadcast : BroadcastReceiver() {
    private val TAG = javaClass.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive:")
        var dataSource = intent?.extras?.getParcelable<ComponentName>("dataSource")
        var complicationId = intent?.extras?.getInt("complicationId")

        var preferenceDAO = SharedPreferenceDAO(context!!)
        var counter = preferenceDAO.reader().getInt("counter", 0)
        preferenceDAO.writer().putInt("counter", (counter+1) % 5)
        preferenceDAO.writer().apply()


        var requester = ComplicationDataSourceUpdateRequester.Companion.create(context!!, dataSource!!)
        requester.requestUpdate(complicationId!!)
        Log.d(TAG, "requested update for complication Id:" + complicationId)
    }
}