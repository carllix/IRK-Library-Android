package com.irklibrary.app.ui.page2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irklibrary.app.data.models.CaesarCipherRequest
import com.irklibrary.app.data.models.CryptographyState
import com.irklibrary.app.data.models.CryptographyTab
import com.irklibrary.app.data.models.RsaRequest
import com.irklibrary.app.data.repositories.CryptographyRepository

class CryptographyViewModel : ViewModel() {

    private val repository = CryptographyRepository()

    private val _state = MutableLiveData<CryptographyState>()
    val state: LiveData<CryptographyState> = _state

    init {
        _state.value = CryptographyState(
            pValue = "17",  // Updated default values for ASCII support
            qValue = "19"   // 17 * 19 = 323 which is > 256
        )
    }

    fun updateInputText(text: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            inputText = text,
            errorMessage = ""
        )
    }

    fun updateShiftValue(shift: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            shiftValue = shift,
            errorMessage = ""
        )
    }

    fun updatePValue(p: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            pValue = p,
            errorMessage = ""
        )
    }

    fun updateQValue(q: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            qValue = q,
            errorMessage = ""
        )
    }

    fun switchTab(tab: CryptographyTab) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            currentTab = tab,
            result = "",
            steps = "",
            isStepsVisible = false,
            showStepsButton = false,
            showCopyButton = false,
            errorMessage = "",
            rsaStepSections = emptyList(),
            showGenerateKeys = tab == CryptographyTab.RSA
        )
    }

    fun performEncryption() {
        when (_state.value?.currentTab) {
            CryptographyTab.CAESAR_CIPHER -> performCaesarCipher(isEncrypt = true)
            CryptographyTab.RSA -> performRsaOperation(isEncrypt = true)
            else -> {}
        }
    }

    fun performDecryption() {
        when (_state.value?.currentTab) {
            CryptographyTab.CAESAR_CIPHER -> performCaesarCipher(isEncrypt = false)
            CryptographyTab.RSA -> performRsaOperation(isEncrypt = false)
            else -> {}
        }
    }

    fun generateRsaKeys() {
        val currentState = _state.value ?: return

        _state.value = currentState.copy(isLoading = true, errorMessage = "")

        val errorMessage = repository.validateRsaInput("A", currentState.pValue, currentState.qValue)

        if (errorMessage != null) {
            _state.value = currentState.copy(
                isLoading = false,
                errorMessage = errorMessage,
                result = errorMessage
            )
            return
        }

        try {
            val p = currentState.pValue.toInt()
            val q = currentState.qValue.toInt()

            val request = RsaRequest(
                text = "A", // Dummy text for key generation
                p = p,
                q = q,
                isEncrypt = true
            )

            val result = repository.processRsa(request)
            val stepSections = repository.generateRsaStepSections(result)

            _state.value = currentState.copy(
                isLoading = false,
                publicKey = result.publicKey,
                privateKey = result.privateKey,
                rsaStepSections = stepSections,
                showGenerateKeys = false,
                showStepsButton = true,
                errorMessage = "",
                result = "Kunci berhasil dibuat!\nPublik (n, e): (${result.publicKey.n}, ${result.publicKey.key})\nPrivat (n, d): (${result.privateKey.n}, ${result.privateKey.key})"
            )

        } catch (e: Exception) {
            _state.value = currentState.copy(
                isLoading = false,
                errorMessage = "Terjadi kesalahan: ${e.message}",
                result = "Terjadi kesalahan: ${e.message}"
            )
        }
    }

    private fun performRsaOperation(isEncrypt: Boolean) {
        val currentState = _state.value ?: return

        if (currentState.publicKey == null || currentState.privateKey == null) {
            _state.value = currentState.copy(
                errorMessage = "Silakan generate kunci terlebih dahulu!",
                result = "Silakan generate kunci terlebih dahulu!"
            )
            return
        }

        _state.value = currentState.copy(isLoading = true, errorMessage = "")

        val errorMessage = repository.validateRsaInput(
            currentState.inputText,
            currentState.pValue,
            currentState.qValue
        )

        if (errorMessage != null) {
            _state.value = currentState.copy(
                isLoading = false,
                errorMessage = errorMessage,
                result = errorMessage
            )
            return
        }

        try {
            val p = currentState.pValue.toInt()
            val q = currentState.qValue.toInt()

            val request = RsaRequest(
                text = currentState.inputText,
                p = p,
                q = q,
                isEncrypt = isEncrypt,
                publicKey = currentState.publicKey,
                privateKey = currentState.privateKey
            )

            val result = repository.processRsa(request)
            val stepSections = repository.generateRsaStepSections(result)

            _state.value = currentState.copy(
                isLoading = false,
                result = result.resultText,
                rsaStepSections = stepSections,
                showStepsButton = true,
                showCopyButton = result.resultText.isNotEmpty(),
                isStepsVisible = false,
                errorMessage = ""
            )

        } catch (e: Exception) {
            _state.value = currentState.copy(
                isLoading = false,
                errorMessage = "Terjadi kesalahan: ${e.message}",
                result = "Terjadi kesalahan: ${e.message}"
            )
        }
    }

    private fun performCaesarCipher(isEncrypt: Boolean) {
        val currentState = _state.value ?: return

        // Show loading
        _state.value = currentState.copy(isLoading = true, errorMessage = "")

        // Validate input
        val errorMessage = repository.validateInput(
            currentState.inputText,
            currentState.shiftValue
        )

        if (errorMessage != null) {
            _state.value = currentState.copy(
                isLoading = false,
                errorMessage = errorMessage,
                result = errorMessage
            )
            return
        }

        try {
            val shift = currentState.shiftValue.toInt()
            val request = CaesarCipherRequest(
                text = currentState.inputText,
                shift = shift,
                isEncrypt = isEncrypt
            )

            val result = repository.processCaesarCipher(request)

            _state.value = currentState.copy(
                isLoading = false,
                result = result.resultText,
                steps = result.steps,
                showStepsButton = true,
                showCopyButton = result.resultText.isNotEmpty(),
                isStepsVisible = false,
                errorMessage = ""
            )

        } catch (e: Exception) {
            _state.value = currentState.copy(
                isLoading = false,
                errorMessage = "Terjadi kesalahan: ${e.message}",
                result = "Terjadi kesalahan: ${e.message}"
            )
        }
    }

    fun toggleStepsVisibility() {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            isStepsVisible = !currentState.isStepsVisible
        )
    }

    fun toggleRsaSection(index: Int) {
        val currentState = _state.value ?: return
        val updatedSections = currentState.rsaStepSections.mapIndexed { i, section ->
            if (i == index) {
                section.copy(isExpanded = !section.isExpanded)
            } else {
                section
            }
        }
        _state.value = currentState.copy(rsaStepSections = updatedSections)
    }

    fun clearCaesarCipher() {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            inputText = "",
            shiftValue = "3",
            result = "",
            steps = "",
            isStepsVisible = false,
            showStepsButton = false,
            showCopyButton = false,
            errorMessage = "",
            copyMessage = ""
        )
    }

    fun clearRsaCipher() {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            inputText = "",
            pValue = "17",  // Updated default values
            qValue = "19",  // Updated default values
            result = "",
            steps = "",
            isStepsVisible = false,
            showStepsButton = false,
            showCopyButton = false,
            errorMessage = "",
            copyMessage = "",
            publicKey = null,
            privateKey = null,
            rsaStepSections = emptyList(),
            showGenerateKeys = true
        )
    }

    fun copyResult(): String {
        return _state.value?.result ?: ""
    }

    fun copySteps(): String {
        return when (_state.value?.currentTab) {
            CryptographyTab.CAESAR_CIPHER -> _state.value?.steps ?: ""
            CryptographyTab.RSA -> {
                val sections = _state.value?.rsaStepSections ?: emptyList()
                sections.joinToString("\n\n") { "${it.title}\n${it.content}" }
            }
            else -> ""
        }
    }
}