package com.tuyennm.mobileauthenticator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuyennm.mobileauthenticator.models.Account

class AddedAccountsAdapter : RecyclerView.Adapter<AddedAccountsAdapter.AddedAccountViewHolder>() {

    private val listItem = mutableListOf<Account>()

    fun addItems(items: List<Account>) {
        listItem.clear()
        listItem.addAll(items)

        notifyDataSetChanged()
    }

    fun addItem(item: Account) {
        listItem.add(item)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddedAccountViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return AddedAccountViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: AddedAccountViewHolder, position: Int) {
        with(listItem[position]) {
            holder.txtTOTP.text = totp
            holder.txtAccountName.text = accountName
        }
    }

    inner class AddedAccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTOTP: TextView by lazy { view.findViewById(R.id.txtTOTP) as TextView }
        val txtAccountName: TextView by lazy { view.findViewById(R.id.txtAccountName) as TextView }
    }
}