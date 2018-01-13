package com.worldline.datawedge

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.worldline.datawedge_barcode_scanner.DataWedge
import com.worldline.datawedge_barcode_scanner.ScanData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val dataWedge: DataWedge by lazy { DataWedge(context = this, callback = { handleScanData(it) }) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataWedge.initialize()

        registerListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        dataWedge.destroy()
    }

    private fun registerListeners() {
        softTrigger.setOnClickListener { dataWedge.startSoftScan() }
    }

    private fun handleScanData(scanData: ScanData) {
        decodedBy.text = scanData.decodedBy
        code.text = scanData.code
        label.text = scanData.labelType
    }

}
