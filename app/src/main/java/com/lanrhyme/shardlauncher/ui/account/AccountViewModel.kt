package com.lanrhyme.shardlauncher.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanrhyme.shardlauncher.model.Account
import com.lanrhyme.shardlauncher.model.AccountType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount: StateFlow<Account?> = _selectedAccount

    init {
        // Sample data for demonstration
        viewModelScope.launch {
            val sampleAccounts = listOf(
                Account("1", "LongPlayerName_12345", AccountType.ONLINE, "2 hours ago", "https://crafatar.com/avatars/b8a7c7c8-f6e-4b9b-8a7c-7c8f6e6b9b8a"),
                Account("2", "Steve", AccountType.OFFLINE, "1 day ago", "https://crafatar.com/avatars/8667ba71-b85a-4004-af54-457a9734eed7"),
                Account("3", "Player3", AccountType.ONLINE, "A very very long time ago", "https://crafatar.com/avatars/c8f6e6b9-b8a7-4c7c-8f6e-6b9b8a7c7c8f"),
                Account("4", "Alex", AccountType.ONLINE, "5 hours ago", "https://crafatar.com/avatars/61699b2e-d327-421c-9f4c-c63a442838e6"),
                Account("5", "Herobrine", AccountType.OFFLINE, "1 year ago", "https://crafatar.com/avatars/f868c14a-5282-4598-a83d-3a52140e6376"),
                Account("6", "Notch", AccountType.ONLINE, "10 years ago", "https://crafatar.com/avatars/069a79f4-44e9-4726-a5be-fca90e38aaf5")
            )
            _accounts.value = sampleAccounts
            if (sampleAccounts.isNotEmpty()) {
                _selectedAccount.value = sampleAccounts[0]
            }
        }
    }

    fun selectAccount(account: Account) {
        _selectedAccount.value = account
    }
}