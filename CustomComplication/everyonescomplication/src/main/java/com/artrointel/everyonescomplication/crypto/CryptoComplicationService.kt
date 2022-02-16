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
import com.artrointel.everyonescomplication.crypto.model.CryptoParser
import com.artrointel.everyonescomplication.utils.IconCreator

class CryptoComplicationService : ComplicationDataSourceService() {
    private val TAG = CryptoComplicationService::class.simpleName

    private var cryptoPayload: CryptoPayload? = null

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        var complicationDataPreview : ComplicationData? = null
        when(type){
            ComplicationType.SHORT_TEXT -> {
                complicationDataPreview = ComplicationDataCreator.shortText(
                    IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)),
                    resources.getString(R.string.crypto_short_text_title),
                    resources.getString(R.string.crypto_short_text_text))
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
        listener.onComplicationData(complicationData!!)
    }

    private fun createShortTextLineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {

        val jsonData = cryptoPayload!!.getCryptoData()
        if(jsonData == null || jsonData == "") {
            Log.d(TAG, "Failed to get crypto data.")
        }
        val cryptoInfo = CryptoParser()
            .load(jsonData)
            .cryptoInfoList[cryptoPayload!!.accessor.reader().getInt(CryptoPayload.Key.CURRENT, 0)]

        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        val intent = Payload.createPendingIntent(
            CryptoComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, CryptoPayload.Command.SET_NEXT.name)

        return ComplicationDataCreator.shortText(
            IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)), // TODO icon
            cryptoInfo.symbol,
            cryptoInfo.price.toString(), "description?", intent)
    }


}