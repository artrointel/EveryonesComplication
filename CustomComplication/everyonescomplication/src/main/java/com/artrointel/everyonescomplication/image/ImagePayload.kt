package com.artrointel.everyonescomplication.image

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.artrointel.customcomplication.boundary.Payload

class ImagePayload(context: Context, payload: Bundle) : Payload(context, payload)  {
    companion object {
        private val TAG = ImagePayload::class.simpleName
        private const val PKG_PREFIX: String = "com.artrointel.everyonescomplication.imagecomplication."
        private const val ACTION_IMAGE_COMPLICATION = PKG_PREFIX + "action"
    }

    enum class Command {
        SET_NEXT,
        SET_DATA,
    }

    class Key {
        companion object {
            const val SIZE = PKG_PREFIX + "SIZE"
            const val CURRENT = PKG_PREFIX + "CURRENT"
            const val INTERVAL = PKG_PREFIX + "INTERVAL" // TODO in hour?
            const val IMAGE_ = PKG_PREFIX + "IMAGE_"
        }
    }

    override fun handleByCommand() : Boolean{
        var needComplicationUpdated = false

        when(extras.getString(Extra.COMMAND)) {
            Command.SET_NEXT.name -> {
                Log.d(TAG, "Show Next Image")
                setNext()
                needComplicationUpdated = true
            }
            Command.SET_DATA.name -> {
                Log.d(TAG, "Set Image data")
                setImageData()
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
        val idx = accessor.reader().getInt(Key.CURRENT, 0)
        return accessor.reader().getString(Key.IMAGE_ + idx.toString(), "")!!
    }

    fun getInterval() : Int {
        return accessor.reader().getInt(Key.INTERVAL, 0)
    }

    private fun setNext() {
        val idx : Int = accessor.reader().getInt(Key.CURRENT, 0)
        val size : Int = accessor.reader().getInt(Key.SIZE, 1) // modulo always to be 0 if size is 1

        accessor.writer().putInt(Key.CURRENT, (idx + 1) % size)
    }

    private fun setImageData() {
        accessor.writer().clear()

        val size = extras.getInt(Key.SIZE, 0)
        accessor.writer().putInt(Key.SIZE, size)

        for(i: Int in 0 until size) {
            val base64Image = extras.getString(Key.IMAGE_ + i.toString())
            accessor.writer().putString(Key.IMAGE_ + i.toString(), base64Image)
        }

        val interval = extras.getInt(Key.INTERVAL, 0)
        accessor.writer().putInt(Key.INTERVAL, interval)
    }
}