package com.irklibrary.app.data.repositories

import com.irklibrary.app.data.models.*
import java.util.*

class HuffmanRepository {

    fun buildHuffmanTree(text: String): HuffmanResult {
        val frequencies = calculateFrequencies(text)

        // 2. Build Huffman tree with step tracking
        val (root, steps) = buildTreeWithSteps(frequencies)

        // 3. Generate Huffman codes
        val codes = generateCodes(root)

        // 4. Encode text
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
            priorityQueue.offer(HuffmanNode(freq.character, freq.frequency, nodeId = freq.character.toString()))
        }

        var stepCounter = 1

        // Urutkan frekuensi dari kecil ke besar
        steps.add(
            HuffmanTreeStep(
                stepNumber = stepCounter++,
                description = "Urutkan simbol berdasarkan frekuensi dari kecil ke besar",
                availableNodes = priorityQueue.toList(),
                selectedNodes = emptyList(),
                newNode = null,
                currentTree = null
            )
        )

        // Buat Pohon Huffman
        while (priorityQueue.size > 1) {
            val left = priorityQueue.poll()!!
            val right = priorityQueue.poll()!!

            val combinedFreq = (left.frequency) + (right.frequency)
            val parentNodeId = if (left.character != null && right.character != null) {
                "${left.character}${right.character}"
            } else if (left.character != null) {
                "${left.character}${right.nodeId}"
            } else if (right.character != null) {
                "${left.nodeId}${right.character}"
            } else {
                "${left.nodeId}${right.nodeId}"
            }

            val parentNode = HuffmanNode(
                character = null,
                frequency = combinedFreq,
                left = left,
                right = right,
                nodeId = parentNodeId
            )

            steps.add(
                HuffmanTreeStep(
                    stepNumber = stepCounter++,
                    description = "Gabungkan ${left.nodeId} (${left.frequency}) dan ${right.nodeId} (${right.frequency}) " +
                            "menjadi ${parentNode.nodeId} (${parentNode.frequency}), tempatkan simbol baru ${parentNode.nodeId} di dalam urutan terurut",
                    availableNodes = priorityQueue.toList(),
                    selectedNodes = listOf(left, right),
                    newNode = parentNode,
                    currentTree = parentNode
                )
            )

            priorityQueue.offer(parentNode)
        }

        val root = priorityQueue.poll()!!

        // Beri label
        steps.add(
            HuffmanTreeStep(
                stepNumber = stepCounter,
                description = "Simbol sudah habis. Stop. Pohon Huffman sudah terbentuk. Kemudian, sisi-sisi kiri di dalam pohon Huffam diberi label 0, sisi-sisi kanan diberi label 1 ",
                availableNodes = emptyList(),
                selectedNodes = emptyList(),
                newNode = null,
                currentTree = root
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