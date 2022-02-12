package com.artrointel.everyonescomplication.crypto

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO
import com.artrointel.everyonescomplication.R
import com.artrointel.everyonescomplication.databinding.CryptoConfigurationsBinding
import com.artrointel.everyonescomplication.databinding.TextlineConfigurationsBinding
import com.artrointel.everyonescomplication.databinding.TextlineItemBinding
import java.lang.Exception

class CryptoConfigurationActivity : Activity() {
    private val TAG = CryptoConfigurationActivity::class.simpleName

    private lateinit var configurationsBinding: CryptoConfigurationsBinding
    private val receiver = CryptoComplicationBroadcast()
    private var complicationId: Int = -1

    companion object {
        val INTENT_FROM_CONFIGURATION = "com.artrointel.everyonescomplication.textline.TextLineConfigurationActivity";
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(receiver, CryptoPayload.getIntentFilter())
        complicationId = intent.getIntExtra(ComplicationDataSourceService.EXTRA_CONFIG_COMPLICATION_ID, -1)
        Log.d(TAG, "Configuration Opened with id: $complicationId")

        configurationsBinding = CryptoConfigurationsBinding.inflate(layoutInflater)
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

        // TODO bind data top x, alarm data

        configurationsBinding.buttonApply.setOnClickListener {
            applyChanges()
        }

        configurationsBinding.buttonCancel.setOnClickListener {
            onCanceled()
        }
    }

    private fun applyChanges() {
        var intent = CryptoPayload.createIntentForBroadcastAction(this, complicationId, CryptoPayload.Command.SET_DATA.name)
        intent.putExtra(INTENT_FROM_CONFIGURATION, true)

        // TODO save top x, alarm data

        sendBroadcast(intent)
        setResult(RESULT_OK)
        finish()
    }

    private fun onCanceled() {
        setResult(RESULT_CANCELED)
        finish()
    }
}