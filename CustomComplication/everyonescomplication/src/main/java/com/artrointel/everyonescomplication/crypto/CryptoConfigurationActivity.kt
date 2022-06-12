package com.artrointel.everyonescomplication.crypto

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.artrointel.customcomplication.boundary.SharedPreferenceDAO
import com.artrointel.everyonescomplication.databinding.CryptoConfigurationsBinding

class CryptoConfigurationActivity : Activity() {
    private val TAG = CryptoConfigurationActivity::class.simpleName

    private lateinit var configurationsBinding: CryptoConfigurationsBinding
    private val receiver = CryptoComplicationBroadcast()
    private var complicationId: Int = -1

    companion object {
        val INTENT_FROM_CONFIGURATION = "com.artrointel.everyonescomplication.crypto.CryptoConfigurationActivity";
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
        configurationsBinding.cryptoMarketTopText.setText(dao.reader().getInt(CryptoPayload.Key.SIZE, 1).toString())
        configurationsBinding.cryptoNotiSwitch.isChecked = dao.reader().getBoolean(CryptoPayload.Key.NOTI_ENABLED, false)
        configurationsBinding.cryptoChangeForNoti.setText(dao.reader().getFloat(CryptoPayload.Key.NOTI_ON_CHANGE, 2.0f).toString())
        configurationsBinding.cryptoPrivateKey.setText(dao.reader().getString(CryptoPayload.Key.PRIVATE_KEY, "a00ddd82-efa6-4d34-8707-8eb241b81feb"))

        configurationsBinding.buttonApply.setOnClickListener {
            applyChanges()
        }

        configurationsBinding.buttonCancel.setOnClickListener {
            onCanceled()
        }
    }

    private fun applyChanges() {
        var intent = CryptoPayload.createIntentForBroadcastAction(this, complicationId, CryptoPayload.Command.SET_CONFIG.name)
        intent.putExtra(INTENT_FROM_CONFIGURATION, true)

        val numberOfTopCoins = configurationsBinding.cryptoMarketTopText.text.toString().toInt()
        val notificationEnabled = configurationsBinding.cryptoNotiSwitch.isChecked
        val changeForNoti = configurationsBinding.cryptoChangeForNoti.text.toString().toFloat()
        val privateKey = configurationsBinding.cryptoPrivateKey.text.toString().replace(" ", "");

        intent.putExtra(CryptoPayload.Key.SIZE, numberOfTopCoins)
        intent.putExtra(CryptoPayload.Key.NOTI_ENABLED, notificationEnabled)
        intent.putExtra(CryptoPayload.Key.NOTI_ON_CHANGE, changeForNoti)
        intent.putExtra(CryptoPayload.Key.PRIVATE_KEY, privateKey)

        sendBroadcast(intent)
        setResult(RESULT_OK)
        finish()
    }

    private fun onCanceled() {
        setResult(RESULT_CANCELED)
        finish()
    }
}