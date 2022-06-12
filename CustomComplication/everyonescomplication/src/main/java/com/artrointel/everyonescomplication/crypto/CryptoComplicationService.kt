package com.artrointel.everyonescomplication.crypto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.icu.text.SimpleDateFormat
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
import java.util.*


class CryptoComplicationService : ComplicationDataSourceService() {
    private val TAG = "CryptoComplicationService"
    private val notificationChannelId = "BTC Price Changed"

    private var complicationDataLatest: ComplicationData? = null
    private var cryptoPayload: CryptoPayload? = null
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
            ComplicationType.LONG_TEXT -> {
                complicationDataPreview = ComplicationDataCreator.longText(
                    IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)),
                    resources.getString(R.string.crypto_long_text_title),
                    resources.getString(R.string.crypto_long_text_text))
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

        cryptoPayload = CryptoPayload.create(this, dataSource, request.complicationInstanceId, CryptoPayload.Command.NONE)

        try {
            onCryptoUpdateRequest()
            when(request.complicationType) {
                ComplicationType.SHORT_TEXT -> {
                    complicationDataLatest = createShortTextComplicationData(request, dataSource)
                }
                ComplicationType.LONG_TEXT -> {
                    complicationDataLatest = createLongTextComplicationData(request, dataSource)
                }
                else -> {
                    Log.e(TAG, "Unsupported ComplicationType: ${request.complicationType}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        listener.onComplicationData(complicationDataLatest)
    }

    // Update latest data and makes Notification, Toast and Vibration if needed.
    private fun onCryptoUpdateRequest() {
        cryptoInfoLatest = getCurrentCryptoInfo()
        val changePerHour = cryptoInfoLatest!!.percent_change_1h
        val changePerDay = cryptoInfoLatest!!.percent_change_24h

        // Make notification if needed
        if(cryptoPayload!!.getCryptoConfig().notiEnabled) {
            val hour = String.format(SimpleDateFormat("HH").format(Calendar.getInstance().time)).toInt()
            if(hour !in 0..6) { // do not wake user up at the midnight.
                if(abs(cryptoInfoLatest!!.percent_change_1h) > cryptoPayload!!.getCryptoConfig().notiOnChange) {
                    makeNotification(String.format(SimpleDateFormat("HH:MM:SS").format(Calendar.getInstance().time)) +
                            "\nPrice Changed Notification for\n" +
                            cryptoInfoLatest!!.symbol + ": " + cryptoInfoLatest!!.price +
                            String.format("\n%.1f%%/h and %.1f%%/d", changePerHour, changePerDay))

                    makeVibration()
                }
            }

        }
        Toast.makeText(this, getCurrentPriceChangeString(true), Toast.LENGTH_LONG).show()
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

    private fun makeVibration() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val vibrationPattern = longArrayOf(0, 300, 100, 300, 100)
        val indexInPatternToRepeat = -1
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat)
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
            dataSource, request.complicationInstanceId, CryptoPayload.Command.REQUEST_REFRESH.name)

        return ComplicationDataCreator.shortText(
            IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)), // TODO icon
            cryptoInfoLatest!!.symbol,
            cryptoInfoLatest!!.price.toInt().toString(),
            " (" + String.format("%.1f",cryptoInfoLatest!!.percent_change_24h) + ")", intent)
    }

    private fun createLongTextComplicationData(
        request: ComplicationRequest, dataSource: ComponentName)
            : ComplicationData {

        // intent for tapAction with the Complication Data.
        val intent = Payload.createPendingIntent(
            CryptoComplicationBroadcast::class.java,
            this,
            dataSource, request.complicationInstanceId, CryptoPayload.Command.REQUEST_REFRESH.name)

        return ComplicationDataCreator.longText(
            IconCreator.createFromBase64(resources.getString(R.string.crypto_short_text_icon)), // TODO icon
            cryptoInfoLatest!!.symbol + " " + cryptoInfoLatest!!.price.toInt().toString(),
            text = getCurrentPriceChangeString(),
            " (" + String.format("%.1f",cryptoInfoLatest!!.percent_change_24h) + ")", intent)
    }

    private fun getCurrentCryptoInfo(): CryptoInfo {
        val jsonData = cryptoPayload!!.getCryptoData()
        if(jsonData == "") {
            Toast.makeText(this, "Put your valid private Key", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Failed to get crypto data. invalid private key in normal case.")
        }

        return CryptoParser()
            .load(jsonData)
            .cryptoInfoList[cryptoPayload!!.accessor.reader().getInt(CryptoPayload.Key.CURRENT, 0)]
    }

    private fun getCurrentPriceChangeString(newLine: Boolean = false): String {
        val changePerHour = cryptoInfoLatest!!.percent_change_1h
        val changePerDay = cryptoInfoLatest!!.percent_change_24h
        return String.format("%.2f%%/h${if(newLine) "\n"; else {", "}}%.2f%%/d", changePerHour, changePerDay)
    }
}