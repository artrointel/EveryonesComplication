package com.artrointel.customcomplication.boundary

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle

abstract class Payload(protected var context: Context, protected var extras: Bundle) {
    var dataSource: ComponentName? = extras.getParcelable(Extra.DATA_SOURCE)
        private set

    var complicationId: Int = extras.getInt(Extra.COMPLICATION_ID, -1)
        private set

    var accessor: SharedPreferenceDAO = SharedPreferenceDAO(context, Extra.COMPLICATION_ID + complicationId.toString())
        private set

    class Extra {
        companion object {
            private var PKG_PREFIX = "com.artrointel.customcomplication."
            var DATA_SOURCE: String = PKG_PREFIX + "DATA_SOURCE"
            var COMPLICATION_ID: String = PKG_PREFIX + "COMPLICATION_ID"
            var COMMAND: String = PKG_PREFIX + "COMMAND"
        }
    }

    /**
     * Implemented from derived class along the business model.
     * handle the payload by command.
     */
    abstract fun handleByCommand() : Boolean;

    companion object {
        fun createPendingIntent(
            receiver: Class<out BroadcastReceiver>, context: Context,
            dataSource: ComponentName, complicationId: Int,
            command: String) : PendingIntent {

            return PendingIntent.getBroadcast(
                context, complicationId,
                createIntent(receiver, context, dataSource, complicationId, command),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun createIntent(
            receiver: Class<out BroadcastReceiver>, context: Context,
            dataSource: ComponentName, complicationId: Int, command: String) : Intent {
            return Intent(context, receiver)
                .putExtra(Extra.DATA_SOURCE, dataSource)
                .putExtra(Extra.COMPLICATION_ID, complicationId)
                .putExtra(Extra.COMMAND, command)
        }
    }
}