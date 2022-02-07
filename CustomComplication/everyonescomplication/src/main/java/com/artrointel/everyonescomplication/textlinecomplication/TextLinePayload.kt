package com.artrointel.everyonescomplication.textlinecomplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.artrointel.customcomplication.boundary.Payload

class TextLinePayload(context: Context, payload: Bundle) : Payload(context, payload) {
    companion object {
        private val TAG = this.javaClass.simpleName
        private val PKG_PREFIX: String = "com.artrointel.everyonescomplication.textlinecomplication."
        val ACTION_TEXTLINE_COMPLICATION = PKG_PREFIX + "action"
    }

    enum class Command {
        NEXT,
        SET,
    }

    class Key {
        companion object {
            val SIZE = PKG_PREFIX + "SIZE"
            val CURRENT = PKG_PREFIX + "CURRENT"
            val INTERVAL = PKG_PREFIX + "INTERVAL" // TODO in hour?
            val TEXTLINE = PKG_PREFIX + "TEXTLINE"
        }
    }

    override fun handleByCommand() : Boolean{
        var needComplicationUpdated = false;

        when(extras.getString(Extra.COMMAND)) {
            Command.NEXT.name -> {
                Log.d(TAG, "Show next text")
                setNext()
                needComplicationUpdated = true
            }
            Command.SET.name -> {
                Log.d(TAG, "Set text data")
                setTextLines()
                needComplicationUpdated = true
            }
            else -> {
                Log.e(TAG, "Unhandled Command!")
            }
        }
        accessor.writer().apply()

        return needComplicationUpdated
    }

    fun getCurrentTextLine() : String {
        var idx = accessor.reader().getInt(Key.CURRENT, 0)
        return accessor.reader().getString(Key.TEXTLINE + idx.toString(), "")!!
    }

    fun getInterval() : Int {
        return accessor.reader().getInt(Key.INTERVAL, 0)
    }

    private fun setNext() {
        var idx : Int = accessor.reader().getInt(Key.CURRENT, 0)
        var size : Int = accessor.reader().getInt(Key.SIZE, 1) // modulo always to be 0 if size is 1

        accessor.writer().putInt(Key.CURRENT, (idx + 1) % size)
        Log.d(TAG, "SHOW:"+accessor.reader().getString(Key.TEXTLINE + idx, "err")) // TODO dbg
    }

    private fun setTextLines() {
        accessor.writer().clear()

        var size = extras.getInt(Key.SIZE, 0)
        for(i: Int in 0 until size) {
            var textLine = extras.getString(Key.TEXTLINE + i.toString())
            accessor.writer().putString(Key.TEXTLINE + i.toString(), textLine)
            Log.d(TAG, "Wrote text : " + textLine);
        }

        accessor.writer().putInt(Key.SIZE, size);

        var interval = extras.getInt(Key.INTERVAL, 0)
        accessor.writer().putInt(Key.INTERVAL, interval)
    }
}