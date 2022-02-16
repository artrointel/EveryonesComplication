package com.artrointel.everyonescomplication.crypto

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.everyonescomplication.crypto.model.CryptoAPI
import com.artrointel.everyonescomplication.crypto.model.CryptoConnection
import com.artrointel.everyonescomplication.crypto.model.CryptoUserConfig

class CryptoPayload(context: Context, payload: Bundle) : Payload(context, payload) {
    companion object {
        private val TAG = CryptoPayload::class.simpleName
        private const val PKG_PREFIX: String = "com.artrointel.everyonescomplication.textlinecomplication."
        private const val ACTION_CRYPTO_COMPLICATION = PKG_PREFIX + "action"

        fun create(context: Context, dataSource: ComponentName, complicationId: Int, command: Command) : CryptoPayload {
            val payload = Bundle()
            payload.putParcelable(Extra.DATA_SOURCE, dataSource)
            payload.putInt(Extra.COMPLICATION_ID, complicationId)
            payload.putString(Extra.COMMAND, command.name)
            return CryptoPayload(context, payload)
        }

        fun createIntentForBroadcastAction(context: Context, complicationId: Int, command: String) : Intent {
            val intent = Intent()
            intent.action = ACTION_CRYPTO_COMPLICATION
            intent.putExtra(Extra.DATA_SOURCE,  ComponentName(context, CryptoComplicationService::class.java))
            intent.putExtra(Extra.COMPLICATION_ID, complicationId)
            intent.putExtra(Extra.COMMAND, command)
            return intent
        }

        fun getIntentFilter(): IntentFilter {
            return IntentFilter(ACTION_CRYPTO_COMPLICATION)
        }
    }

    enum class Command {
        NONE,
        SET_NEXT,
        REQUEST_REFRESH,
        SET_CONFIG,
    }

    class Key {
        companion object {
            const val SIZE = PKG_PREFIX + "SIZE"
            const val CURRENT = PKG_PREFIX + "CURRENT"
            const val NOTI_ENABLED = PKG_PREFIX + "ENABLED"
            const val NOTI_ON_CHANGE = PKG_PREFIX + "NOTI"
            const val CRYPTO_DATA = PKG_PREFIX + "CRYPTO_" // JSON
            const val PRIVATE_KEY = PKG_PREFIX + "PRIVATE_KEY"
        }
    }

    override fun handleByCommand() : Boolean{
        var needComplicationUpdated = false

        when(extras.getString(Extra.COMMAND)) {
            Command.SET_NEXT.name -> {
                setNext()
                needComplicationUpdated = true
            }
            Command.SET_CONFIG.name -> {
                setCryptoConfig()
                needComplicationUpdated = true
            }
            else -> {
                Log.e(TAG, "Unhandled Command!")
            }
        }
        accessor.writer().apply()

        return needComplicationUpdated
    }

    fun setNext(apply: Boolean = false) {
        val idx : Int = accessor.reader().getInt(Key.CURRENT, 0)
        val size : Int = accessor.reader().getInt(Key.SIZE, 1) // modulo always to be 0 if size is 1

        accessor.writer().putInt(Key.CURRENT, (idx + 1) % size)
    }

    fun queryCryptoData() {
        Log.d(TAG, "query to update Crypto Data.")
        val key = accessor.reader().getString(Key.PRIVATE_KEY, "")
        val api = CryptoAPI(key)
        val connTest = CryptoConnection(context)
        connTest.connect()
        //val jsonData = api.queryTopPrices(accessor.reader().getInt(Key.SIZE, 1))
        //accessor.writer().putString(Key.CRYPTO_DATA, jsonData)
    }

    fun getCryptoConfig(): CryptoUserConfig {
        val size = accessor.reader().getInt(Key.SIZE, 0)
        val notiEnabled = accessor.reader().getBoolean(Key.NOTI_ENABLED, false)
        val notiOnChange = accessor.reader().getFloat(Key.NOTI_ON_CHANGE, 0.0f)
        val privateKey = accessor.reader().getString(Key.PRIVATE_KEY, "")
        return CryptoUserConfig(size, notiEnabled, notiOnChange, privateKey!!)
    }

    private fun setCryptoConfig() {
        accessor.writer().clear()

        // Size of the crypto data
        val size = extras.getInt(Key.SIZE, 0)
        accessor.writer().putInt(Key.SIZE, size)

        // Enabled Notification
        val notiEnabled = extras.getBoolean(Key.NOTI_ENABLED, false)
        accessor.writer().putBoolean(Key.NOTI_ENABLED, notiEnabled)

        // NotiOnChange
        val notiOnChange = extras.getFloat(Key.NOTI_ON_CHANGE, 0.0f)
        accessor.writer().putFloat(Key.NOTI_ON_CHANGE, notiOnChange)

        // Private Key
        val privateKey = extras.getString(Key.PRIVATE_KEY, "")
        accessor.writer().putString(Key.PRIVATE_KEY, privateKey)
    }

    fun getCryptoData() : String {
        return accessor.reader().getString(Key.CRYPTO_DATA, "")!!
    }
}