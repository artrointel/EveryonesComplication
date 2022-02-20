package com.artrointel.customcomplication.utils

import android.app.PendingIntent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*

class ComplicationDataCreator {
    companion object {
        fun shortText(
            text: String, desc: String = text,
            tapAction: PendingIntent? = null) : ShortTextComplicationData {

            return ShortTextComplicationData.Builder(
                complicationText(text),
                complicationText(desc))
                .setTapAction(tapAction).build()
        }

        fun shortText(
            icon: Icon, title: String, text: String, desc: String = text,
            tapAction: PendingIntent? = null) : ShortTextComplicationData {

            return ShortTextComplicationData.Builder(
                complicationText(text),
                complicationText(desc))
                .setTitle(complicationText(title))
                .setMonochromaticImage(MonochromaticImage.Builder(icon).build())
                .setTapAction(tapAction).build()
        }

        fun longText(
            text: String, desc: String = text,
            tapAction: PendingIntent? = null) : LongTextComplicationData {

            return LongTextComplicationData.Builder(
                complicationText(text),
                complicationText(desc))
                .setTapAction(tapAction).build()
        }

        fun longText(
            icon: Icon,
            title: String,
            text: String, desc: String = text,
            tapAction: PendingIntent? = null) : LongTextComplicationData {

            return LongTextComplicationData.Builder(
                complicationText(text),
                complicationText(desc))
                .setTitle(complicationText(title))
                .setMonochromaticImage(MonochromaticImage.Builder(icon).build())
                .setTapAction(tapAction).build()
        }

        fun smallImage(
            icon: Icon, type: SmallImageType = SmallImageType.ICON, desc: String = "",
            tapAction: PendingIntent? = null) : SmallImageComplicationData {

            return SmallImageComplicationData.Builder(
                SmallImage.Builder(icon, type).build(),
                complicationText(desc))
                .setTapAction(tapAction).build()
        }

        fun photoImage(
            icon: Icon, desc: String = "",
            tapAction: PendingIntent? = null) : PhotoImageComplicationData {

            return PhotoImageComplicationData.Builder(
                icon, complicationText(desc))
                .setTapAction(tapAction).build()
        }

        fun monochromaticImage(
            icon: Icon, desc: String = "",
            tapAction: PendingIntent? = null) : MonochromaticImageComplicationData{

            return MonochromaticImageComplicationData.Builder(
                MonochromaticImage.Builder(icon).build(),
                complicationText(desc))
                .setTapAction(tapAction).build()
        }

        private fun complicationText(string: String = "") : ComplicationText {
            return when (string) {
                "" -> ComplicationText.EMPTY
                else -> PlainComplicationText.Builder(string).build()
            }
        }
    }
}