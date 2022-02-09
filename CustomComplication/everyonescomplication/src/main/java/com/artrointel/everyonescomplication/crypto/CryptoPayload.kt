package com.artrointel.everyonescomplication.crypto

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.artrointel.customcomplication.boundary.Payload

class CryptoPayload(context: Context, payload: Bundle) : Payload(context, payload) {
    companion object {
        private val TAG = CryptoPayload::class.simpleName
        private const val PKG_PREFIX: String = "com.artrointel.everyonescomplication.textlinecomplication."
        private const val ACTION_TEXT_LINE_COMPLICATION = PKG_PREFIX + "action"

        fun create(context: Context, dataSource: ComponentName, complicationId: Int, command: Command) : CryptoPayload {
            val payload = Bundle()
            payload.putParcelable(Extra.DATA_SOURCE, dataSource)
            payload.putInt(Extra.COMPLICATION_ID, complicationId)
            payload.putString(Extra.COMMAND, command.name)
            return CryptoPayload(context, payload)
        }

        fun createIntentForBroadcastAction(context: Context, complicationId: Int, command: String) : Intent {
            val intent = Intent()
            intent.action = ACTION_TEXT_LINE_COMPLICATION
            intent.putExtra(Extra.DATA_SOURCE,  ComponentName(context, CryptoComplicationService::class.java))
            intent.putExtra(Extra.COMPLICATION_ID, complicationId)
            intent.putExtra(Extra.COMMAND, command)
            return intent
        }

        fun getIntentFilter(): IntentFilter {
            return IntentFilter(ACTION_TEXT_LINE_COMPLICATION)
        }
    }

    enum class Command {
        NONE,
        SET_NEXT,
        REQUEST_REFRESH,
        SET_DATA, // private apikey, alarm, show crypto top x, update frequency, etc
    }

    class Key {
        companion object {
            const val SIZE = PKG_PREFIX + "SIZE"
            const val CURRENT = PKG_PREFIX + "CURRENT"
            const val INTERVAL = PKG_PREFIX + "INTERVAL" // in seconds
            const val INTERVAL_UNIT = PKG_PREFIX + "INTERVAL_UNIT"
            const val TEXTLINE_ = PKG_PREFIX + "TEXTLINE_"
        }
    }

    override fun handleByCommand() : Boolean{
        var needComplicationUpdated = false

        when(extras.getString(Extra.COMMAND)) {
            Command.REQUEST_REFRESH.name -> {
                setNext()
                needComplicationUpdated = true
            }
            Command.SET_DATA.name -> {
                setTextLineData()
                needComplicationUpdated = true
            }
            else -> {
                Log.e(TAG, "Unhandled Command!")
            }
        }
        accessor.writer().apply()

        return needComplicationUpdated
    }

    fun getCurrentText() : String {
        val idx = accessor.reader().getInt(Key.CURRENT, 0)
        return accessor.reader().getString(Key.TEXTLINE_ + idx.toString(), "")!!
    }

    fun getInterval() : Long {
        return accessor.reader().getLong(Key.INTERVAL, 0L)
    }

    fun setNext(apply: Boolean = false) {
        val idx : Int = accessor.reader().getInt(Key.CURRENT, 0)
        val size : Int = accessor.reader().getInt(Key.SIZE, 1) // modulo always to be 0 if size is 1

        accessor.writer().putInt(Key.CURRENT, (idx + 1) % size)

        if(apply) accessor.writer().apply()
    }

    private fun setTextLineData() {
        accessor.writer().clear()

        val size = extras.getInt(Key.SIZE, 0)
        accessor.writer().putInt(Key.SIZE, size)

        for(i: Int in 0 until size) {
            val textLine = extras.getString(Key.TEXTLINE_ + i.toString())
            accessor.writer().putString(Key.TEXTLINE_ + i.toString(), textLine)
        }

        val interval = extras.getLong(Key.INTERVAL, 0L)
        accessor.writer().putLong(Key.INTERVAL, interval)

        val selectedItem = extras.getInt(Key.INTERVAL_UNIT, 0)
        accessor.writer().putInt(Key.INTERVAL_UNIT, selectedItem)
    }
}