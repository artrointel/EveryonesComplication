package com.artrointel.everyonescomplication.crypto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.customcomplication.utils.ComplicationDataCreator
import com.artrointel.everyonescomplication.R
import com.artrointel.everyonescomplication.crypto.model.CryptoInfo
import com.artrointel.everyonescomplication.crypto.model.CryptoParser
import com.artrointel.everyonescomplication.utils.IconCreator
import java.lang.Exception
import kotlin.math.abs
import android.os.Vibrator




class CryptoComplicationService : ComplicationDataSourceService() {
    private val TAG = "CryptoComplicationService"

    private val notificationChannelId = "CryptoCurrencyPriceChangeNotification"

    private var complicationDataLatest: ComplicationData? = null
    private var cryptoPayloadManager: CryptoPayloadManager? = null
    private var cryptoInfoLatest: CryptoInfo? = null

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
        createNotificationChannel()
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener) {
        Log.d(TAG, "onComplicationRequest id: ${request.complicationInstanceId}"
                + ", Type:${request.complicationType}")

        val dataSource = ComponentName(this, CryptoComplicationService::class.java)

        cryptoPayloadManager = CryptoPayloadManager.create(this, dataSource, request.complicationInstanceId, CryptoPayloadManager.Command.NONE)

        when(request.complicationType){
            ComplicationType.SHORT_TEXT -> {
                try {
                    onCryptoUpdateRequest()
                    complicationDataLatest = createShortTextComplicationData(request, dataSource)
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

    private fun onCryptoUpdateRequest() {
        cryptoInfoLatest = getCurrentCryptoInfo()
        val changePerHour = cryptoInfoLatest!!.percent_change_1h * 100.0f
        val changePerDay = cryptoInfoLatest!!.percent_change_24h * 100.0f

        // Make notification if needed
        if(cryptoPayloadManager!!.getCryptoConfig().notiEnabled) {
            // TODO get system time whether it's daylight or not
            if(abs(cryptoInfoLatest!!.percent_change_1h) * 100.0f > cryptoPayloadManager!!.getCryptoConfig().notiOnChange) {
                makeNotification("Price Changed Notification for " +
                        cryptoInfoLatest!!.symbol + ": " + cryptoInfoLatest!!.price +
                        String.format(", %.1f/h and %.1f/d", changePerHour, changePerDay) + "%")
            }
        }
        Toast.makeText(this, String.format("%.2f/h, %.2f/d", changePerHour, changePerDay) , Toast.LENGTH_LONG).show()
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val vibrationPattern = longArrayOf(0, 300, 100, 300, 100)
        val indexInPatternToRepeat = -1
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = notificationChannelId
            val descriptionText = "Crypto Currency Price Change Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun makeNotification(desc: String) {
        var builder = NotificationCompat.Builder(this, notificationChannelId)
        .setSmallIcon(R.drawable.crypto_btc)
        .setContentTitle(notificationChannelId)
        .setContentText(desc)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(this).apply {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
        }
    }

    private fun createShortTextComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {

        // intent for tapAction with the Complication Data.
        val intent = Payload.createPendingIntent(
            CryptoComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, CryptoPayloadManager.Command.REQUEST_REFRESH.name)

        return ComplicationDataCreator.shortText(
            IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)), // TODO icon
            cryptoInfoLatest!!.symbol,
            cryptoInfoLatest!!.price.toInt().toString(),
            " (" + String.format("%.1f",cryptoInfoLatest!!.percent_change_24h) + ")", intent)
    }

    private fun getCurrentCryptoInfo(): CryptoInfo {
        val jsonData = cryptoPayloadManager!!.getCryptoData()
        if(jsonData == "") {
            Toast.makeText(this, "Failed to get crypto data. Check your Private Key.", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Failed to get crypto data.")
        }

        return CryptoParser()
            .load(jsonData)
            .cryptoInfoList[cryptoPayloadManager!!.accessor.reader().getInt(CryptoPayloadManager.Key.CURRENT, 0)]
    }
}