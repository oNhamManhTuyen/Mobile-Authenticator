package com.tuyennm.mobileauthenticator

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CAMERA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showAddedAccountsFragment()

        fab.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission())
                requestPermission()
            else
                showCameraToScanQRCode()
        }
    }

    private fun showAddedAccountsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AddedAccountFragment())
            .addToBackStack(AddedAccountFragment.TAG)
            .commit()
    }

    private fun showCameraToScanQRCode() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CameraFragment())
            .addToBackStack(CameraFragment.TAG)
            .commit()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA -> if (grantResults.isNotEmpty()) {
                val cameraAccepted =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted) {
                    showCameraToScanQRCode()

                    showToast(getString(R.string.permission_granted_can_access_camera))
                } else {
                    showToast(getString(R.string.permission_denied_cannot_access_and_camera))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            showMessage(getString(R.string.you_need_to_allow_access_to_both_the_permissions),
                                DialogInterface.OnClickListener { _, _ ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermission()
                                    }
                                })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(message: String, okListener: DialogInterface.OnClickListener) =
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), okListener)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()

    private fun AppCompatActivity.showToast(text: CharSequence) {
        Toast.makeText(
            applicationContext,
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}
