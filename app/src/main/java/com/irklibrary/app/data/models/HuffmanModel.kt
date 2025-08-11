package com.irklibrary.app.data.models

data class HuffmanNode(
    val character: Char? = null,
    val frequency: Int,
    val left: HuffmanNode? = null,
    val right: HuffmanNode? = null,
    val nodeId: String = ""
) : Comparable<HuffmanNode> {
    override fun compareTo(other: HuffmanNode): Int {
        return this.frequency.compareTo(other.frequency)
    }

    fun isLeaf(): Boolean = left == null && right == null
}

data class CharacterFrequency(
    val character: Char,
    val frequency: Int
)

data class HuffmanCode(
    val character: Char,
    val frequency: Int,
    val code: String
)

data class HuffmanTreeStep(
    val stepNumber: Int,
    val description: String,
    val availableNodes: List<HuffmanNode>,
    val selectedNodes: List<HuffmanNode>,
    val newNode: HuffmanNode?,
    val currentTree: HuffmanNode?,
    val currentTrees: List<HuffmanNode> = emptyList()
)

data class DecodingStep(
    val stepNumber: Int,
    val currentBits: String,
    val matchedCode: String,
    val decodedCharacter: Char,
    val remainingBits: String,
    val currentDecoded: String,
    val description: String
)

data class CompressionInfo(
    val originalSizeBits: Int,
    val compressedSizeBits: Int,
    val compressionRatio: Double,
    val spaceSavedBits: Int,
    val spaceSavedPercentage: Double
)

data class HuffmanResult(
    val originalText: String,
    val characterFrequencies: List<CharacterFrequency>,
    val huffmanCodes: List<HuffmanCode>,
    val huffmanTree: HuffmanNode,
    val constructionSteps: List<HuffmanTreeStep>,
    val encodedText: String,
    val decodingSteps: List<DecodingStep>,
    val compressionInfo: CompressionInfo
)