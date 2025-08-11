package com.irklibrary.app.ui.page3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irklibrary.app.data.models.HuffmanResult
import com.irklibrary.app.data.repositories.HuffmanRepository
import kotlinx.coroutines.launch

class HuffmanViewModel : ViewModel() {

    private val repository = HuffmanRepository()

    private val _huffmanResult = MutableLiveData<HuffmanResult?>()
    val huffmanResult: LiveData<HuffmanResult?> = _huffmanResult

    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> = _isProcessing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentAnimationStep = MutableLiveData<Int>()

    fun processText(inputText: String) {
        if (inputText.isBlank()) {
            _errorMessage.value = "Input text tidak boleh kosong!"
            return
        }

        if (inputText.length > 1000) {
            _errorMessage.value = "Input text terlalu panjang! Maksimal 1000 karakter."
            return
        }

        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _errorMessage.value = null

                kotlinx.coroutines.delay(500)

                val result = repository.buildHuffmanTree(inputText.trim())
                _huffmanResult.value = result

            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun clearResult() {
        _huffmanResult.value = null
        _errorMessage.value = null
        _currentAnimationStep.value = 0
    }

    fun validateInput(text: String): String? {
        return when {
            text.isBlank() -> "Input tidak boleh kosong"
            text.length < 1 -> "Input minimal 1 karakter"
            text.length > 1000 -> "Input maksimal 1000 karakter"
            text.all { it.isWhitespace() } -> "Input tidak boleh hanya spasi"
            else -> null
        }
    }
}