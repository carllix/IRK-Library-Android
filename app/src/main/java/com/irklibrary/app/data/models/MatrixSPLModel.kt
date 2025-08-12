package com.irklibrary.app.data.models

data class Matrix(
    val rows: Int,
    val cols: Int,
    val data: Array<Array<Double>>
) {
    constructor(rows: Int, cols: Int) : this(rows, cols, Array(rows) { Array(cols) { 0.0 } })

    fun copy(): Matrix {
        val newData = Array(rows) { i -> Array(cols) { j -> data[i][j] } }
        return Matrix(rows, cols, newData)
    }

    fun get(row: Int, col: Int): Double = data[row][col]
    fun set(row: Int, col: Int, value: Double) {
        data[row][col] = if (kotlin.math.abs(value) < 1e-10) 0.0 else value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Matrix
        if (rows != other.rows) return false
        if (cols != other.cols) return false
        return data.contentDeepEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + data.contentDeepHashCode()
        return result
    }
}

data class AugmentedMatrix(
    val coefficients: Matrix,
    val constants: Array<Double>
) {
    fun toMatrix(): Matrix {
        val augmented = Matrix(coefficients.rows, coefficients.cols + 1)
        for (i in 0 until coefficients.rows) {
            for (j in 0 until coefficients.cols) {
                augmented.set(i, j, coefficients.get(i, j))
            }
            augmented.set(i, coefficients.cols, constants[i])
        }
        return augmented
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AugmentedMatrix
        if (coefficients != other.coefficients) return false
        return constants.contentEquals(other.constants)
    }

    override fun hashCode(): Int {
        var result = coefficients.hashCode()
        result = 31 * result + constants.contentHashCode()
        return result
    }
}

data class MatrixStep(
    val stepNumber: Int,
    val description: String,
    val matrix: Matrix,
    val operation: String = ""
)

enum class MatrixOperation {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    INVERSE,
    DETERMINANT,
    EXPONENTIATION
}

enum class MultiplicationMethod {
    BRUTE_FORCE,
    DIVIDE_AND_CONQUER
}

data class MatrixResult(
    val operation: MatrixOperation,
    val result: Matrix? = null,
    val determinant: Double? = null,
    val steps: List<MatrixStep>,
    val success: Boolean,
    val message: String,
    val multiplicationMethod: MultiplicationMethod? = null
)

enum class SPLMethod {
    GAUSS_JORDAN,
    CRAMER
}

data class CramerStep(
    val stepNumber: Int,
    val description: String,
    val matrix: Matrix? = null,
    val determinant: Double? = null,
    val variable: String = "",
    val calculation: String = ""
)

data class SPLSolution(
    val method: SPLMethod,
    val variables: Array<Double>?,
    val steps: List<Any>,
    val solutionType: SolutionType,
    val message: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SPLSolution
        if (method != other.method) return false
        if (variables != null && other.variables != null) {
            if (!variables.contentEquals(other.variables)) return false
        } else if (variables != other.variables) return false
        if (steps != other.steps) return false
        if (solutionType != other.solutionType) return false
        if (message != other.message) return false
        return true
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + (variables?.contentHashCode() ?: 0)
        result = 31 * result + steps.hashCode()
        result = 31 * result + solutionType.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }
}

enum class SolutionType {
    UNIQUE,
    INFINITE,
    NO_SOLUTION
}