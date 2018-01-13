package com.worldline.datawedge_barcode_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * DataWedge
 */
class DataWedge(private val context: Context,
                private val customAction: String = DEFAULT_ACTION,
                private val callback: (ScanData) -> Unit) {
    companion object {
        /**
         * Intent action
         * */
        private val DEFAULT_ACTION = "com.worldline.default.ACTION"

        /**
         * Parameters to get the scan value
         * */
        private val KEY_SOURCE = "com.symbol.datawedge.source"
        private val LABEL_TYPE = "com.symbol.datawedge.label_type"
        private val DATA = "com.symbol.datawedge.data_string"

        /**
         * Parameters to get the scan value by legacy way
         * */
        private val KEY_SOURCE_LEGACY = "com.motorolasolutions.emdk.datawedge.source"
        private val LABEL_TYPE_LEGACY = "com.motorolasolutions.emdk.datawedge.label_type"
        private val DATA_LEGACY = "com.motorolasolutions.emdk.datawedge.data_string"

        /**
         * Parameters to trigger the soft button
         * */
        private val SOFT_TRIGGER = "com.symbol.datawedge.api.ACTION_SOFTSCANTRIGGER"
        private val TRIGGER_EXTRA_PARAMETER = "com.symbol.datawedge.api.EXTRA_PARAMETER"

        private val START_SCANNING = "START_SCANNING"
        private val STOP_SCANNING = "STOP_SCANNING"
    }

    private val dataWedgeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == customAction) {
                val scanData = handleScanData(intent)
                callback(scanData)
            }
        }
    }

    fun startSoftScan() {
        softTrigger(START_SCANNING)
    }

    fun stopSoftScan() {
        softTrigger(STOP_SCANNING)
    }

    private fun softTrigger(scanEvent: String) {
        val intent = Intent()
        intent.action = SOFT_TRIGGER
        intent.putExtra(TRIGGER_EXTRA_PARAMETER, scanEvent)

        context.sendBroadcast(intent)
    }

    fun initialize() {
        val filter = IntentFilter()
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addAction(customAction)
        context.registerReceiver(dataWedgeReceiver, filter)
    }

    fun destroy() {
        context.unregisterReceiver(dataWedgeReceiver)
    }

    private fun handleScanData(intent: Intent): ScanData {
        var source = intent.getStringExtra(KEY_SOURCE)
        var data = intent.getStringExtra(DATA)
        var labelType = intent.getStringExtra(LABEL_TYPE)

        // if source is null, try to get data by legacy way
        if (source == null) {
            source = intent.getStringExtra(KEY_SOURCE_LEGACY)
            data = intent.getStringExtra(DATA_LEGACY)
            labelType = intent.getStringExtra(LABEL_TYPE_LEGACY)
        }

        return ScanData(source, data, labelType)
    }
}

data class ScanData(val decodedBy: String, val code: String, val labelType: String)