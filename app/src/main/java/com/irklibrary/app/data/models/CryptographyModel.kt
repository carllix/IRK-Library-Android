package com.irklibrary.app.data.models

data class CaesarCipherRequest(
    val text: String,
    val shift: Int,
    val isEncrypt: Boolean
)

data class CaesarCipherResult(
    val originalText: String,
    val resultText: String,
    val shift: Int,
    val isEncrypt: Boolean,
    val steps: String
)

data class RsaRequest(
    val text: String,
    val p: Int,
    val q: Int,
    val isEncrypt: Boolean,
    val publicKey: RsaKey? = null,
    val privateKey: RsaKey? = null
)

data class RsaKey(
    val key: Int,
    val n: Int
)

data class RsaResult(
    val originalText: String,
    val resultText: String,
    val publicKey: RsaKey,
    val privateKey: RsaKey,
    val p: Int,
    val q: Int,
    val isEncrypt: Boolean,
    val keyGenerationSteps: String,
    val encryptionSteps: String
)

data class RsaStepSection(
    val title: String,
    val content: String,
    val isExpanded: Boolean = false
)

data class CryptographyState(
    val inputText: String = "",
    val shiftValue: String = "1",
    val result: String = "",
    val steps: String = "",
    val isStepsVisible: Boolean = false,
    val showStepsButton: Boolean = false,
    val showCopyButton: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val currentTab: CryptographyTab = CryptographyTab.CAESAR_CIPHER,
    val copyMessage: String = "",

    val pValue: String = "17",
    val qValue: String = "19",
    val publicKey: RsaKey? = null,
    val privateKey: RsaKey? = null,
    val rsaStepSections: List<RsaStepSection> = emptyList(),
    val showGenerateKeys: Boolean = true
)

enum class CryptographyTab {
    CAESAR_CIPHER,
    RSA
}