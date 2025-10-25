package com.lanrhyme.shardlauncher.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lanrhyme.shardlauncher.model.Account

class AccountRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAccounts(): List<Account> {
        val json = prefs.getString(KEY_ACCOUNTS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Account>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveAccounts(accounts: List<Account>) {
        val json = gson.toJson(accounts)
        prefs.edit().putString(KEY_ACCOUNTS, json).apply()
    }

    fun getSelectedAccount(): Account? {
        val json = prefs.getString(KEY_SELECTED_ACCOUNT, null)
        return if (json != null) {
            gson.fromJson(json, Account::class.java)
        } else {
            null
        }
    }

    fun saveSelectedAccount(account: Account?) {
        val json = gson.toJson(account)
        prefs.edit().putString(KEY_SELECTED_ACCOUNT, json).apply()
    }

    companion object {
        private const val PREFS_NAME = "account_prefs"
        private const val KEY_ACCOUNTS = "accounts"
        private const val KEY_SELECTED_ACCOUNT = "selected_account"
    }
}