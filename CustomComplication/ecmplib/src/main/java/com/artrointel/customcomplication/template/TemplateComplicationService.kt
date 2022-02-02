package com.artrointel.customcomplication.template

import android.content.ComponentName
import android.graphics.drawable.Icon
import androidx.wear.complications.data.ComplicationData
import androidx.wear.complications.data.ComplicationType
import androidx.wear.complications.datasource.ComplicationDataSourceService
import androidx.wear.complications.datasource.ComplicationRequest
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.customcomplication.utils.ComplicationDataCreator

class TemplateComplicationService : ComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        TODO("Not yet implemented")
        return ComplicationDataCreator.shortText("foo")
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        var dataSource = ComponentName(this, TemplateComplicationBroadcastReceiver::class.simpleName.toString())

    }


}