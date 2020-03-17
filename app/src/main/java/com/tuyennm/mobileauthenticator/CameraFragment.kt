package com.tuyennm.mobileauthenticator

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_camera.*
import me.dm7.barcodescanner.core.CameraUtils
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap


class CameraFragment : Fragment(), ZXingScannerView.ResultHandler {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scannerView?.apply {
            scannerView.setFormats(ZXingScannerView.ALL_FORMATS)
            setResultHandler(this@CameraFragment)
            startCamera(CameraUtils.getDefaultCameraId())
            setAutoFocus(true)
        }
    }

    override fun handleResult(result: Result) {
//        otpauth://totp/GitHub:manhtuyen911?secret=eg7jv2pzptq3hjnk&issuer=GitHub
        val stringResult: String = result.text
        val resultUri = URI.create(stringResult)
        val querySplited = splitQuery(resultUri)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Scan Result")
        builder.setNegativeButton(
            "Again"
        ) { _, _ -> scannerView.resumeCameraPreview(this) }
        builder.setPositiveButton(
            "Go to"
        ) { _, _ ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(stringResult))
            startActivity(browserIntent)
        }
        builder.setMessage(stringResult)
        val alert = builder.create()
        alert.show()

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        df.timeZone = TimeZone.getTimeZone("UTC")
        val T0 = 0
        val X = 30
        var steps = "0"
        val testTime = Calendar.getInstance().timeInMillis
        val T: Long = (testTime - T0) / X
        steps = java.lang.Long.toHexString(T).toUpperCase(Locale.getDefault())
        while (steps.length < 16) steps = "0$steps"
        val fmtTime = String.format("%1$-11s", testTime)
        val utcTime: String = df.format(Date(testTime * 1000))
        val totp = TOTP.generateTOTP(querySplited?.get("secret"))

        print("totp = $totp")
    }

    override fun onResume() {
        super.onResume()

        scannerView?.resumeCameraPreview(this)
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()

        scannerView?.apply {
            flash = false
            stopCamera()
            stopCameraPreview()
        }
    }

    @Throws(UnsupportedEncodingException::class)
    fun splitQuery(url: URI): Map<String, String>? {
        val queryPairs: MutableMap<String, String> =
            LinkedHashMap()
        val query: String = url.query
        val pairs = query.split("&").toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] =
                URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
        }
        return queryPairs
    }
}
