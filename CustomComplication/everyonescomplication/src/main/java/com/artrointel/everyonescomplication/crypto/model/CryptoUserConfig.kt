package com.artrointel.everyonescomplication.crypto.model

import com.artrointel.everyonescomplication.crypto.CryptoPayload

class CryptoUserConfig(size: Int, notiEnabled: Boolean, notiOnChange: Float, privateKey: String) {
    val size = size
    val notiEnabled = notiEnabled
    val notiOnChange = notiOnChange
    val privateKey = privateKey
}