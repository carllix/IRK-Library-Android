package com.irklibrary.app.data.repositories

import com.irklibrary.app.data.models.*
import kotlin.math.abs
import kotlin.math.max

class MatrixSPLRepository {

    fun solveSPL(augmentedMatrix: AugmentedMatrix, method: SPLMethod): SPLSolution {
        return when (method) {
            SPLMethod.GAUSS_JORDAN -> solveGaussJordan(augmentedMatrix)
            SPLMethod.CRAMER -> solveCramer(augmentedMatrix)
        }
    }

    private fun solveGaussJordan(augmentedMatrix: AugmentedMatrix): SPLSolution {
        val matrix = augmentedMatrix.toMatrix()
        val (resultMatrix, steps) = gaussJordanElimination(matrix, "Matriks augmented awal:")

        val n = matrix.rows
        val variables = Array(n) { 0.0 }
        var solutionType = SolutionType.UNIQUE
        var message = ""

        val (type, vars, msg) = analyzeSolution(resultMatrix, n)
        solutionType = type
        message = msg

        return SPLSolution(
            method = SPLMethod.GAUSS_JORDAN,
            variables = vars,
            steps = steps,
            solutionType = solutionType,
            message = message
        )
    }

    private fun solveCramer(augmentedMatrix: AugmentedMatrix): SPLSolution {
        val steps = mutableListOf<CramerStep>()
        val A = augmentedMatrix.coefficients
        val b = augmentedMatrix.constants
        val n = A.rows

        if (n != A.cols) {
            return SPLSolution(
                method = SPLMethod.CRAMER,
                variables = null,
                steps = steps,
                solutionType = SolutionType.NO_SOLUTION,
                message = "Matrix harus persegi untuk menggunakan Kaidah Cramer"
            )
        }

        steps.add(CramerStep(
            stepNumber = 1,
            description = "Hitung determinan matrix A:",
            matrix = A.copy()
        ))

        val (detA, detASteps) = calculateDeterminantWithSteps(A)
        steps.addAll(detASteps.map { step ->
            CramerStep(
                stepNumber = steps.size + 1,
                description = step.description,
                matrix = step.matrix,
                determinant = if (step.stepNumber == detASteps.size) detA else null,
                calculation = step.operation
            )
        })

        if (abs(detA) < 1e-10) {
            return SPLSolution(
                method = SPLMethod.CRAMER,
                variables = null,
                steps = steps,
                solutionType = SolutionType.NO_SOLUTION,
                message = "det(A) = 0, sistem tidak dapat diselesaikan dengan Kaidah Cramer"
            )
        }

        val variables = Array(n) { 0.0 }

        for (i in 0 until n) {
            val Ai = A.copy()
            for (row in 0 until n) {
                Ai.set(row, i, b[row])
            }

            steps.add(CramerStep(
                stepNumber = steps.size + 1,
                description = "Hitung determinan A${i+1} (ganti kolom ${i+1} dengan konstanta):",
                matrix = Ai.copy(),
                variable = "x${i+1}"
            ))

            val (detAi, detAiSteps) = calculateDeterminantWithSteps(Ai)
            steps.addAll(detAiSteps.map { step ->
                CramerStep(
                    stepNumber = steps.size + 1,
                    description = step.description,
                    matrix = step.matrix,
                    determinant = if (step.stepNumber == detAiSteps.size) detAi else null,
                    variable = "x${i+1}",
                    calculation = step.operation
                )
            })

            steps.add(CramerStep(
                stepNumber = steps.size + 1,
                description = "Hasil determinan A${i+1}:",
                determinant = detAi,
                variable = "",
                calculation = "det(A${i+1}) = ${formatNumber(detAi)}"
            ))

            variables[i] = detAi / detA

            steps.add(CramerStep(
                stepNumber = steps.size + 1,
                description = "Hasil perhitungan x${i+1}:",
                determinant = variables[i],
                variable = "x${i+1}",
                calculation = "x${i+1} = det(A${i+1})/det(A) = ${formatNumber(detAi)}/${formatNumber(detA)} = ${formatNumber(variables[i])}"
            ))
        }

        val message = "Solusi: " + variables.mapIndexed { i, v ->
            "x${i+1} = ${formatNumber(v)}"
        }.joinToString(", ")

        return SPLSolution(
            method = SPLMethod.CRAMER,
            variables = variables,
            steps = steps,
            solutionType = SolutionType.UNIQUE,
            message = message
        )
    }

    private fun gaussJordanElimination(matrix: Matrix, initialDescription: String): Pair<Matrix, List<MatrixStep>> {
        val steps = mutableListOf<MatrixStep>()
        var currentMatrix = matrix.copy()
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = 0,
            description = initialDescription,
            matrix = currentMatrix.copy()
        ))

        val n = currentMatrix.rows

        for (i in 0 until n) {
            val maxRow = findPivotRow(currentMatrix, i)
            if (maxRow != i) {
                swapRows(currentMatrix, i, maxRow)
                steps.add(MatrixStep(
                    stepNumber = stepNumber++,
                    description = "Tukar baris R${i+1} dengan R${maxRow+1}:",
                    matrix = currentMatrix.copy(),
                    operation = "R${i+1} ↔ R${maxRow+1}"
                ))
            }

            if (abs(currentMatrix.get(i, i)) < 1e-10) {
                continue
            }

            val pivot = currentMatrix.get(i, i)
            if (abs(pivot - 1.0) > 1e-10) {
                scaleRow(currentMatrix, i, 1.0 / pivot)
                steps.add(MatrixStep(
                    stepNumber = stepNumber++,
                    description = "Bagi baris R${i+1} dengan ${formatNumber(pivot)}:",
                    matrix = currentMatrix.copy(),
                    operation = "R${i+1} = R${i+1} / ${formatNumber(pivot)}"
                ))
            }

            for (k in 0 until n) {
                if (k != i && abs(currentMatrix.get(k, i)) > 1e-10) {
                    val factor = currentMatrix.get(k, i)
                    addRowMultiple(currentMatrix, k, i, -factor)

                    val operation = if (factor > 0) {
                        "R${k+1} = R${k+1} - ${formatNumber(factor)} × R${i+1}"
                    } else {
                        "R${k+1} = R${k+1} + ${formatNumber(-factor)} × R${i+1}"
                    }

                    steps.add(MatrixStep(
                        stepNumber = stepNumber++,
                        description = "Eliminasi kolom ${i+1}:",
                        matrix = currentMatrix.copy(),
                        operation = operation
                    ))
                }
            }
        }

        return Pair(currentMatrix, steps)
    }

    private fun swapRows(matrix: Matrix, row1: Int, row2: Int) {
        for (j in 0 until matrix.cols) {
            val temp = matrix.get(row1, j)
            matrix.set(row1, j, matrix.get(row2, j))
            matrix.set(row2, j, temp)
        }
    }

    private fun scaleRow(matrix: Matrix, row: Int, factor: Double) {
        for (j in 0 until matrix.cols) {
            val newValue = matrix.get(row, j) * factor
            matrix.set(row, j, if (abs(newValue) < 1e-10) 0.0 else newValue)
        }
    }

    private fun addRowMultiple(matrix: Matrix, toRow: Int, fromRow: Int, factor: Double) {
        for (j in 0 until matrix.cols) {
            val newValue = matrix.get(toRow, j) + factor * matrix.get(fromRow, j)
            matrix.set(toRow, j, if (abs(newValue) < 1e-10) 0.0 else newValue)
        }
    }

    private fun findPivotRow(matrix: Matrix, startRow: Int): Int {
        var maxRow = startRow
        for (k in startRow + 1 until matrix.rows) {
            if (abs(matrix.get(k, startRow)) > abs(matrix.get(maxRow, startRow))) {
                maxRow = k
            }
        }
        return maxRow
    }

    private fun analyzeSolution(matrix: Matrix, n: Int): Triple<SolutionType, Array<Double>?, String> {
        val variables = Array(n) { 0.0 }

        for (i in 0 until n) {
            var allZero = true
            for (j in 0 until n) {
                if (abs(matrix.get(i, j)) > 1e-10) {
                    allZero = false
                    break
                }
            }
            if (allZero && abs(matrix.get(i, n)) > 1e-10) {
                return Triple(SolutionType.NO_SOLUTION, null, "Sistem tidak konsisten (tidak ada solusi)")
            }
        }

        val pivotColumns = mutableSetOf<Int>()
        val freeVariables = mutableListOf<Int>()

        for (i in 0 until n) {
            var leadingColumn = -1
            for (j in 0 until n) {
                if (abs(matrix.get(i, j)) > 1e-10) {
                    leadingColumn = j
                    break
                }
            }
            if (leadingColumn != -1) {
                pivotColumns.add(leadingColumn)
            }
        }

        // Find free variables
        for (j in 0 until n) {
            if (!pivotColumns.contains(j)) {
                freeVariables.add(j)
            }
        }

        if (freeVariables.isNotEmpty()) {
            val message = "Sistem memiliki solusi tak hingga dengan ${freeVariables.size} variabel bebas: " +
                    freeVariables.map { "x${it+1}" }.joinToString(", ") +
                    "\n\nSolusi parametrik:\n" + buildParametricSolution(matrix, pivotColumns, freeVariables, n)

            return Triple(SolutionType.INFINITE, null, message)
        } else {
            val nonZeroRows = (0 until n).count { i ->
                (0 until n).any { j -> abs(matrix.get(i, j)) > 1e-10 }
            }

            if (nonZeroRows < n) {
                return Triple(SolutionType.INFINITE, null, "Sistem memiliki solusi tak hingga (tidak cukup persamaan independent)")
            } else {
                for (i in 0 until n) {
                    var leadingColumn = -1
                    for (j in 0 until n) {
                        if (abs(matrix.get(i, j)) > 1e-10) {
                            leadingColumn = j
                            break
                        }
                    }
                    if (leadingColumn != -1 && leadingColumn < n) {
                        variables[leadingColumn] = matrix.get(i, n)
                    }
                }

                val message = "Solusi unik ditemukan: " + variables.mapIndexed { i, v ->
                    "x${i+1} = ${formatNumber(v)}"
                }.joinToString(", ")

                return Triple(SolutionType.UNIQUE, variables, message)
            }
        }
    }

    private fun buildParametricSolution(matrix: Matrix, pivotColumns: Set<Int>, freeVariables: List<Int>, n: Int): String {
        val parametricSolution = mutableListOf<String>()

        for (i in 0 until n) {
            if (pivotColumns.contains(i)) {
                val rowIndex = findRowWithPivotInColumn(matrix, i, n)
                if (rowIndex != -1) {
                    var expression = formatNumber(matrix.get(rowIndex, n))
                    val terms = mutableListOf<String>()

                    for (j in 0 until n) {
                        if (j != i && abs(matrix.get(rowIndex, j)) > 1e-10) {
                            val coeff = -matrix.get(rowIndex, j)
                            if (freeVariables.contains(j)) {
                                val sign = if (coeff >= 0) "+" else ""
                                terms.add("${sign}${formatNumber(coeff)}⋅t${freeVariables.indexOf(j)+1}")
                            }
                        }
                    }

                    if (terms.isNotEmpty()) {
                        expression += " " + terms.joinToString(" ")
                    }
                    parametricSolution.add("x${i+1} = $expression")
                }
            } else {
                val paramIndex = freeVariables.indexOf(i) + 1
                parametricSolution.add("x${i+1} = t$paramIndex")
            }
        }

        return parametricSolution.joinToString("\n")
    }

    private fun calculateDeterminantWithSteps(matrix: Matrix): Pair<Double, List<MatrixStep>> {
        val steps = mutableListOf<MatrixStep>()
        val n = matrix.rows
        var currentMatrix = matrix.copy()
        var determinant = 1.0
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix awal:",
            matrix = currentMatrix.copy()
        ))

        for (i in 0 until n) {
            val maxRow = findPivotRow(currentMatrix, i)

            if (maxRow != i) {
                swapRows(currentMatrix, i, maxRow)
                determinant *= -1
                steps.add(MatrixStep(
                    stepNumber = stepNumber++,
                    description = "Tukar baris R${i+1} ↔ R${maxRow+1} (det × -1):",
                    matrix = currentMatrix.copy(),
                    operation = "R${i+1} ↔ R${maxRow+1}"
                ))
            }

            val pivot = currentMatrix.get(i, i)
            if (abs(pivot) < 1e-10) {
                determinant = 0.0
                break
            }

            for (k in i + 1 until n) {
                val factor = currentMatrix.get(k, i) / pivot
                if (abs(factor) > 1e-10) {
                    addRowMultiple(currentMatrix, k, i, -factor)

                    val operation = if (factor > 0) {
                        "R${k+1} = R${k+1} - ${formatNumber(factor)} × R${i+1}"
                    } else {
                        "R${k+1} = R${k+1} + ${formatNumber(-factor)} × R${i+1}"
                    }

                    steps.add(MatrixStep(
                        stepNumber = stepNumber++,
                        description = "Eliminasi elemen di bawah pivot:",
                        matrix = currentMatrix.copy(),
                        operation = operation
                    ))
                }
            }
        }

        for (i in 0 until n) {
            determinant *= currentMatrix.get(i, i)
        }

        steps.add(MatrixStep(
            stepNumber = stepNumber,
            description = "Determinan = produk elemen diagonal:",
            matrix = currentMatrix.copy(),
            operation = "det = " + (0 until n).map { i ->
                formatNumber(currentMatrix.get(i, i))
            }.joinToString(" × ") + " = ${formatNumber(determinant)}"
        ))

        return Pair(determinant, steps)
    }

    fun performMatrixOperation(matrix1: Matrix, matrix2: Matrix?, operation: MatrixOperation, multiplicationMethod: MultiplicationMethod? = null, exponent: Int? = null): MatrixResult {
        return when (operation) {
            MatrixOperation.ADDITION -> addMatricesWithSteps(matrix1, matrix2!!)
            MatrixOperation.SUBTRACTION -> subtractMatricesWithSteps(matrix1, matrix2!!)
            MatrixOperation.MULTIPLICATION -> {
                when (multiplicationMethod) {
                    MultiplicationMethod.BRUTE_FORCE -> multiplyMatricesWithSteps(matrix1, matrix2!!)
                    MultiplicationMethod.DIVIDE_AND_CONQUER -> multiplyMatricesDnCWithSteps(matrix1, matrix2!!)
                    else -> multiplyMatricesWithSteps(matrix1, matrix2!!)
                }
            }
            MatrixOperation.INVERSE -> calculateInverse(matrix1)
            MatrixOperation.DETERMINANT -> calculateDeterminant(matrix1)
            MatrixOperation.EXPONENTIATION -> calculateExponentiation(matrix1, exponent ?: 1)
        }
    }

    private fun multiplyMatricesDnCWithSteps(matrix1: Matrix, matrix2: Matrix): MatrixResult {
        if (matrix1.cols != matrix2.rows) {
            return MatrixResult(
                operation = MatrixOperation.MULTIPLICATION,
                steps = emptyList(),
                success = false,
                message = "Jumlah kolom matrix pertama harus sama dengan jumlah baris matrix kedua",
                multiplicationMethod = MultiplicationMethod.DIVIDE_AND_CONQUER
            )
        }

        val steps = mutableListOf<MatrixStep>()
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix A (${matrix1.rows}×${matrix1.cols}):",
            matrix = matrix1.copy(),
            operation = "Metode: Divide and Conquer"
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix B (${matrix2.rows}×${matrix2.cols}):",
            matrix = matrix2.copy(),
            operation = ""
        ))

        val n = max(max(matrix1.rows, matrix1.cols), max(matrix2.rows, matrix2.cols))
        val nextPowerOf2 = nextPowerOfTwo(n)

        if (nextPowerOf2 > n) {
            steps.add(MatrixStep(
                stepNumber = stepNumber++,
                description = "Padding matriks ke ukuran ${nextPowerOf2}×${nextPowerOf2} (power of 2):",
                matrix = Matrix(1, 1), // Dummy matrix
                operation = "Menambah padding dengan nilai 0 untuk optimalisasi DnC"
            ))
        }

        val paddedA = padMatrix(matrix1, nextPowerOf2, nextPowerOf2)
        val paddedB = padMatrix(matrix2, nextPowerOf2, nextPowerOf2)

        if (nextPowerOf2 > n) {
            steps.add(MatrixStep(
                stepNumber = stepNumber++,
                description = "Matrix A setelah padding:",
                matrix = paddedA.copy(),
                operation = ""
            ))

            steps.add(MatrixStep(
                stepNumber = stepNumber++,
                description = "Matrix B setelah padding:",
                matrix = paddedB.copy(),
                operation = ""
            ))
        }

        val dnCSteps = mutableListOf<MatrixStep>()
        val result = multiplyDnCRecursive(paddedA, paddedB, 0, dnCSteps)

        dnCSteps.forEachIndexed { index, step ->
            steps.add(MatrixStep(
                stepNumber = stepNumber++,
                description = step.description,
                matrix = step.matrix.copy(),
                operation = step.operation
            ))
        }

        val finalResult = extractMatrix(result, matrix1.rows, matrix2.cols)

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Hasil perkalian (menghilangkan padding):",
            matrix = finalResult.copy(),
            operation = "Matrix hasil DnC diambil sesuai ukuran asli"
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber,
            description = "Hasil akhir perkalian matrix A × B:",
            matrix = finalResult,
            operation = "Perkalian DnC selesai"
        ))

        return MatrixResult(
            operation = MatrixOperation.MULTIPLICATION,
            result = finalResult,
            steps = steps,
            success = true,
            message = "Perkalian dengan Divide and Conquer berhasil",
            multiplicationMethod = MultiplicationMethod.DIVIDE_AND_CONQUER
        )
    }

    private fun multiplyDnCRecursive(A: Matrix, B: Matrix, depth: Int, steps: MutableList<MatrixStep>): Matrix {
        val n = A.rows

        if (n <= 2) {
            val result = Matrix(n, n)
            for (i in 0 until n) {
                for (j in 0 until n) {
                    var sum = 0.0
                    for (k in 0 until n) {
                        sum += A.get(i, k) * B.get(k, j)
                    }
                    result.set(i, j, sum)
                }
            }

            if (depth <= 2) {
                steps.add(MatrixStep(
                    stepNumber = steps.size + 1,
                    description = "Base case (${n}×${n}) - menggunakan perkalian langsung:",
                    matrix = result.copy(),
                    operation = "Depth: $depth"
                ))
            }

            return result
        }

        val half = n / 2

        val A11 = getSubMatrix(A, 0, 0, half, half)
        val A12 = getSubMatrix(A, 0, half, half, half)
        val A21 = getSubMatrix(A, half, 0, half, half)
        val A22 = getSubMatrix(A, half, half, half, half)

        val B11 = getSubMatrix(B, 0, 0, half, half)
        val B12 = getSubMatrix(B, 0, half, half, half)
        val B21 = getSubMatrix(B, half, 0, half, half)
        val B22 = getSubMatrix(B, half, half, half, half)

        if (depth <= 1) {
            steps.add(MatrixStep(
                stepNumber = steps.size + 1,
                description = "Membagi matriks ${n}×${n} menjadi 4 submatriks ${half}×${half}:",
                matrix = A.copy(),
                operation = "Depth: $depth - Divide phase"
            ))
        }

        val C11 = addMatrices(
            multiplyDnCRecursive(A11, B11, depth + 1, steps),
            multiplyDnCRecursive(A12, B21, depth + 1, steps)
        )
        val C12 = addMatrices(
            multiplyDnCRecursive(A11, B12, depth + 1, steps),
            multiplyDnCRecursive(A12, B22, depth + 1, steps)
        )
        val C21 = addMatrices(
            multiplyDnCRecursive(A21, B11, depth + 1, steps),
            multiplyDnCRecursive(A22, B21, depth + 1, steps)
        )
        val C22 = addMatrices(
            multiplyDnCRecursive(A21, B12, depth + 1, steps),
            multiplyDnCRecursive(A22, B22, depth + 1, steps)
        )

        val result = Matrix(n, n)
        setSubMatrix(result, C11, 0, 0)
        setSubMatrix(result, C12, 0, half)
        setSubMatrix(result, C21, half, 0)
        setSubMatrix(result, C22, half, half)

        if (depth <= 1) {
            steps.add(MatrixStep(
                stepNumber = steps.size + 1,
                description = "Menggabungkan hasil submatriks ${half}×${half} menjadi ${n}×${n}:",
                matrix = result.copy(),
                operation = "Depth: $depth - Conquer phase"
            ))
        }

        return result
    }

    private fun nextPowerOfTwo(n: Int): Int {
        var power = 1
        while (power < n) {
            power *= 2
        }
        return power
    }

    private fun padMatrix(matrix: Matrix, newRows: Int, newCols: Int): Matrix {
        val padded = Matrix(newRows, newCols)
        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.cols) {
                padded.set(i, j, matrix.get(i, j))
            }
        }
        return padded
    }

    private fun extractMatrix(matrix: Matrix, rows: Int, cols: Int): Matrix {
        val extracted = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                extracted.set(i, j, matrix.get(i, j))
            }
        }
        return extracted
    }

    private fun getSubMatrix(matrix: Matrix, startRow: Int, startCol: Int, rows: Int, cols: Int): Matrix {
        val sub = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                sub.set(i, j, matrix.get(startRow + i, startCol + j))
            }
        }
        return sub
    }

    private fun setSubMatrix(matrix: Matrix, subMatrix: Matrix, startRow: Int, startCol: Int) {
        for (i in 0 until subMatrix.rows) {
            for (j in 0 until subMatrix.cols) {
                matrix.set(startRow + i, startCol + j, subMatrix.get(i, j))
            }
        }
    }

    private fun addMatrices(A: Matrix, B: Matrix): Matrix {
        val result = Matrix(A.rows, A.cols)
        for (i in 0 until A.rows) {
            for (j in 0 until A.cols) {
                result.set(i, j, A.get(i, j) + B.get(i, j))
            }
        }
        return result
    }

    private fun addMatricesWithSteps(matrix1: Matrix, matrix2: Matrix): MatrixResult {
        if (matrix1.rows != matrix2.rows || matrix1.cols != matrix2.cols) {
            return MatrixResult(
                operation = MatrixOperation.ADDITION,
                steps = emptyList(),
                success = false,
                message = "Matrix harus memiliki dimensi yang sama untuk penjumlahan"
            )
        }

        val result = Matrix(matrix1.rows, matrix1.cols)
        val steps = mutableListOf<MatrixStep>()
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix A:",
            matrix = matrix1.copy(),
            operation = ""
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix B:",
            matrix = matrix2.copy(),
            operation = ""
        ))

        val intermediateMatrix = Matrix(matrix1.rows, matrix1.cols)
        val calculationDetails = mutableListOf<String>()

        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix1.cols) {
                val valueA = matrix1.get(i, j)
                val valueB = matrix2.get(i, j)
                val sum = valueA + valueB
                result.set(i, j, sum)
                intermediateMatrix.set(i, j, sum)

                calculationDetails.add("C[${i+1},${j+1}] = ${formatNumber(valueA)} + ${formatNumber(valueB)} = ${formatNumber(sum)}")
            }
        }

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Proses penjumlahan elemen per elemen:",
            matrix = intermediateMatrix.copy(),
            operation = calculationDetails.joinToString(", ")
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber,
            description = "Hasil penjumlahan matrix A + B:",
            matrix = result,
            operation = "C[i,j] = A[i,j] + B[i,j]"
        ))

        return MatrixResult(
            operation = MatrixOperation.ADDITION,
            result = result,
            steps = steps,
            success = true,
            message = "Penjumlahan berhasil"
        )
    }

    private fun subtractMatricesWithSteps(matrix1: Matrix, matrix2: Matrix): MatrixResult {
        if (matrix1.rows != matrix2.rows || matrix1.cols != matrix2.cols) {
            return MatrixResult(
                operation = MatrixOperation.SUBTRACTION,
                steps = emptyList(),
                success = false,
                message = "Matrix harus memiliki dimensi yang sama untuk pengurangan"
            )
        }

        val result = Matrix(matrix1.rows, matrix1.cols)
        val steps = mutableListOf<MatrixStep>()
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix A:",
            matrix = matrix1.copy(),
            operation = ""
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix B:",
            matrix = matrix2.copy(),
            operation = ""
        ))

        val intermediateMatrix = Matrix(matrix1.rows, matrix1.cols)
        val calculationDetails = mutableListOf<String>()

        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix1.cols) {
                val valueA = matrix1.get(i, j)
                val valueB = matrix2.get(i, j)
                val difference = valueA - valueB
                result.set(i, j, difference)
                intermediateMatrix.set(i, j, difference)

                calculationDetails.add("C[${i+1},${j+1}] = ${formatNumber(valueA)} - ${formatNumber(valueB)} = ${formatNumber(difference)}")
            }
        }

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Proses pengurangan elemen per elemen:",
            matrix = intermediateMatrix.copy(),
            operation = calculationDetails.joinToString(", ")
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber,
            description = "Hasil pengurangan matrix A - B:",
            matrix = result,
            operation = "C[i,j] = A[i,j] - B[i,j]"
        ))

        return MatrixResult(
            operation = MatrixOperation.SUBTRACTION,
            result = result,
            steps = steps,
            success = true,
            message = "Pengurangan berhasil"
        )
    }

    private fun multiplyMatricesWithSteps(matrix1: Matrix, matrix2: Matrix): MatrixResult {
        if (matrix1.cols != matrix2.rows) {
            return MatrixResult(
                operation = MatrixOperation.MULTIPLICATION,
                steps = emptyList(),
                success = false,
                message = "Jumlah kolom matrix pertama harus sama dengan jumlah baris matrix kedua",
                multiplicationMethod = MultiplicationMethod.BRUTE_FORCE
            )
        }

        val result = Matrix(matrix1.rows, matrix2.cols)
        val steps = mutableListOf<MatrixStep>()
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix A (${matrix1.rows}×${matrix1.cols}):",
            matrix = matrix1.copy(),
            operation = "Metode: Brute Force (Standar)"
        ))

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix B (${matrix2.rows}×${matrix2.cols}):",
            matrix = matrix2.copy(),
            operation = ""
        ))

        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix2.cols) {
                var sum = 0.0
                val calculationParts = mutableListOf<String>()
                val valueParts = mutableListOf<String>()

                for (k in 0 until matrix1.cols) {
                    val valueA = matrix1.get(i, k)
                    val valueB = matrix2.get(k, j)
                    sum += valueA * valueB
                    calculationParts.add("${formatNumber(valueA)}×${formatNumber(valueB)}")
                    valueParts.add("${formatNumber(valueA * valueB)}")
                }

                result.set(i, j, sum)

                val intermediateMatrix = Matrix(matrix1.rows, matrix2.cols)
                for (ii in 0 until matrix1.rows) {
                    for (jj in 0 until matrix2.cols) {
                        if (ii < i || (ii == i && jj <= j)) {
                            intermediateMatrix.set(ii, jj, result.get(ii, jj))
                        } else {
                            intermediateMatrix.set(ii, jj, 0.0)
                        }
                    }
                }

                val calculation = "C[${i+1},${j+1}] = ${calculationParts.joinToString(" + ")} = ${valueParts.joinToString(" + ")} = ${formatNumber(sum)}"

                steps.add(MatrixStep(
                    stepNumber = stepNumber++,
                    description = "Hitung elemen C[${i+1},${j+1}]:",
                    matrix = intermediateMatrix.copy(),
                    operation = calculation
                ))
            }
        }

        steps.add(MatrixStep(
            stepNumber = stepNumber,
            description = "Hasil perkalian matrix A × B:",
            matrix = result,
            operation = "Perkalian matrix selesai"
        ))

        return MatrixResult(
            operation = MatrixOperation.MULTIPLICATION,
            result = result,
            steps = steps,
            success = true,
            message = "Perkalian dengan Brute Force berhasil",
            multiplicationMethod = MultiplicationMethod.BRUTE_FORCE
        )
    }

    private fun subtractMatrices(matrix1: Matrix, matrix2: Matrix): MatrixResult {
        if (matrix1.rows != matrix2.rows || matrix1.cols != matrix2.cols) {
            return MatrixResult(
                operation = MatrixOperation.SUBTRACTION,
                steps = emptyList(),
                success = false,
                message = "Matrix harus memiliki dimensi yang sama untuk pengurangan"
            )
        }

        val result = Matrix(matrix1.rows, matrix1.cols)
        val steps = mutableListOf<MatrixStep>()

        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix1.cols) {
                result.set(i, j, matrix1.get(i, j) - matrix2.get(i, j))
            }
        }

        steps.add(MatrixStep(
            stepNumber = 1,
            description = "Pengurangan matrix:",
            matrix = result,
            operation = "C[i,j] = A[i,j] - B[i,j]"
        ))

        return MatrixResult(
            operation = MatrixOperation.SUBTRACTION,
            result = result,
            steps = steps,
            success = true,
            message = "Pengurangan berhasil"
        )
    }

    private fun multiplyMatrices(matrix1: Matrix, matrix2: Matrix): MatrixResult {
        if (matrix1.cols != matrix2.rows) {
            return MatrixResult(
                operation = MatrixOperation.MULTIPLICATION,
                steps = emptyList(),
                success = false,
                message = "Jumlah kolom matrix pertama harus sama dengan jumlah baris matrix kedua"
            )
        }

        val result = Matrix(matrix1.rows, matrix2.cols)
        val steps = mutableListOf<MatrixStep>()

        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix2.cols) {
                var sum = 0.0
                for (k in 0 until matrix1.cols) {
                    sum += matrix1.get(i, k) * matrix2.get(k, j)
                }
                result.set(i, j, sum)
            }
        }

        steps.add(MatrixStep(
            stepNumber = 1,
            description = "Perkalian matrix:",
            matrix = result,
            operation = "C[i,j] = Σ(A[i,k] × B[k,j])"
        ))

        return MatrixResult(
            operation = MatrixOperation.MULTIPLICATION,
            result = result,
            steps = steps,
            success = true,
            message = "Perkalian berhasil"
        )
    }

    private fun calculateInverse(matrix: Matrix): MatrixResult {
        if (matrix.rows != matrix.cols) {
            return MatrixResult(
                operation = MatrixOperation.INVERSE,
                steps = emptyList(),
                success = false,
                message = "Matrix harus persegi untuk menghitung inverse"
            )
        }

        val n = matrix.rows
        val augmented = Matrix(n, 2 * n)

        for (i in 0 until n) {
            for (j in 0 until n) {
                augmented.set(i, j, matrix.get(i, j))
                augmented.set(i, j + n, if (i == j) 1.0 else 0.0)
            }
        }

        val (resultMatrix, steps) = gaussJordanElimination(augmented, "Matrix augmented [A|I]:")

        for (i in 0 until n) {
            if (abs(resultMatrix.get(i, i)) < 1e-10) {
                return MatrixResult(
                    operation = MatrixOperation.INVERSE,
                    steps = steps,
                    success = false,
                    message = "Matrix singular (tidak memiliki inverse)"
                )
            }
        }

        val inverse = Matrix(n, n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                inverse.set(i, j, resultMatrix.get(i, j + n))
            }
        }

        return MatrixResult(
            operation = MatrixOperation.INVERSE,
            result = inverse,
            steps = steps,
            success = true,
            message = "Inverse berhasil dihitung"
        )
    }

    private fun calculateDeterminant(matrix: Matrix): MatrixResult {
        if (matrix.rows != matrix.cols) {
            return MatrixResult(
                operation = MatrixOperation.DETERMINANT,
                steps = emptyList(),
                success = false,
                message = "Matrix harus persegi untuk menghitung determinan"
            )
        }

        val (determinant, steps) = calculateDeterminantWithSteps(matrix)

        return MatrixResult(
            operation = MatrixOperation.DETERMINANT,
            determinant = determinant,
            steps = steps,
            success = true,
            message = "Determinan = ${formatNumber(determinant)}"
        )
    }

    private fun calculateExponentiation(matrix: Matrix, exponent: Int): MatrixResult {
        if (matrix.rows != matrix.cols) {
            return MatrixResult(
                operation = MatrixOperation.EXPONENTIATION,
                steps = emptyList(),
                success = false,
                message = "Matrix harus persegi untuk eksponensiasi"
            )
        }

        if (exponent < 0) {
            return MatrixResult(
                operation = MatrixOperation.EXPONENTIATION,
                steps = emptyList(),
                success = false,
                message = "Eksponen harus bilangan bulat non-negatif"
            )
        }

        val steps = mutableListOf<MatrixStep>()
        var stepNumber = 1

        steps.add(MatrixStep(
            stepNumber = stepNumber++,
            description = "Matrix A (${matrix.rows}×${matrix.cols}):",
            matrix = matrix.copy(),
            operation = "Eksponensiasi: A^$exponent menggunakan Divide and Conquer"
        ))

        when (exponent) {
            0 -> {
                val identity = createIdentityMatrix(matrix.rows)
                steps.add(MatrixStep(
                    stepNumber = stepNumber++,
                    description = "A^0 = I (Matrix Identitas):",
                    matrix = identity.copy(),
                    operation = "Base case: Setiap matrix pangkat 0 adalah matrix identitas"
                ))

                steps.add(MatrixStep(
                    stepNumber = stepNumber,
                    description = "Hasil akhir A^0:",
                    matrix = identity,
                    operation = "Eksponensiasi selesai"
                ))

                return MatrixResult(
                    operation = MatrixOperation.EXPONENTIATION,
                    result = identity,
                    steps = steps,
                    success = true,
                    message = "Eksponensiasi A^$exponent berhasil"
                )
            }
            1 -> {
                steps.add(MatrixStep(
                    stepNumber = stepNumber++,
                    description = "A^1 = A:",
                    matrix = matrix.copy(),
                    operation = "Base case: Setiap matrix pangkat 1 adalah matrix itu sendiri"
                ))

                steps.add(MatrixStep(
                    stepNumber = stepNumber,
                    description = "Hasil akhir A^1:",
                    matrix = matrix,
                    operation = "Eksponensiasi selesai"
                ))

                return MatrixResult(
                    operation = MatrixOperation.EXPONENTIATION,
                    result = matrix,
                    steps = steps,
                    success = true,
                    message = "Eksponensiasi A^$exponent berhasil"
                )
            }
        }

        val exponentiationSteps = mutableListOf<MatrixStep>()
        val result = matrixPowerDnC(matrix, exponent, 0, exponentiationSteps)

        exponentiationSteps.forEachIndexed { index, step ->
            steps.add(MatrixStep(
                stepNumber = stepNumber++,
                description = step.description,
                matrix = step.matrix.copy(),
                operation = step.operation
            ))
        }

        steps.add(MatrixStep(
            stepNumber = stepNumber,
            description = "Hasil akhir eksponensiasi A^$exponent:",
            matrix = result,
            operation = "Eksponensiasi DnC selesai"
        ))

        return MatrixResult(
            operation = MatrixOperation.EXPONENTIATION,
            result = result,
            steps = steps,
            success = true,
            message = "Eksponensiasi A^$exponent berhasil menggunakan Divide and Conquer"
        )
    }

    private fun matrixPowerDnC(matrix: Matrix, exponent: Int, depth: Int, steps: MutableList<MatrixStep>): Matrix {
        if (exponent == 0) {
            return createIdentityMatrix(matrix.rows)
        }
        if (exponent == 1) {
            return matrix.copy()
        }

        if (depth <= 3) {
            steps.add(MatrixStep(
                stepNumber = steps.size + 1,
                description = "Menghitung A^$exponent dengan DnC:",
                matrix = matrix.copy(),
                operation = "Depth: $depth - ${if (exponent % 2 == 0) "A^$exponent = (A^${exponent/2})^2" else "A^$exponent = A × A^${exponent-1}"}"
            ))
        }

        return if (exponent % 2 == 0) {
            val halfPower = matrixPowerDnC(matrix, exponent / 2, depth + 1, steps)

            if (depth <= 2) {
                steps.add(MatrixStep(
                    stepNumber = steps.size + 1,
                    description = "Hasil A^${exponent/2}:",
                    matrix = halfPower.copy(),
                    operation = "Depth: $depth - Setengah pangkat dihitung"
                ))
            }

            val result = multiplyMatricesDirect(halfPower, halfPower)

            if (depth <= 2) {
                steps.add(MatrixStep(
                    stepNumber = steps.size + 1,
                    description = "Kuadratkan hasil: (A^${exponent/2})^2 = A^$exponent:",
                    matrix = result.copy(),
                    operation = "Depth: $depth - Perkalian matrix dilakukan"
                ))
            }

            result
        } else {
            val powerMinusOne = matrixPowerDnC(matrix, exponent - 1, depth + 1, steps)

            if (depth <= 2) {
                steps.add(MatrixStep(
                    stepNumber = steps.size + 1,
                    description = "Hasil A^${exponent-1}:",
                    matrix = powerMinusOne.copy(),
                    operation = "Depth: $depth - Pangkat n-1 dihitung"
                ))
            }

            val result = multiplyMatricesDirect(matrix, powerMinusOne)

            if (depth <= 2) {
                steps.add(MatrixStep(
                    stepNumber = steps.size + 1,
                    description = "Kalikan dengan A: A × A^${exponent-1} = A^$exponent:",
                    matrix = result.copy(),
                    operation = "Depth: $depth - Perkalian dengan matrix asli"
                ))
            }

            result
        }
    }

    private fun createIdentityMatrix(size: Int): Matrix {
        val identity = Matrix(size, size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                identity.set(i, j, if (i == j) 1.0 else 0.0)
            }
        }
        return identity
    }

    private fun multiplyMatricesDirect(matrix1: Matrix, matrix2: Matrix): Matrix {
        val result = Matrix(matrix1.rows, matrix2.cols)
        for (i in 0 until matrix1.rows) {
            for (j in 0 until matrix2.cols) {
                var sum = 0.0
                for (k in 0 until matrix1.cols) {
                    sum += matrix1.get(i, k) * matrix2.get(k, j)
                }
                result.set(i, j, sum)
            }
        }
        return result
    }

    private fun findRowWithPivotInColumn(matrix: Matrix, column: Int, numRows: Int): Int {
        for (i in 0 until numRows) {
            if (abs(matrix.get(i, column)) > 1e-10) {
                var isLeading = true
                for (j in 0 until column) {
                    if (abs(matrix.get(i, j)) > 1e-10) {
                        isLeading = false
                        break
                    }
                }
                if (isLeading) {
                    return i
                }
            }
        }
        return -1
    }

    private fun formatNumber(number: Double): String {
        val normalizedNumber = if (abs(number) < 1e-10) 0.0 else number

        return if (normalizedNumber == normalizedNumber.toInt().toDouble()) {
            normalizedNumber.toInt().toString()
        } else {
            String.format("%.3f", normalizedNumber).trimEnd('0').trimEnd('.')
        }
    }
}