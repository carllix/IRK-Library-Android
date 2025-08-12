package com.irklibrary.app.ui.page1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irklibrary.app.data.models.*
import com.irklibrary.app.data.repositories.MatrixSPLRepository

class MatrixSPLViewModel : ViewModel() {

    private val repository = MatrixSPLRepository()

    // Current mode (SPL or Matrix Operations)
    private val _currentMode = MutableLiveData<OperationMode>(OperationMode.SPL)
    val currentMode: LiveData<OperationMode> = _currentMode

    // SPL related
    private val _splMethod = MutableLiveData<SPLMethod>(SPLMethod.GAUSS_JORDAN)
    val splMethod: LiveData<SPLMethod> = _splMethod

    private val _splSize = MutableLiveData<Int>(3)
    val splSize: LiveData<Int> = _splSize

    private val _splMatrix = MutableLiveData<Matrix>()
    val splMatrix: LiveData<Matrix> = _splMatrix

    private val _splConstants = MutableLiveData<Array<Double>>()
    val splConstants: LiveData<Array<Double>> = _splConstants

    private val _splSolution = MutableLiveData<SPLSolution?>()
    val splSolution: LiveData<SPLSolution?> = _splSolution

    // Matrix Operations related
    private val _matrixOperation = MutableLiveData<MatrixOperation>(MatrixOperation.ADDITION)
    val matrixOperation: LiveData<MatrixOperation> = _matrixOperation

    private val _multiplicationMethod = MutableLiveData<MultiplicationMethod>(MultiplicationMethod.BRUTE_FORCE)
    val multiplicationMethod: LiveData<MultiplicationMethod> = _multiplicationMethod

    private val _showMultiplicationMethods = MutableLiveData<Boolean>(false)
    val showMultiplicationMethods: LiveData<Boolean> = _showMultiplicationMethods

    private val _matrixARows = MutableLiveData<Int>(2)
    val matrixARows: LiveData<Int> = _matrixARows

    private val _matrixACols = MutableLiveData<Int>(2)
    val matrixACols: LiveData<Int> = _matrixACols

    private val _matrixBRows = MutableLiveData<Int>(2)
    val matrixBRows: LiveData<Int> = _matrixBRows

    private val _matrixBCols = MutableLiveData<Int>(2)
    val matrixBCols: LiveData<Int> = _matrixBCols

    private val _matrixA = MutableLiveData<Matrix>()
    val matrixA: LiveData<Matrix> = _matrixA

    private val _matrixB = MutableLiveData<Matrix>()
    val matrixB: LiveData<Matrix> = _matrixB

    private val _matrixResult = MutableLiveData<MatrixResult?>()
    val matrixResult: LiveData<MatrixResult?> = _matrixResult

    private val _needsTwoMatrices = MutableLiveData<Boolean>(true)
    val needsTwoMatrices: LiveData<Boolean> = _needsTwoMatrices

    private val _matrixExponent = MutableLiveData<Int>(2)
    val matrixExponent: LiveData<Int> = _matrixExponent

    private val _showExponentInput = MutableLiveData<Boolean>(false)
    val showExponentInput: LiveData<Boolean> = _showExponentInput

    // UI State
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _showSteps = MutableLiveData<Boolean>(false)
    val showSteps: LiveData<Boolean> = _showSteps

    init {
        initializeSPLMatrix()
        initializeMatrices()
    }

    fun setOperationMode(mode: OperationMode) {
        _currentMode.value = mode
        clearResults()
    }

    fun setSPLMethod(method: SPLMethod) {
        _splMethod.value = method
        clearSPLResult()
    }

    fun setSPLSize(size: Int) {
        if (size in 2..20) {
            _splSize.value = size
            initializeSPLMatrix()
            clearSPLResult()

            if (size > 10) {
                _errorMessage.value = "Matrix besar (>10x10) mungkin memerlukan waktu komputasi lebih lama"
            } else {
                _errorMessage.value = null
            }
        } else {
            _errorMessage.value = "Ukuran SPL tidak valid (harus 2-20)"
        }
    }

    private fun initializeSPLMatrix() {
        val size = _splSize.value ?: 3
        _splMatrix.value = Matrix(size, size)
        _splConstants.value = Array(size) { 0.0 }
    }

    fun updateSPLMatrixElement(row: Int, col: Int, value: Double) {
        val matrix = _splMatrix.value ?: return
        matrix.set(row, col, value)
        _splMatrix.value = matrix
        clearSPLResult()
    }

    fun updateSPLConstant(index: Int, value: Double) {
        val constants = _splConstants.value ?: return
        constants[index] = value
        _splConstants.value = constants
        clearSPLResult()
    }

    fun solveSPL() {
        clearSPLResult()

        val matrix = _splMatrix.value
        val constants = _splConstants.value
        val method = _splMethod.value

        if (matrix == null || constants == null || method == null) {
            _errorMessage.value = "Data tidak lengkap"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        try {
            val augmentedMatrix = AugmentedMatrix(matrix, constants)
            val solution = repository.solveSPL(augmentedMatrix, method)
            _splSolution.value = solution

            if (solution.solutionType == SolutionType.NO_SOLUTION) {
                _errorMessage.value = solution.message
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun setMatrixOperation(operation: MatrixOperation) {
        _matrixOperation.value = operation

        _showMultiplicationMethods.value = (operation == MatrixOperation.MULTIPLICATION)
        _showExponentInput.value = (operation == MatrixOperation.EXPONENTIATION)

        android.util.Log.d("MatrixViewModel", "Operation: $operation, showExponentInput: ${operation == MatrixOperation.EXPONENTIATION}")

        _needsTwoMatrices.value = when (operation) {
            MatrixOperation.ADDITION, MatrixOperation.SUBTRACTION, MatrixOperation.MULTIPLICATION -> true
            MatrixOperation.INVERSE, MatrixOperation.DETERMINANT, MatrixOperation.EXPONENTIATION -> false
        }
        clearMatrixResult()

        // Update matrix B dimensions for multiplication
        if (operation == MatrixOperation.MULTIPLICATION) {
            val aRows = _matrixARows.value ?: 2
            val aCols = _matrixACols.value ?: 2
            _matrixBRows.value = aCols
            initializeMatrices()
        }
    }

    fun setMultiplicationMethod(method: MultiplicationMethod) {
        _multiplicationMethod.value = method
        clearMatrixResult()
    }

    fun setMatrixExponent(exponent: Int) {
        if (exponent >= 0) {
            _matrixExponent.value = exponent
            clearMatrixResult()
        } else {
            _errorMessage.value = "Eksponen harus bilangan bulat non-negatif"
        }
    }

    fun setMatrixASize(rows: Int, cols: Int) {
        if (rows in 1..10 && cols in 1..10) {
            _matrixARows.value = rows
            _matrixACols.value = cols

            if (_matrixOperation.value == MatrixOperation.MULTIPLICATION) {
                _matrixBRows.value = cols
            }

            initializeMatrices()
            clearMatrixResult()
        } else {
            _errorMessage.value = "Ukuran matrix tidak valid (harus 1-10)"
        }
    }

    fun setMatrixBSize(rows: Int, cols: Int) {
        if (rows in 1..10 && cols in 1..10) {
            _matrixBRows.value = rows
            _matrixBCols.value = cols
            initializeMatrices()
            clearMatrixResult()
        } else {
            _errorMessage.value = "Ukuran matrix tidak valid (harus 1-10)"
        }
    }

    private fun initializeMatrices() {
        val aRows = _matrixARows.value ?: 2
        val aCols = _matrixACols.value ?: 2
        val bRows = _matrixBRows.value ?: 2
        val bCols = _matrixBCols.value ?: 2

        _matrixA.value = Matrix(aRows, aCols)
        _matrixB.value = Matrix(bRows, bCols)
    }

    fun updateMatrixAElement(row: Int, col: Int, value: Double) {
        val matrix = _matrixA.value ?: return
        matrix.set(row, col, value)
        _matrixA.value = matrix
        clearMatrixResult()
    }

    fun updateMatrixBElement(row: Int, col: Int, value: Double) {
        val matrix = _matrixB.value ?: return
        matrix.set(row, col, value)
        _matrixB.value = matrix
        clearMatrixResult()
    }

    fun performMatrixOperation() {
        clearMatrixResult()

        val matrixA = _matrixA.value
        val operation = _matrixOperation.value

        if (matrixA == null || operation == null) {
            _errorMessage.value = "Data tidak lengkap"
            return
        }

        val matrixB = if (_needsTwoMatrices.value == true) _matrixB.value else null
        val multiplicationMethod = if (operation == MatrixOperation.MULTIPLICATION) {
            _multiplicationMethod.value
        } else {
            null
        }
        val exponent = if (operation == MatrixOperation.EXPONENTIATION) {
            _matrixExponent.value
        } else {
            null
        }

        if (_needsTwoMatrices.value == true && matrixB == null) {
            _errorMessage.value = "Matrix B diperlukan untuk operasi ini"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        try {
            val result = repository.performMatrixOperation(matrixA, matrixB, operation, multiplicationMethod, exponent)
            _matrixResult.value = result

            if (!result.success) {
                _errorMessage.value = result.message
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleSteps() {
        _showSteps.value = !(_showSteps.value ?: false)
    }

    fun clearResults() {
        clearSPLResult()
        clearMatrixResult()
    }

    fun clearAll() {
        when (_currentMode.value) {
            OperationMode.SPL -> {
                _splSize.value = 3
                initializeSPLMatrix()
                clearSPLResult()
            }
            OperationMode.MATRIX_OPERATIONS -> {
                _matrixARows.value = 2
                _matrixACols.value = 2
                _matrixBRows.value = 2
                _matrixBCols.value = 2
                _multiplicationMethod.value = MultiplicationMethod.BRUTE_FORCE
                _matrixExponent.value = 2
                initializeMatrices()
                clearMatrixResult()
            }
            else -> {
            }
        }
    }

    private fun clearSPLResult() {
        _splSolution.value = null
        _showSteps.value = false
        _errorMessage.value = null
    }

    private fun clearMatrixResult() {
        _matrixResult.value = null
        _showSteps.value = false
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

enum class OperationMode {
    SPL,
    MATRIX_OPERATIONS
}