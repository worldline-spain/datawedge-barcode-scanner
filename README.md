# datawedge-barcode-scanner

Hello! This library will help you to add Zebra barcode support to your app!!

<img src="https://github.com/worldline-spain/datawedge-barcode-scanner/blob/master/art/demo.gif" width="250" />

# How to import

WIP

# Device Configuration

As you maybe know, all zebra devices have DataWedge support for scanning barcodes, but we need to configurate the app before using it:

### 1. Go to DataWedge app
<img src="https://github.com/worldline-spain/datawedge-barcode-scanner/blob/master/art/0.jpg" width="250" />

### 2. Create new DataWedge profile
<img src="https://github.com/worldline-spain/datawedge-barcode-scanner/blob/master/art/1.jpg" width="250" />

### 3. Add your app
Go to Applications -> Associated apps -> new

<img src="https://github.com/worldline-spain/datawedge-barcode-scanner/blob/master/art/2.jpg" width="250" />

### 4. Select activity
<img src="https://github.com/worldline-spain/datawedge-barcode-scanner/blob/master/art/4.jpg" width="250" />

### 5. Configure intent
Go back to the profile config, and scroll down to the "intent output" section, the section should looks like this:

IMPORTANT: The intent action MUST be the same than the app configured action, in this case is "com.worldline.default.ACTION" but obviously you can customize this action, feel free to change it:

<img src="https://github.com/worldline-spain/datawedge-barcode-scanner/blob/master/art/5.jpg" width="250" />

# App configuration

The library has a constructor with callback, where you will receive your scans (triggered by hardware or soft buttons)

```kotlin
DataWedge(private val context: Context,
          private val customAction: String = DEFAULT_ACTION,
          private val callback: (ScanData) -> Unit)
```

The configuration is really easy (you can use this library with java also), take a look to the sample, the activity looks like this:

```kotlin
class MainActivity : AppCompatActivity() {

    // There are two ways to instantiate the library
    // 1. By default
    private val dataWedge: DataWedge by lazy { DataWedge(context = this, callback = { handleScanData(it) }) }
    // 2. With custom action
    private val dataWedge: DataWedge by lazy {
        DataWedge(context = this, customAction = "com.mycompany.ACTION", callback = { handleScanData(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize when onCreate()
        dataWedge.initialize()

        registerListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Destroy when onDestroy()
        dataWedge.destroy()
    }

    private fun registerListeners() {
        // Trigger the soft scan button
        softTrigger.setOnClickListener { dataWedge.startSoftScan() }
    }

    private fun handleScanData(scanData: ScanData) {
        decodedBy.text = scanData.decodedBy
        code.text = scanData.code
        label.text = scanData.labelType
    }

}
```

That's all! Enjoy it!
# 
# Contributing to the project
Feel free to report any issues or suggest new features.

# Thanks
Special thanks @darryncampbell, his samples look really nice and help us to create the library :)

# License
Copyright 2018 Worldline Iberia

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
Synchronization is one of the biggest features of StackEdit. It enables you to synchronize any file in your workspace with other files stored in your **Google Drive**, your **Dropbox** and your **GitHub** accounts. This allows you to keep writing on other devices, collaborate with people you share the file with, integrate easily into your workflow... The synchronization mechanism takes place every minute in the background, downloading, merging, and uploading file modifications.
