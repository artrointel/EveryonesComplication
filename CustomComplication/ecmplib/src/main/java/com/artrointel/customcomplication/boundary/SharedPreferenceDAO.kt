package com.artrointel.customcomplication.boundary

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceDAO(private var context: Context, private var name: String) {
    private val preferences: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()


    fun reader() : SharedPreferences {
        return preferences
    }

    fun writer() : SharedPreferences.Editor {
        return editor
    }
}