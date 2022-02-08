package com.artrointel.everyonescomplication.textlinecomplication

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import com.artrointel.customcomplication.boundary.Payload
import com.artrointel.everyonescomplication.databinding.TextlineConfigurationsBinding
import com.artrointel.everyonescomplication.databinding.TextlineItemBinding

class TextLineConfigurationActivity : Activity() {

    private lateinit var configurationsBinding: TextlineConfigurationsBinding
    private val receiver = TextLineComplicationBroadcast()


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(receiver, TextLinePayload.getIntentFilter())

        configurationsBinding = TextlineConfigurationsBinding.inflate(layoutInflater)
        initializeConfigActivity()
        setContentView(configurationsBinding.root)

    }

    @Override
    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun initializeConfigActivity() {
        configurationsBinding.imageViewAdd.setOnClickListener {
            addTextlineItem()
        }

        configurationsBinding.buttonApply.setOnClickListener {
            applyChanges()
        }

        configurationsBinding.buttonCancel.setOnClickListener {
            onCanceled()
        }
    }

    private fun addTextlineItem() {
        val textlineItemBinding = TextlineItemBinding.inflate(layoutInflater)
        textlineItemBinding.buttonDelete.setOnClickListener {
            configurationsBinding.textContainer.removeView(textlineItemBinding.itemTextline)
        }
        configurationsBinding.textContainer.addView(textlineItemBinding.itemTextline)
    }

    private fun applyChanges() {
        intent = TextLinePayload.createIntentForBroadcastAction(TextLinePayload.Command.SET.name)
        val size = configurationsBinding.textContainer.childCount
        for (i in 0 until size) {
            val item = configurationsBinding.textContainer.getChildAt(i) as LinearLayout
            val editText = item.getChildAt(0) as EditText
            intent.putExtra(TextLinePayload.Key.TEXTLINE + i.toString(), editText.text.toString())
        }
        intent.putExtra(TextLinePayload.Key.SIZE, size)
        sendBroadcast(intent)
        setResult(RESULT_OK)
        finish()
    }

    private fun onCanceled() {
        setResult(RESULT_CANCELED)
        finish()
    }
}