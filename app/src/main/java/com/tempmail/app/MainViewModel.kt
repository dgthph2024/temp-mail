package com.tempmail.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tempmail.app.data.api.RetrofitClient
import com.tempmail.app.data.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppState(
    val email: String = "",
    val messages: List<Message> = emptyList(),
    val selectedMessage: Message? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val copiedText: String? = null
)

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val api = RetrofitClient.instance

    fun createEmail() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, messages = emptyList())
            try {
                val response = api.createEmail()
                if (response.isSuccessful) {
                    val body = response.body()
                    _state.value = _state.value.copy(
                        email = body?.email ?: "",
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Lỗi tạo email: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.localizedMessage}"
                )
            }
        }
    }

    fun refreshMessages() {
        val currentEmail = _state.value.email
        if (currentEmail.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = api.getMessages(currentEmail)
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        messages = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Lỗi tải thư: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi kết nối: ${e.localizedMessage}"
                )
            }
        }
    }

    fun selectMessage(message: Message) {
        _state.value = _state.value.copy(selectedMessage = message)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedMessage = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun setCopied(text: String) {
        _state.value = _state.value.copy(copiedText = text)
    }

    fun clearCopied() {
        _state.value = _state.value.copy(copiedText = null)
    }
}
