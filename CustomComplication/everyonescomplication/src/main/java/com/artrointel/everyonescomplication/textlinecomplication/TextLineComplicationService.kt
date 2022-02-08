package com.artrointel.everyonescomplication.textlinecomplication

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.customcomplication.utils.ComplicationDataCreator
import com.artrointel.everyonescomplication.R

class TextLineComplicationService : ComplicationDataSourceService() {
    private val TAG = javaClass.simpleName

    private var textLinePayload: TextLinePayload? = null
    private var previewDataLatest: ComplicationData? = null

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if(textLinePayload != null) {
            Log.d(TAG, "getPreviewData:" + textLinePayload!!.getCurrentTextLine());
            previewDataLatest = ComplicationDataCreator.longText(textLinePayload!!.getCurrentTextLine())
        }

        if(previewDataLatest == null) {
            previewDataLatest = ComplicationDataCreator.longText(resources.getString(R.string.preview_textline))
        }
        return previewDataLatest
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.d(TAG, "onComplicationRequest id:" + request.complicationInstanceId
                + ", Type:" + request.complicationType)

        var dataSource = ComponentName(this, TextLineComplicationService::class.java)

        textLinePayload = TextLinePayload.create(this, dataSource, request.complicationInstanceId, TextLinePayload.Command.NONE)

        var complicationData: ComplicationData? = null
        when(request.complicationType){
            ComplicationType.LONG_TEXT -> {
                complicationData = createLongTextlineComplicationData(request, dataSource)
            }
            ComplicationType.SHORT_TEXT -> {
                complicationData = createShortTextlineComplicationData(request, dataSource)
            }
        }
        previewDataLatest = complicationData
        listener.onComplicationData(complicationData!!)
    }

    private fun createLongTextlineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        var text = textLinePayload!!.getCurrentTextLine()

        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        var intent = Payload.createPendingIntent(
            TextLineComplicationBroadcast::class.java,
            this, dataSource, request.complicationInstanceId, TextLinePayload.Command.NEXT.name)

        return ComplicationDataCreator.longText(
            text, text, intent)
    }

    private fun createShortTextlineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        var text = textLinePayload!!.getCurrentTextLine()

        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        var intent = Payload.createPendingIntent(
            TextLineComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, TextLinePayload.Command.NEXT.name)

        return ComplicationDataCreator.shortText(
            text, text, intent)
    }
}