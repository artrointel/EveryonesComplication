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
import java.lang.Exception

class CryptoComplicationService : ComplicationDataSourceService() {
    private val TAG = CryptoComplicationService::class.simpleName

    private var complicationDataLatest: ComplicationData? = null
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

        when(request.complicationType){
            ComplicationType.SHORT_TEXT -> {
                try {
                    complicationDataLatest = createShortTextLineComplicationData(request, dataSource)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> {
                Log.e(TAG, "Unsupported ComplicationType: ${request.complicationType}")
            }
        }
        listener.onComplicationData(complicationDataLatest)
    }

    private fun createShortTextLineComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {

        val jsonData = cryptoPayload!!.getCryptoData()
        if(jsonData == null || jsonData == "") {
            Log.d(TAG, "Failed to get crypto data.")
        } else {
            Log.d(TAG, "JSON Data: $jsonData")
        }

        val cryptoInfo = CryptoParser()

            .load(jsonData)
            //.load("{\"status\":{\"timestamp\":\"2022-02-17T11:48:11.141Z\",\"error_code\":0,\"error_message\":null,\"elapsed\":18,\"credit_count\":1,\"notice\":null,\"total_count\":9412},\"data\":[{\"id\":1,\"name\":\"Bitcoin\",\"symbol\":\"BTC\",\"slug\":\"bitcoin\",\"num_market_pairs\":9160,\"date_added\":\"2013-04-28T00:00:00.000Z\",\"tags\":[\"mineable\",\"pow\",\"sha-256\",\"store-of-value\",\"state-channel\",\"coinbase-ventures-portfolio\",\"three-arrows-capital-portfolio\",\"polychain-capital-portfolio\",\"binance-labs-portfolio\",\"blockchain-capital-portfolio\",\"boostvc-portfolio\",\"cms-holdings-portfolio\",\"dcg-portfolio\",\"dragonfly-capital-portfolio\",\"electric-capital-portfolio\",\"fabric-ventures-portfolio\",\"framework-ventures-portfolio\",\"galaxy-digital-portfolio\",\"huobi-capital-portfolio\",\"alameda-research-portfolio\",\"a16z-portfolio\",\"1confirmation-portfolio\",\"winklevoss-capital-portfolio\",\"usv-portfolio\",\"placeholder-ventures-portfolio\",\"pantera-capital-portfolio\",\"multicoin-capital-portfolio\",\"paradigm-portfolio\"],\"max_supply\":21000000,\"circulating_supply\":18960850,\"total_supply\":18960850,\"platform\":null,\"cmc_rank\":1,\"self_reported_circulating_supply\":null,\"self_reported_market_cap\":null,\"last_updated\":\"2022-02-17T11:46:00.000Z\",\"quote\":{\"USD\":{\"price\":43138.55279718401,\"volume_24h\":21767064502.26856,\"volume_change_24h\":5.1901,\"percent_change_1h\":-0.15706312,\"percent_change_24h\":-2.37666928,\"percent_change_7d\":-4.22351048,\"percent_change_30d\":3.23593778,\"percent_change_60d\":-8.6644874,\"percent_change_90d\":-24.19854014,\"market_cap\":817943628804.4865,\"market_cap_dominance\":42.0066,\"fully_diluted_market_cap\":905909608740.86,\"last_updated\":\"2022-02-17T11:46:00.000Z\"}}}]}")
            .cryptoInfoList[cryptoPayload!!.accessor.reader().getInt(CryptoPayload.Key.CURRENT, 0)]

        // intent for tapAction with the Complication Data. it would show next text-line on tap action
        val intent = Payload.createPendingIntent(
            CryptoComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, CryptoPayload.Command.SET_NEXT.name)

        return ComplicationDataCreator.shortText(
            IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)), // TODO icon
            cryptoInfo.symbol, cryptoInfo.price.toString(), "description?", intent)
    }
}