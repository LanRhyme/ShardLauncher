package com.lanrhyme.shardlauncher.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanrhyme.shardlauncher.api.ApiClient
import com.lanrhyme.shardlauncher.data.AccountRepository
import com.lanrhyme.shardlauncher.data.AuthRepository
import com.lanrhyme.shardlauncher.model.Account
import com.lanrhyme.shardlauncher.model.AccountType
import com.lanrhyme.shardlauncher.model.auth.DeviceCodeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed class MicrosoftLoginState {
    object Idle : MicrosoftLoginState()
    data class InProgress(val deviceCodeResponse: DeviceCodeResponse) : MicrosoftLoginState()
    object Success : MicrosoftLoginState()
    data class Error(val message: String) : MicrosoftLoginState()
}

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    private val authRepository = AuthRepository(ApiClient.microsoftAuthService, ApiClient.minecraftAuthService, ApiClient.mojangApiService, ApiClient.rmsApiService)

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount: StateFlow<Account?> = _selectedAccount

    private val _microsoftLoginState = MutableStateFlow<MicrosoftLoginState>(MicrosoftLoginState.Idle)
    val microsoftLoginState = _microsoftLoginState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            val savedAccounts = repository.getAccounts()
            _accounts.value = savedAccounts
            _selectedAccount.value = repository.getSelectedAccount() ?: savedAccounts.firstOrNull()
        }
    }

    fun loginWithMicrosoft() {
        viewModelScope.launch {
            try {
                val deviceCodeResponse = authRepository.getDeviceCode()
                _microsoftLoginState.value = MicrosoftLoginState.InProgress(deviceCodeResponse)

                val authTokenResponse = authRepository.pollForToken(deviceCodeResponse)
                val minecraftAuthResponse = authRepository.getMinecraftAuth(authTokenResponse.accessToken)
                val minecraftProfile = authRepository.getMinecraftProfile(minecraftAuthResponse.accessToken)

                val newAccount = Account(
                    id = minecraftProfile.id,
                    username = minecraftProfile.name,
                    accountType = AccountType.ONLINE,
                    lastPlayed = "",
                    skinUrl = minecraftProfile.skins.first().url
                )

                addAccount(newAccount)
                selectAccount(newAccount)

                _microsoftLoginState.value = MicrosoftLoginState.Success
            } catch (e: Exception) {
                _microsoftLoginState.value = MicrosoftLoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetMicrosoftLoginState() {
        _microsoftLoginState.value = MicrosoftLoginState.Idle
    }

    fun selectAccount(account: Account) {
        _selectedAccount.value = account
        repository.saveSelectedAccount(account)
    }

    private fun addAccount(account: Account) {
        _accounts.update { currentAccounts ->
            val newAccounts = currentAccounts + account
            repository.saveAccounts(newAccounts)
            newAccounts
        }
    }

    fun addOfflineAccount(username: String) {
        viewModelScope.launch {
            val skinUrl = authRepository.getOfflineSkinUrl(username)
            val newAccount = Account(
                id = UUID.randomUUID().toString(),
                username = username,
                accountType = AccountType.OFFLINE,
                lastPlayed = "",
                skinUrl = skinUrl
            )
            addAccount(newAccount)
        }
    }

    fun deleteAccount(account: Account) {
        _accounts.update { currentAccounts ->
            val newAccounts = currentAccounts.filter { it.id != account.id }
            repository.saveAccounts(newAccounts)
            if (_selectedAccount.value == account) {
                val newSelectedAccount = newAccounts.firstOrNull()
                _selectedAccount.value = newSelectedAccount
                repository.saveSelectedAccount(newSelectedAccount)
            }
            newAccounts
        }
    }

    fun updateOfflineAccount(account: Account, newUsername: String) {
        viewModelScope.launch {
            val skinUrl = authRepository.getOfflineSkinUrl(newUsername)
            val updatedAccount = account.copy(username = newUsername, skinUrl = skinUrl)
            updateAccount(updatedAccount)
        }
    }

    private fun updateAccount(account: Account) {
        _accounts.update { currentAccounts ->
            val newAccounts = currentAccounts.map {
                if (it.id == account.id) account else it
            }
            repository.saveAccounts(newAccounts)
            if (_selectedAccount.value?.id == account.id) {
                _selectedAccount.value = account
                repository.saveSelectedAccount(account)
            }
            newAccounts
        }
    }
}