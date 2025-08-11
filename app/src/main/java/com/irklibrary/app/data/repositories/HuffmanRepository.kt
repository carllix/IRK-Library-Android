package com.irklibrary.app.data.repositories

import com.irklibrary.app.data.models.*
import java.util.*

class HuffmanRepository {

    fun buildHuffmanTree(text: String): HuffmanResult {
        val frequencies = calculateFrequencies(text)

        val (root, steps) = buildTreeWithSteps(frequencies)

        val codes = generateCodes(root)

        val encodedText = encodeText(text, codes)

        return HuffmanResult(
            originalText = text,
            characterFrequencies = frequencies,
            huffmanCodes = codes,
            huffmanTree = root,
            constructionSteps = steps,
            encodedText = encodedText
        )
    }

    private fun calculateFrequencies(text: String): List<CharacterFrequency> {
        val frequencyMap = mutableMapOf<Char, Int>()
        text.forEach { char ->
            frequencyMap[char] = frequencyMap.getOrDefault(char, 0) + 1
        }

        return frequencyMap.map { (char, freq) ->
            CharacterFrequency(char, freq)
        }.sortedBy { it.frequency }
    }

    private fun buildTreeWithSteps(frequencies: List<CharacterFrequency>): Pair<HuffmanNode, MutableList<HuffmanTreeStep>> {
        val steps = mutableListOf<HuffmanTreeStep>()
        val priorityQueue = PriorityQueue<HuffmanNode>()

        frequencies.forEach { freq ->
            val character = if (freq.character == ' ') '_' else freq.character
            priorityQueue.offer(HuffmanNode(character, freq.frequency, nodeId = character.toString()))
        }

        var stepCounter = 1

        val initialTrees = priorityQueue.toList().sortedBy { it.frequency }
        steps.add(
            HuffmanTreeStep(
                stepNumber = stepCounter++,
                description = "Urutkan simbol berdasarkan frekuensi dari kecil ke besar",
                availableNodes = initialTrees,
                selectedNodes = emptyList(),
                newNode = null,
                currentTree = null,
                currentTrees = initialTrees
            )
        )

        // Buat Pohon Huffman
        while (priorityQueue.size > 1) {
            val left = priorityQueue.poll()!!
            val right = priorityQueue.poll()!!

            val combinedFreq = left.frequency + right.frequency
            val parentNodeId = "${left.nodeId}${right.nodeId}"

            val parentNode = HuffmanNode(
                character = null,
                frequency = combinedFreq,
                left = left,
                right = right,
                nodeId = parentNodeId
            )

            priorityQueue.offer(parentNode)

            val currentTrees = priorityQueue.toList().sortedBy { it.frequency }

            steps.add(
                HuffmanTreeStep(
                    stepNumber = stepCounter++,
                    description = "Gabungkan ${left.nodeId} (${left.frequency}) dan ${right.nodeId} (${right.frequency}) " +
                            "menjadi ${parentNode.nodeId} (${parentNode.frequency}), tempatkan simbol baru ${parentNode.nodeId} di dalam urutan terurut",
                    availableNodes = currentTrees,
                    selectedNodes = listOf(left, right),
                    newNode = parentNode,
                    currentTree = parentNode,
                    currentTrees = currentTrees
                )
            )
        }

        val root = priorityQueue.poll()!!

        steps.add(
            HuffmanTreeStep(
                stepNumber = stepCounter,
                description = "Simbol sudah habis. Stop. Pohon Huffman sudah terbentuk. Kemudian, sisi-sisi kiri di dalam pohon Huffman diberi label 0, sisi-sisi kanan diberi label 1",
                availableNodes = emptyList(),
                selectedNodes = emptyList(),
                newNode = null,
                currentTree = root,
                currentTrees = listOf(root)
            )
        )

        return Pair(root, steps)
    }

    private fun generateCodes(root: HuffmanNode): List<HuffmanCode> {
        val codes = mutableListOf<HuffmanCode>()

        if (root.isLeaf()) {
            codes.add(HuffmanCode(root.character!!, root.frequency, "0"))
        } else {
            generateCodesRecursive(root, "", codes)
        }

        return codes.sortedWith(
            compareByDescending<HuffmanCode> { it.frequency }
                .thenBy { it.character }
        )
    }

    private fun generateCodesRecursive(node: HuffmanNode, code: String, codes: MutableList<HuffmanCode>) {
        if (node.isLeaf()) {
            codes.add(HuffmanCode(node.character!!, node.frequency, code))
            return
        }

        node.left?.let { generateCodesRecursive(it, code + "0", codes) }
        node.right?.let { generateCodesRecursive(it, code + "1", codes) }
    }

    private fun encodeText(text: String, codes: List<HuffmanCode>): String {
        val codeMap = codes.associateBy { it.character }
        return text.map { char ->
            codeMap[char]?.code ?: ""
        }.joinToString("")
    }
}