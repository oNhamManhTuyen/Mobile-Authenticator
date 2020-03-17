package com.tuyennm.mobileauthenticator

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_camera.*
import me.dm7.barcodescanner.core.CameraUtils
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder


class CameraFragment : Fragment(), ZXingScannerView.ResultHandler {

    companion object {
        const val TAG = "CameraFragment"
    }

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
        val resultUri = URI.create(result.text)
        val querySplited = splitQuery(resultUri)

        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent(Constant.ACTION_RECEIVE_SECRET_KEY).apply {
                putExtra(Constant.EXTRA_ACCOUNT, resultUri.path.substring(1))
                putExtra(Constant.EXTRA_SECRET_KEY, querySplited?.get("secret"))
            })

        activity?.supportFragmentManager?.popBackStack()
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
