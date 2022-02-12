package com.artrointel.everyonescomplication.crypto

import android.content.ComponentName
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.customcomplication.utils.ComplicationDataCreator
import com.artrointel.everyonescomplication.R

class CryptoComplicationService : ComplicationDataSourceService() {
    private val TAG = CryptoComplicationService::class.simpleName

    private var cryptoPayload: CryptoPayload? = null
    private var previewDataLatest: ComplicationData? = null

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        var complicationDataPreview : ComplicationData? = null
        when(type){
            ComplicationType.LONG_TEXT -> {
                complicationDataPreview = ComplicationDataCreator.longText(resources.getString(R.string.preview_long_textline))
            }
            ComplicationType.SHORT_TEXT -> {
                complicationDataPreview = ComplicationDataCreator.shortText(resources.getString(R.string.preview_long_textline))
            }
            else -> {
                Log.e(TAG, "Unsupported ComplicationType: $type")
            }
        }
        return complicationDataPreview
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        Log.d(TAG, "onComplicationActivated id:$complicationInstanceId, Type:$type")
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.d(TAG, "onComplicationRequest id: ${request.complicationInstanceId}"
                + ", Type:${request.complicationType}")

        val dataSource = ComponentName(this, CryptoComplicationService::class.java)

        cryptoPayload = CryptoPayload.create(this, dataSource, request.complicationInstanceId, CryptoPayload.Command.NONE)

        var complicationData: ComplicationData? = null
        when(request.complicationType){
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

    private fun createShortTextLineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {
        // TODO
        val text = cryptoPayload!!.getCurrentCrypto()
        // Parse crypto json
        // val icon is from Bitmap from base64
        // val title is symbol
        // val text is price


        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        val intent = Payload.createPendingIntent(
            CryptoComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, CryptoPayload.Command.REQUEST_REFRESH.name)

        return ComplicationDataCreator.shortText(
            text, text, intent)
    }
}