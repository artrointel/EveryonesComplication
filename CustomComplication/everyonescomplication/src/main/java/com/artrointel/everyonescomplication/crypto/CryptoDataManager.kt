package com.artrointel.everyonescomplication.crypto

import com.artrointel.everyonescomplication.crypto.model.CryptoInfo

class CryptoDataManager {
    var cryptoDataTop10List = ArrayList<CryptoInfo>()

    // parse data
    // create CryptoInfo from the data
    // create

    fun getData(index: Int) : CryptoInfo {
        return CryptoInfo("",0f, "", 0f, 0f)
    }
}