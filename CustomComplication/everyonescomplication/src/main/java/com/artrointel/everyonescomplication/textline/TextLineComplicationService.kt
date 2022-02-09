package com.artrointel.everyonescomplication.textline

import android.content.ComponentName
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.customcomplication.utils.ComplicationDataCreator
import com.artrointel.everyonescomplication.R

class TextLineComplicationService : ComplicationDataSourceService() {
    private val TAG = TextLineComplicationService::class.simpleName

    private var textLinePayload: TextLinePayload? = null
    private var previewDataLatest: ComplicationData? = null

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if(textLinePayload != null) {
            Log.d(TAG, "getPreviewData: ${textLinePayload!!.getCurrentTextLine()}")
            previewDataLatest = ComplicationDataCreator.longText(textLinePayload!!.getCurrentTextLine())
        }

        if(previewDataLatest == null) {
            previewDataLatest = ComplicationDataCreator.longText(resources.getString(R.string.preview_textline))
        }
        return previewDataLatest
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        Log.d(TAG, "onComplicationActivated id:$complicationInstanceId, Type:$type")
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.d(TAG, "onComplicationRequest id: ${request.complicationInstanceId}"
                + ", Type:${request.complicationType}")

        val dataSource = ComponentName(this, TextLineComplicationService::class.java)

        textLinePayload = TextLinePayload.create(this, dataSource, request.complicationInstanceId, TextLinePayload.Command.NONE)

        var complicationData: ComplicationData? = null
        when(request.complicationType){
            ComplicationType.LONG_TEXT -> {
                complicationData = createLongTextLineComplicationData(request, dataSource)
            }
            ComplicationType.SHORT_TEXT -> {
                complicationData = createShortTextLineComplicationData(request, dataSource)
            }
            else -> {
                Log.e(TAG, "Unsupported ComplicationType: ${request.complicationType}")
            }
        }
        previewDataLatest = complicationData
        listener.onComplicationData(complicationData!!)
    }

    private fun createLongTextLineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        val text = textLinePayload!!.getCurrentTextLine()

        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        val intent = Payload.createPendingIntent(
            TextLineComplicationBroadcast::class.java,
            this, dataSource, request.complicationInstanceId, TextLinePayload.Command.SET_NEXT.name)

        return ComplicationDataCreator.longText(
            text, text, intent)
    }

    private fun createShortTextLineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        val text = textLinePayload!!.getCurrentTextLine()

        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        val intent = Payload.createPendingIntent(
            TextLineComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, TextLinePayload.Command.SET_NEXT.name)

        return ComplicationDataCreator.shortText(
            text, text, intent)
    }
}