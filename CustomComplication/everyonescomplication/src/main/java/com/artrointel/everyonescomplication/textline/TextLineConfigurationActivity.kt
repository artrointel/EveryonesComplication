package com.artrointel.everyonescomplication.textline

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO
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
        var dao = SharedPreferenceDAO(this)
        var size = dao.reader().getInt(TextLinePayload.Key.SIZE, 0)
        for(i in 0 until size) {
            var text = dao.reader().getString(TextLinePayload.Key.TEXTLINE_ + i.toString(), "")
            addTextLineItem(text!!)
        }

        configurationsBinding.imageViewAdd.setOnClickListener {
            addTextLineItem()
        }

        configurationsBinding.buttonApply.setOnClickListener {
            applyChanges()
        }

        configurationsBinding.buttonCancel.setOnClickListener {
            onCanceled()
        }
    }

    private fun addTextLineItem(text: String = "") {
        val textLineItemBinding = TextlineItemBinding.inflate(layoutInflater)
        if (text != "") {
            textLineItemBinding.edittext.setText(text)
        }
        textLineItemBinding.buttonDelete.setOnClickListener {
            configurationsBinding.textContainer.removeView(textLineItemBinding.itemTextline)
        }
        configurationsBinding.textContainer.addView(textLineItemBinding.itemTextline)
    }

    private fun applyChanges() {
        intent = TextLinePayload.createIntentForBroadcastAction(this, TextLinePayload.Command.SET_DATA.name)
        val size = configurationsBinding.textContainer.childCount
        for (i in 0 until size) {
            val item = configurationsBinding.textContainer.getChildAt(i) as LinearLayout
            val editText = item.getChildAt(0) as EditText
            intent.putExtra(TextLinePayload.Key.TEXTLINE_ + i.toString(), editText.text.toString())
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