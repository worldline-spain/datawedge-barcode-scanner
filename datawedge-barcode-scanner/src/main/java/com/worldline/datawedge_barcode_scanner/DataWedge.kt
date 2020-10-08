package com.worldline.datawedge_barcode_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import java.util.*

/**
 * DataWedge
 */
class DataWedge(private val context: Context,
                private val callback: (ScanData) -> Unit) {
    companion object {
        /**
         * Intent action
         * */
        private const val ACTION_RESULT_DATAWEDGE = "com.symbol.datawedge.api.RESULT_ACTION"
        private const val ACTIVITY_INTENT_FILTER_ACTION = "com.zebra.dwmultiactivity.ACTION"
        /**
         * Parameters to create profile datawedge
         * */
        const val PROFILE_NAME = "DataWedgeTest" // You can change profile name here
        private const val DATAWEDGE_SEND_ACTION = "com.symbol.datawedge.api.ACTION"
        private const val DATAWEDGE_SEND_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"
        /**
         * Parameters to get the scan value
         * */
        private const val KEY_SOURCE = "com.symbol.datawedge.source"
        private const val LABEL_TYPE = "com.symbol.datawedge.label_type"
        private const val DATA = "com.symbol.datawedge.data_string"

        /**
         * Parameters to get the scan value by legacy way
         * */
        private const val KEY_SOURCE_LEGACY = "com.motorolasolutions.emdk.datawedge.source"
        private const val LABEL_TYPE_LEGACY = "com.motorolasolutions.emdk.datawedge.label_type"
        private const val DATA_LEGACY = "com.motorolasolutions.emdk.datawedge.data_string"

        /**
         * Parameters to trigger the soft button
         * */
        private const val SOFT_TRIGGER = "com.symbol.datawedge.api.ACTION_SOFTSCANTRIGGER"
        private const val TRIGGER_EXTRA_PARAMETER = "com.symbol.datawedge.api.EXTRA_PARAMETER"

        private const val START_SCANNING = "START_SCANNING"
        private const val STOP_SCANNING = "STOP_SCANNING"
    }

    private val dataWedgeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == ACTIVITY_INTENT_FILTER_ACTION) {
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
        createProfile()
        val filter = IntentFilter()
        filter.addAction(ACTION_RESULT_DATAWEDGE)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addAction(ACTIVITY_INTENT_FILTER_ACTION)
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


    private fun createProfile() {

        // Configure profile to apply to this app
        val bMain = Bundle()
        bMain.putString("PROFILE_NAME", PROFILE_NAME)
        bMain.putString("PROFILE_ENABLED", "true")
        bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST") // Create profile if it does not exist

        // Configure barcode input plugin
        val bConfigBarcode = Bundle()
        bConfigBarcode.putString("PLUGIN_NAME", "BARCODE")
        bConfigBarcode.putString("RESET_CONFIG", "true") //  This is the default

        // PARAM_LIST bundle properties
        val bParamsBarcode = Bundle()
        bParamsBarcode.putString("scanner_selection", "auto")
        bParamsBarcode.putString("scanner_input_enabled", "true")
        bParamsBarcode.putString("decoder_code128", "true")
        bParamsBarcode.putString("decoder_ean13", "true")

        // Bundle "bParamsBarcode" within bundle "bConfigBarcode"
        bConfigBarcode.putBundle("PARAM_LIST", bParamsBarcode)

        // Associate appropriate activity to profile
        val appConfig = Bundle()
        appConfig.putString("PACKAGE_NAME", context.packageName)

        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        bMain.putParcelableArray("APP_LIST", arrayOf(appConfig))

        // Configure intent output for captured data to be sent to this app
        val bConfigIntent = Bundle()
        bConfigIntent.putString("PLUGIN_NAME", "INTENT")
        bConfigIntent.putString("RESET_CONFIG", "true")

        // Set params for intent output
        val bParamsIntent = Bundle()
        bParamsIntent.putString("intent_output_enabled", "true")
        bParamsIntent.putString("intent_action", "com.zebra.dwmultiactivity.ACTION")
        bParamsIntent.putString("intent_delivery", "2")

        // Bundle "bParamsIntent" within bundle "bConfigIntent"
        bConfigIntent.putBundle("PARAM_LIST", bParamsIntent)

        // Place both "bConfigBarcode" and "bConfigIntent" bundles into arraylist bundle
        val bundlePluginConfig = ArrayList<Bundle>()
        bundlePluginConfig.add(bConfigBarcode)
        bundlePluginConfig.add(bConfigIntent)

        // Place bundle arraylist into "bMain" bundle
        bMain.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig)

        // Apply configs using SET_CONFIG: http://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
        val dwIntent = Intent()
        dwIntent.action = DATAWEDGE_SEND_ACTION
        dwIntent.putExtra(DATAWEDGE_SEND_SET_CONFIG, bMain)
        context.sendBroadcast(dwIntent)
    }

}

data class ScanData(val decodedBy: String, val code: String, val labelType: String)