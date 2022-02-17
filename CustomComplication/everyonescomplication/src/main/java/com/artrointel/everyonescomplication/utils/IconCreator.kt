package com.artrointel.everyonescomplication.utils

import android.graphics.BitmapFactory
import android.graphics.drawable.Icon

import android.util.Base64


class IconCreator {

    companion object {
        fun createFromBase64(base64Image: String) : Icon {
            val decodedString: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            return Icon.createWithAdaptiveBitmap(decodedByte)
        }
    }
}