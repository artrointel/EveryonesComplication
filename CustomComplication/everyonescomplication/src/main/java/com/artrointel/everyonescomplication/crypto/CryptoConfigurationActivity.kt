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
        val INTENT_FROM_CONFIGURATION = "com.artrointel.everyonescomplication.textline.CryptoConfigurationActivity";
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(receiver, CryptoPayloadManager.getIntentFilter())
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
        configurationsBinding.cryptoMarketTopText.setText(dao.reader().getInt(CryptoPayloadManager.Key.SIZE, 1).toString())
        configurationsBinding.cryptoNotiSwitch.isChecked = dao.reader().getBoolean(CryptoPayloadManager.Key.NOTI_ENABLED, false)
        configurationsBinding.cryptoChangeForNoti.setText(dao.reader().getFloat(CryptoPayloadManager.Key.NOTI_ON_CHANGE, 0.0f).toString())
        configurationsBinding.cryptoPrivateKey.setText(dao.reader().getString(CryptoPayloadManager.Key.PRIVATE_KEY, ""))

        configurationsBinding.buttonApply.setOnClickListener {
            applyChanges()
        }

        configurationsBinding.buttonCancel.setOnClickListener {
            onCanceled()
        }
    }

    private fun applyChanges() {
        var intent = CryptoPayloadManager.createIntentForBroadcastAction(this, complicationId, CryptoPayloadManager.Command.SET_CONFIG.name)
        intent.putExtra(INTENT_FROM_CONFIGURATION, true)

        val numberOfTopCoins = configurationsBinding.cryptoMarketTopText.text.toString().toInt()
        val notificationEnabled = configurationsBinding.cryptoNotiSwitch.isChecked
        val changeForNoti = configurationsBinding.cryptoChangeForNoti.text.toString().toFloat()
        val privateKey = configurationsBinding.cryptoPrivateKey.text.toString()

        // TODO check whether the private key is valid or not.

        intent.putExtra(CryptoPayloadManager.Key.SIZE, numberOfTopCoins)
        intent.putExtra(CryptoPayloadManager.Key.NOTI_ENABLED, notificationEnabled)
        intent.putExtra(CryptoPayloadManager.Key.NOTI_ON_CHANGE, changeForNoti)
        intent.putExtra(CryptoPayloadManager.Key.PRIVATE_KEY, privateKey)

        sendBroadcast(intent)
        setResult(RESULT_OK)
        finish()
    }

    private fun onCanceled() {
        setResult(RESULT_CANCELED)
        finish()
    }
}