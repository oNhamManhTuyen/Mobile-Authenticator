package com.tuyennm.mobileauthenticator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tuyennm.mobileauthenticator.models.Account
import kotlinx.android.synthetic.main.fragment_added_account.*

class AddedAccountFragment : Fragment() {

    companion object {
        const val TAG = "AddedAccountFragment"
    }

    private val adapter = AddedAccountsAdapter()
    private val broadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constant.ACTION_RECEIVE_SECRET_KEY) {
                val accountName = intent.getStringExtra(Constant.EXTRA_ACCOUNT)
                val secretKey = intent.getStringExtra(Constant.EXTRA_SECRET_KEY)

                if (!accountName.isNullOrEmpty() && !secretKey.isNullOrEmpty()) {
                    adapter.addItem(Account(accountName, secretKey))
                    showToast("accountName is Added")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_added_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvAddedAccounts.adapter = adapter
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcast, IntentFilter(Constant.ACTION_RECEIVE_SECRET_KEY))
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcast)
    }

    private fun Fragment.showToast(text: CharSequence) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}