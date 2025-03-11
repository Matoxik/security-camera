package com.macieandrz.securitycamera.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class NotificationViewModel(app: Application) : AndroidViewModel(app) {
    private val sharedPrefs = app.getSharedPreferences("notification_prefs",
        Context.MODE_PRIVATE)

    private val _recipientEmail = MutableStateFlow("")
    val recipientEmail: StateFlow<String> = _recipientEmail

    init {
        _recipientEmail.value = getRecipientEmail()
    }

    fun saveRecipientEmail(email: String) {
        sharedPrefs.edit().putString("recipient_email", email).apply()
        _recipientEmail.value = email
    }

    fun getRecipientEmail(): String {
        return sharedPrefs.getString("recipient_email", "") ?: ""
    }
}
