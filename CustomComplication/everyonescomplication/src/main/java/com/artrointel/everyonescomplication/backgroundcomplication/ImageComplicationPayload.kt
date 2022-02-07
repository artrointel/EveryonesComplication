package com.artrointel.everyonescomplication.backgroundcomplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.everyonescomplication.textlinecomplication.TextLinePayload

class ImageComplicationPayload(context: Context, payload: Bundle) : Payload(context, payload)  {
    companion object {
        private val TAG = this.javaClass.simpleName
        private val PKG_PREFIX: String = "com.artrointel.everyonescomplication.imagecomplication."
        val ACTION_IMAGE_COMPLICATION = PKG_PREFIX + "action"
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
            val IMAGE = PKG_PREFIX + "IMAGE"
        }
    }

    override fun handleByCommand() : Boolean{
        var needComplicationUpdated = false;

        when(extras.getString(Extra.COMMAND)) {
            Command.NEXT.name -> {
                Log.d(TAG, "Show Next Image")
                setNext()
                needComplicationUpdated = true
            }
            Command.SET.name -> {
                Log.d(TAG, "Set Image data")
                setImages()
                needComplicationUpdated = true
            }
            else -> {
                Log.e(TAG, "Unhandled Command!")
            }
        }
        accessor.writer().apply()

        return needComplicationUpdated
    }

    fun getCurrentImageBase64() : String {
        var idx = accessor.reader().getInt(Key.CURRENT, 0)
        return accessor.reader().getString(Key.IMAGE + idx.toString(), "")!!
    }

    fun getInterval() : Int {
        return accessor.reader().getInt(Key.INTERVAL, 0)
    }

    private fun setNext() {
        var idx : Int = accessor.reader().getInt(Key.CURRENT, 0)
        var size : Int = accessor.reader().getInt(Key.SIZE, 1) // modulo always to be 0 if size is 1

        accessor.writer().putInt(Key.CURRENT, (idx + 1) % size)
    }

    private fun setImages() {
        accessor.writer().clear()

        var size = extras.getInt(Key.SIZE, 0)
        for(i: Int in 0 until size) {
            var textLine = extras.getString(Key.IMAGE + i.toString())
            accessor.writer().putString(Key.IMAGE + i.toString(), textLine)
        }

        var interval = extras.getInt(Key.INTERVAL, 0)
        accessor.writer().putInt(Key.INTERVAL, interval)
    }
}