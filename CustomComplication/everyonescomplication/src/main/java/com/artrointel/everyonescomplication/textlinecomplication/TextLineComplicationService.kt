package com.artrointel.everyonescomplication.textlinecomplication

import android.content.ComponentName
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

class TextLineComplicationService : ComplicationDataSourceService() {
    private val TAG = this.javaClass.simpleName

    private var textLinePayload: TextLinePayload? = null

    private var previewDataLatest: ComplicationData? = null

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return previewDataLatest
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.e(TAG, "onComplicationRequest id:" + request.complicationInstanceId + ", Type:" + request.complicationType)

        var dataSource = ComponentName(this, TextLineComplicationService::class::simpleName.toString())

        textLinePayload = TextLinePayload(this, Bundle())

        var complicationData: ComplicationData? = null
        when(request.complicationType){
            ComplicationType.LONG_TEXT -> {
                complicationData = createTextlineComplicationData(request, dataSource)
            }
            ComplicationType.SHORT_TEXT -> {
                complicationData = createShortTextlineComplicationData(request, dataSource)
            }
        }
        previewDataLatest = complicationData
        listener.onComplicationData(complicationData!!)
    }

    private fun createTextlineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        var text = textLinePayload!!.getCurrentTextLine()
        var intent = Payload.createPendingIntent(
            TextLineComplicationBroadcast::class.java, this,
            dataSource, request.complicationInstanceId, TextLinePayload.Command.NEXT.name)

        return ComplicationDataCreator.longText(
            text, text, intent)
    }

    private fun createShortTextlineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        var text = textLinePayload!!.getCurrentTextLine()
        var intent = Payload.createPendingIntent(
            TextLineComplicationBroadcast::class.java, this,
            dataSource, request.complicationInstanceId, TextLinePayload.Command.NEXT.name)

        return ComplicationDataCreator.shortText(
            text, text, intent)
    }
}