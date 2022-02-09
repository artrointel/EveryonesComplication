package com.artrointel.everyonescomplication.textline

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO
import com.artrointel.everyonescomplication.R
import com.artrointel.everyonescomplication.databinding.TextlineConfigurationsBinding
import com.artrointel.everyonescomplication.databinding.TextlineItemBinding
import java.lang.Exception

class TextLineConfigurationActivity : Activity() {
    private val TAG = TextLineConfigurationActivity::class.simpleName

    private lateinit var configurationsBinding: TextlineConfigurationsBinding
    private val receiver = TextLineComplicationBroadcast()
    private var complicationId: Int = -1

    companion object {
        val INTENT_FROM_CONFIGURATION = "com.artrointel.everyonescomplication.textline.TextLineConfigurationActivity";
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(receiver, TextLinePayload.getIntentFilter())
        complicationId = intent.getIntExtra(ComplicationDataSourceService.EXTRA_CONFIG_COMPLICATION_ID, -1)
        Log.d(TAG, "Configuration Opened with id: $complicationId")

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
        val dao = SharedPreferenceDAO(this)
        val size = dao.reader().getInt(TextLinePayload.Key.SIZE, 0)
        for(i in 0 until size) {
            val text = dao.reader().getString(TextLinePayload.Key.TEXTLINE_ + i.toString(), "")
            addTextLineItem(text!!)
        }

        var selectedUnit = 0
        var seconds = 0L
        var time = 0L
        try {
            selectedUnit = dao.reader().getInt(TextLinePayload.Key.INTERVAL_UNIT, 0)
            seconds = dao.reader().getLong(TextLinePayload.Key.INTERVAL, 0L)
            when(selectedUnit) {
                0 -> { // days
                    time = seconds / (3600*24L)
                }
                1 -> { // hours
                    time = seconds / 3600L
                }
                2 -> { // minutes
                    time = seconds / 60L
                }
                3 -> { // seconds
                    // do nothing
                    time = seconds
                }
                else -> {} // do nothing when there was no data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        configurationsBinding.itemInterval.spinner.setSelection(selectedUnit)
        configurationsBinding.itemInterval.edittext.setText(time.toString())


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
        var intent = TextLinePayload.createIntentForBroadcastAction(this, complicationId, TextLinePayload.Command.SET_DATA.name)
        intent.putExtra(INTENT_FROM_CONFIGURATION, true)
        val size = configurationsBinding.textContainer.childCount
        for (i in 0 until size) {
            val item = configurationsBinding.textContainer.getChildAt(i) as LinearLayout
            val editText = item.getChildAt(0) as EditText
            intent.putExtra(TextLinePayload.Key.TEXTLINE_ + i.toString(), editText.text.toString())
        }
        intent.putExtra(TextLinePayload.Key.SIZE, size)

        //
        val unitOfTime = configurationsBinding.itemInterval.spinner.selectedItem
        var seconds = 0L
        try {
            seconds = configurationsBinding.itemInterval.edittext.text.toString().toLong()
            when (unitOfTime) {
                resources.getStringArray(R.array.unit_of_time)[0] -> { // days
                    seconds *= 3600*24L
                }
                resources.getStringArray(R.array.unit_of_time)[1] -> { // hours
                    seconds *= 3600L
                }
                resources.getStringArray(R.array.unit_of_time)[2] -> { // minutes
                    seconds *= 60L
                }
                resources.getStringArray(R.array.unit_of_time)[3] -> { // seconds
                    // Do nothing.
                }
                else -> {
                    Log.e(TAG, "Unsupported unit of time")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Interval is not set")
        }
        intent.putExtra(TextLinePayload.Key.INTERVAL, seconds)
        intent.putExtra(TextLinePayload.Key.INTERVAL_UNIT, configurationsBinding.itemInterval.spinner.selectedItemPosition)

        sendBroadcast(intent)
        setResult(RESULT_OK)
        finish()
    }

    private fun onCanceled() {
        setResult(RESULT_CANCELED)
        finish()
    }
}