package com.rk.billing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var web: WebView
    private var uploadCallback: ValueCallback<Array<android.net.Uri>>? = null
    private val fileRequest = 4101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        setContentView(web)
        requestBluetoothPermissions()
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.addJavascriptInterface(PrintBridge(this, web), "RKAndroid")
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(v: WebView, callback: ValueCallback<Array<android.net.Uri>>, params: FileChooserParams): Boolean {
                uploadCallback?.onReceiveValue(null)
                uploadCallback = callback
                return try { startActivityForResult(params.createIntent(), fileRequest); true } catch (_: Exception) { uploadCallback = null; false }
            }
        }
        web.loadUrl("file:///android_asset/index.html")
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= 31) {
            val needed = arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            val missing = needed.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
            if (missing.isNotEmpty()) ActivityCompat.requestPermissions(this, missing.toTypedArray(), 4102)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileRequest) {
            val result = if (resultCode == Activity.RESULT_OK) WebChromeClient.FileChooserParams.parseResult(resultCode, data) else null
            uploadCallback?.onReceiveValue(result)
            uploadCallback = null
        }
    }

    override fun onBackPressed() { if (web.canGoBack()) web.goBack() else super.onBackPressed() }
}

class PrintBridge(private val context: Context, private val web: WebView) {
    @JavascriptInterface fun printReceipt() {
        (context as Activity).runOnUiThread {
            val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            manager.print("RK Billing receipt", web.createPrintDocumentAdapter("RK Billing"), PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build())
        }
    }
    @JavascriptInterface fun toast(message: String) { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
}
