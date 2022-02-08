package com.artrointel.everyonescomplication.example

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO

class CounterComplicationBroadcast : BroadcastReceiver() {
    private val TAG = CounterComplicationBroadcast::class.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        val dataSource = intent?.extras?.getParcelable<ComponentName>("dataSource")
        val complicationId = intent?.extras?.getInt("complicationId")

        val preferenceDAO = SharedPreferenceDAO(context!!, "default")
        val counter = preferenceDAO.reader().getInt("counter", 0)
        preferenceDAO.writer().putInt("counter", (counter+1) % 5)
        preferenceDAO.writer().apply()
        Log.e(TAG, "Counter value is ${preferenceDAO.reader().getInt("counter", -1)}")


        val requester = ComplicationDataSourceUpdateRequester.Companion.create(context, dataSource!!)
        requester.requestUpdate(complicationId!!)
        Log.d(TAG, "requested update for complication Id: $complicationId")
    }
}