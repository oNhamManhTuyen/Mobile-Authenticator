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


class CameraFragment : Fragment(), ZXingScannerView.ResultHandler {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scannerView.apply {
            scannerView.setFormats(ZXingScannerView.ALL_FORMATS)
            setResultHandler(this@CameraFragment)
            startCamera(CameraUtils.getDefaultCameraId())
            setAutoFocus(true)
        }
    }

    override fun handleResult(result: Result) {
        val stringResult: String = result.text
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

//        TOTP.generateTOTP()
    }

    override fun onResume() {
        super.onResume()

        scannerView.resumeCameraPreview(this)
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()

        scannerView.apply {
            flash = false
            stopCamera()
            stopCameraPreview()
        }
    }
}
