package com.irklibrary.app.data.repositories

import com.irklibrary.app.data.models.CaesarCipherRequest
import com.irklibrary.app.data.models.CaesarCipherResult
import com.irklibrary.app.data.models.RsaRequest
import com.irklibrary.app.data.models.RsaResult
import com.irklibrary.app.data.models.RsaKey
import com.irklibrary.app.data.models.RsaStepSection

class CryptographyRepository {

    fun processCaesarCipher(request: CaesarCipherRequest): CaesarCipherResult {
        val actualShift = if (request.isEncrypt) request.shift else -request.shift
        val resultText = caesarCipher(request.text, actualShift)
        val steps = generateSteps(request.text, actualShift, request.isEncrypt)

        return CaesarCipherResult(
            originalText = request.text,
            resultText = resultText,
            shift = request.shift,
            isEncrypt = request.isEncrypt,
            steps = steps
        )
    }

    fun processRsa(request: RsaRequest): RsaResult {
        val n = request.p * request.q
        val phi = (request.p - 1) * (request.q - 1)

        // Generate keys if not provided
        val publicKey = request.publicKey ?: generatePublicKey(phi, n)
        val privateKey = request.privateKey ?: generatePrivateKey(publicKey.key, phi, n)

        val resultText = if (request.isEncrypt) {
            rsaEncrypt(request.text, publicKey)
        } else {
            rsaDecrypt(request.text, privateKey)
        }

        val keyGenerationSteps = generateKeySteps(request.p, request.q, n, phi, publicKey, privateKey)
        val encryptionSteps = if (request.isEncrypt) {
            generateEncryptionSteps(request.text, publicKey)
        } else {
            generateDecryptionSteps(request.text, privateKey)
        }

        return RsaResult(
            originalText = request.text,
            resultText = resultText,
            publicKey = publicKey,
            privateKey = privateKey,
            p = request.p,
            q = request.q,
            isEncrypt = request.isEncrypt,
            keyGenerationSteps = keyGenerationSteps,
            encryptionSteps = encryptionSteps
        )
    }

    fun generateRsaStepSections(result: RsaResult): List<RsaStepSection> {
        return listOf(
            RsaStepSection(
                title = "1. Pembuatan Kunci Publik dan Privat",
                content = result.keyGenerationSteps
            ),
            RsaStepSection(
                title = if (result.isEncrypt) "2. Proses Enkripsi" else "2. Proses Dekripsi",
                content = result.encryptionSteps
            )
        )
    }

    private fun generatePublicKey(phi: Int, n: Int): RsaKey {
        // Find e such that gcd(e, phi) = 1 and 1 < e < phi
        var e = 3
        while (e < phi) {
            if (gcd(e, phi) == 1) {
                return RsaKey(e, n)
            }
            e += 2
        }
        return RsaKey(65537, n) // Default RSA exponent
    }

    private fun generatePrivateKey(e: Int, phi: Int, n: Int): RsaKey {
        val d = modInverse(e, phi)
        return RsaKey(d, n)
    }

    private fun generateKeySteps(p: Int, q: Int, n: Int, phi: Int, publicKey: RsaKey, privateKey: RsaKey): String {
        val steps = StringBuilder()

        steps.append("Langkah 1: Pilih dua bilangan prima\n")
        steps.append("p = $p\n")
        steps.append("q = $q\n\n")

        steps.append("Langkah 2: Hitung n\n")
        steps.append("n = p × q = $p × $q = $n\n\n")

        steps.append("Langkah 3: Hitung m\n")
        steps.append("m = (p-1) × (q-1)\n")
        steps.append("m = (${p-1}) × (${q-1}) = $phi\n\n")

        steps.append("Langkah 4: Pilih e (kunci publik)\n")
        steps.append("e = ${publicKey.key}\n")
        steps.append("gcd(e, m) = gcd(${publicKey.key}, $phi) = 1\n\n")

        steps.append("Langkah 5: Hitung d (kunci privat)\n")
        steps.append("ed ≡ 1 (mod m)\n")
        steps.append("${publicKey.key} × d ≡ 1 (mod $phi)\n")
        steps.append("d = ${privateKey.key}\n\n")

        steps.append("Verifikasi: ${publicKey.key} × ${privateKey.key} mod $phi = ${(publicKey.key * privateKey.key) % phi}\n\n")

        steps.append("Hasil:\n")
        steps.append("Kunci Publik (n, e): ($n, ${publicKey.key})\n")
        steps.append("Kunci Privat (n, d): ($n, ${privateKey.key})\n\n")

        return steps.toString()
    }

    private fun generateEncryptionSteps(text: String, publicKey: RsaKey): String {
        val steps = StringBuilder()
        val e = publicKey.key
        val n = publicKey.n

        steps.append("Formula Enkripsi: c ≡ p^e (mod n)\n")
        steps.append("Dengan e = $e dan n = $n\n\n")

        steps.append("Tahap 1: Konversi karakter ke nilai ASCII (0-255)\n")
        text.forEachIndexed { index, char ->
            val ascii = char.code
            steps.append("'$char' = ASCII $ascii\n")
        }

        steps.append("\nTahap 2: Enkripsi RSA\n")
        text.forEachIndexed { index, char ->
            val p = char.code
            val c = modPow(p.toLong(), e.toLong(), n.toLong()).toInt()
            steps.append("p${index + 1} = $p → c = $p^$e mod $n = $c\n")
        }

        steps.append("\nTahap 3: Format hasil\n")
        val results = mutableListOf<String>()
        text.forEachIndexed { index, char ->
            val p = char.code
            val c = modPow(p.toLong(), e.toLong(), n.toLong()).toInt()
            if (c in 0..255) {
                val resultChar = c.toChar()
                if (resultChar.isLetterOrDigit() || resultChar.isWhitespace() || resultChar in "!@#$%^&*(),.?\":{}|<>") {
                    steps.append("c${index + 1} = $c → dalam rentang ASCII → '$resultChar'\n")
                    results.add(resultChar.toString())
                } else {
                    steps.append("c${index + 1} = $c → karakter non-printable → [$c]\n")
                    results.add("[$c]")
                }
            } else {
                steps.append("c${index + 1} = $c → di luar rentang ASCII → [$c]\n")
                results.add("[$c]")
            }
        }

        return steps.toString()
    }

    private fun generateDecryptionSteps(inputText: String, privateKey: RsaKey): String {
        val steps = StringBuilder()
        val d = privateKey.key
        val n = privateKey.n

        steps.append("Formula Dekripsi: p ≡ c^d (mod n)\n")
        steps.append("Dengan d = $d dan n = $n\n\n")

        steps.append("Tahap 1: Parsing input\n")
        val inputs = parseDecryptInput(inputText)
        inputs.forEachIndexed { index, input ->
            when (input) {
                is DecryptInput.Character -> {
                    steps.append("Input ${index + 1}: '$input.char' = ASCII ${input.value}\n")
                }
                is DecryptInput.Number -> {
                    steps.append("Input ${index + 1}: [$input.value]\n")
                }
            }
        }

        steps.append("\nTahap 2: Dekripsi RSA\n")
        inputs.forEachIndexed { index, input ->
            val c = input.value
            val p = modPow(c.toLong(), d.toLong(), n.toLong()).toInt()
            steps.append("c${index + 1} = $c → p = $c^$d mod $n = $p\n")
        }

        steps.append("\nTahap 3: Format hasil\n")
        inputs.forEachIndexed { index, input ->
            val c = input.value
            val p = modPow(c.toLong(), d.toLong(), n.toLong()).toInt()
            if (p in 0..255) {
                val resultChar = p.toChar()
                steps.append("p${index + 1} = $p → ASCII valid → '$resultChar'\n")
            } else {
                steps.append("p${index + 1} = $p → di luar rentang ASCII → [$p]\n")
            }
        }

        return steps.toString()
    }

    private fun rsaEncrypt(text: String, publicKey: RsaKey): String {
        val result = mutableListOf<String>()

        text.forEach { char ->
            val p = char.code
            val c = modPow(p.toLong(), publicKey.key.toLong(), publicKey.n.toLong()).toInt()

            if (c in 0..255) {
                val resultChar = c.toChar()
                if (resultChar.isLetterOrDigit() || resultChar.isWhitespace() || resultChar in "!@#$%^&*(),.?\":{}|<>") {
                    result.add(resultChar.toString())
                } else {
                    result.add("[$c]")
                }
            } else {
                result.add("[$c]")
            }
        }

        // If result contains bracketed numbers, join with spaces
        val hasNonPrintable = result.any { it.startsWith("[") && it.endsWith("]") }
        return if (hasNonPrintable) {
            result.joinToString(" ")
        } else {
            result.joinToString("")
        }
    }

    private fun rsaDecrypt(inputText: String, privateKey: RsaKey): String {
        val result = mutableListOf<String>()
        val inputs = parseDecryptInput(inputText)

        inputs.forEach { input ->
            val c = input.value
            val p = modPow(c.toLong(), privateKey.key.toLong(), privateKey.n.toLong()).toInt()

            if (p in 0..255) {
                val resultChar = p.toChar()
                result.add(resultChar.toString())
            } else {
                result.add("[$p]")
            }
        }

        return result.joinToString("")
    }

    private sealed class DecryptInput(val value: Int) {
        class Character(val char: Char, value: Int) : DecryptInput(value)
        class Number(value: Int) : DecryptInput(value)
    }

    private fun parseDecryptInput(inputText: String): List<DecryptInput> {
        val inputs = mutableListOf<DecryptInput>()

        if (inputText.contains("[") && inputText.contains("]")) {
            // Parse mixed format with bracketed numbers
            var i = 0
            while (i < inputText.length) {
                if (inputText[i] == '[') {
                    // Find closing bracket
                    val endIndex = inputText.indexOf(']', i)
                    if (endIndex != -1) {
                        val numberStr = inputText.substring(i + 1, endIndex)
                        val number = numberStr.toIntOrNull()
                        if (number != null) {
                            inputs.add(DecryptInput.Number(number))
                        }
                        i = endIndex + 1
                    } else {
                        i++
                    }
                } else if (inputText[i] != ' ') {
                    // Regular character
                    val char = inputText[i]
                    inputs.add(DecryptInput.Character(char, char.code))
                    i++
                } else {
                    i++
                }
            }
        } else if (inputText.contains(" ")) {
            // Space-separated numbers
            inputText.split("\\s+".toRegex()).forEach { token ->
                if (token.isNotEmpty()) {
                    val numberValue = token.toIntOrNull()
                    if (numberValue != null) {
                        inputs.add(DecryptInput.Number(numberValue))
                    } else {
                        token.forEach { char ->
                            inputs.add(DecryptInput.Character(char, char.code))
                        }
                    }
                }
            }
        } else {
            // Direct character input
            inputText.forEach { char ->
                inputs.add(DecryptInput.Character(char, char.code))
            }
        }

        return inputs
    }

    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    private fun modInverse(a: Int, m: Int): Int {
        var a0 = a
        var m0 = m
        var x0 = 0
        var x1 = 1

        if (m == 1) return 0

        while (a0 > 1) {
            val q = a0 / m0
            var t = m0

            m0 = a0 % m0
            a0 = t
            t = x0

            x0 = x1 - q * x0
            x1 = t
        }

        if (x1 < 0) x1 += m
        return x1
    }

    private fun modPow(base: Long, exponent: Long, modulus: Long): Long {
        if (modulus == 1L) return 0
        var result = 1L
        var base = base % modulus
        var exponent = exponent

        while (exponent > 0) {
            if (exponent % 2 == 1L) {
                result = (result * base) % modulus
            }
            exponent = exponent shr 1
            base = (base * base) % modulus
        }
        return result
    }

    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n == 2) return true
        if (n % 2 == 0) return false

        for (i in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
            if (n % i == 0) return false
        }
        return true
    }

    private fun caesarCipher(text: String, shift: Int): String {
        val result = StringBuilder()

        for (char in text) {
            val ascii = char.code
            val shiftedAscii = ((ascii + shift) % 256 + 256) % 256
            result.append(shiftedAscii.toChar())
        }

        return result.toString()
    }

    private fun generateSteps(text: String, shift: Int, isEncrypt: Boolean): String {
        val steps = StringBuilder()
        val operation = if (isEncrypt) "Enkripsi" else "Dekripsi"
        val formula = if (isEncrypt) "E(p) = (p + $shift) mod 256" else "D(c) = (c - $shift) mod 256"

        steps.append("$operation Caesar Cipher (256 ASCII):\n")
        steps.append("Formula: $formula\n")
        steps.append("Konversi: Setiap karakter → nilai ASCII (0-255)\n\n")

        var charIndex = 1
        for (char in text) {
            val originalValue = char.code
            val shiftedValue = ((originalValue + shift) % 256 + 256) % 256
            val resultChar = shiftedValue.toChar()

            val inputChar = if (isEncrypt) "p" else "c"
            val outputChar = if (isEncrypt) "c" else "p"
            val operation = if (isEncrypt) "E" else "D"

            steps.append("${inputChar}$charIndex = '$char' = ASCII $originalValue")
            steps.append(" → ${outputChar}$charIndex = $operation($originalValue) = ")
            steps.append("($originalValue ${if (shift >= 0) "+" else ""} $shift) mod 256 = ")
            steps.append("${(originalValue + shift)} mod 256 = $shiftedValue")

            if (resultChar.isLetterOrDigit() || resultChar.isWhitespace() || resultChar in "!@#$%^&*(),.?\":{}|<>") {
                steps.append(" = '$resultChar'\n")
            } else {
                steps.append(" = [non-printable]\n")
            }

            charIndex++
        }

        if (charIndex == 1) {
            steps.append("Tidak ada karakter untuk diproses.\n")
        }

        return steps.toString()
    }

    fun validateInput(text: String, shiftStr: String): String? {
        return when {
            text.isEmpty() -> "Input text tidak boleh kosong!"
            shiftStr.isEmpty() -> "Nilai pergeseran tidak boleh kosong!"
            else -> {
                try {
                    shiftStr.toInt()
                    null // No error
                } catch (e: NumberFormatException) {
                    "Nilai pergeseran harus berupa angka!"
                }
            }
        }
    }

    fun validateRsaInput(text: String, pStr: String, qStr: String): String? {
        return when {
            text.isEmpty() -> "Input text tidak boleh kosong!"
            pStr.isEmpty() -> "Nilai p tidak boleh kosong!"
            qStr.isEmpty() -> "Nilai q tidak boleh kosong!"
            else -> {
                try {
                    val p = pStr.toInt()
                    val q = qStr.toInt()
                    when {
                        !isPrime(p) -> "p ($p) harus bilangan prima!"
                        !isPrime(q) -> "q ($q) harus bilangan prima!"
                        p == q -> "p dan q harus berbeda!"
                        p * q < 256 -> "n = p × q harus >= 256 untuk enkripsi ASCII!"
                        else -> null // No error
                    }
                } catch (e: NumberFormatException) {
                    "p dan q harus berupa angka!"
                }
            }
        }
    }
}