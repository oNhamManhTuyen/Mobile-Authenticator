package com.tuyennm.mobileauthenticator.models

class Account(
    val accountName: String,
    var secretKey: String,
    var totp: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Account) return false

        return other.accountName == accountName
    }

    override fun hashCode(): Int {
        return accountName.hashCode()
    }
}