package com.irklibrary.app.ui.page1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.irklibrary.app.R
import com.irklibrary.app.data.models.*

class MatrixSPLFragment : Fragment() {

    private lateinit var viewModel: MatrixSPLViewModel
    private lateinit var stepsAdapter: StepsAdapter

    // UI Elements
    private lateinit var btnSPLMode: MaterialButton
    private lateinit var btnMatrixMode: MaterialButton
    private lateinit var layoutSPL: LinearLayout
    private lateinit var layoutMatrixOps: LinearLayout

    // SPL UI Elements
    private lateinit var btnGaussJordan: MaterialButton
    private lateinit var btnCramer: MaterialButton
    private lateinit var editSPLSize: TextInputEditText
    private lateinit var tableSPLMatrix: TableLayout
    private lateinit var layoutSPLConstants: LinearLayout

    // Matrix Operations UI Elements
    private lateinit var btnAddition: MaterialButton
    private lateinit var btnSubtraction: MaterialButton
    private lateinit var btnMultiplication: MaterialButton
    private lateinit var btnInverse: MaterialButton
    private lateinit var btnDeterminant: MaterialButton
    private lateinit var btnExponentiation: MaterialButton
    private lateinit var editMatrixARows: TextInputEditText
    private lateinit var editMatrixACols: TextInputEditText
    private lateinit var editMatrixBRows: TextInputEditText
    private lateinit var editMatrixBCols: TextInputEditText
    private lateinit var layoutMatrixBSize: LinearLayout
    private lateinit var tableMatrixA: TableLayout
    private lateinit var tableMatrixB: TableLayout
    private lateinit var cardMatrixB: MaterialCardView

    // Multiplication Method UI Elements
    private lateinit var cardMultiplicationMethods: MaterialCardView
    private lateinit var btnBruteForce: MaterialButton
    private lateinit var btnDivideConquer: MaterialButton

    // Exponentiation UI Elements
    private lateinit var cardExponentInput: MaterialCardView
    private lateinit var editExponent: TextInputEditText

    // Common UI Elements
    private lateinit var btnClear: MaterialButton
    private lateinit var btnSolve: MaterialButton
    private lateinit var progressLoading: LinearProgressIndicator
    private lateinit var cardError: MaterialCardView
    private lateinit var textError: TextView
    private lateinit var layoutResult: LinearLayout
    private lateinit var textResult: TextView
    private lateinit var scrollResultMatrix: HorizontalScrollView
    private lateinit var tableResultMatrix: TableLayout
    private lateinit var btnShowSteps: MaterialButton
    private lateinit var cardSteps: MaterialCardView
    private lateinit var recyclerSteps: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matrixspl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initViewModel()
        setupUI()
        observeViewModel()
    }

    private fun initViews(view: View) {
        // Mode buttons
        btnSPLMode = view.findViewById(R.id.btnSPLMode)
        btnMatrixMode = view.findViewById(R.id.btnMatrixMode)
        layoutSPL = view.findViewById(R.id.layoutSPL)
        layoutMatrixOps = view.findViewById(R.id.layoutMatrixOps)

        // SPL UI
        btnGaussJordan = view.findViewById(R.id.btnGaussJordan)
        btnCramer = view.findViewById(R.id.btnCramer)
        editSPLSize = view.findViewById(R.id.editSPLSize)
        tableSPLMatrix = view.findViewById(R.id.tableSPLMatrix)
        layoutSPLConstants = view.findViewById(R.id.layoutSPLConstants)

        // Matrix Operations UI
        btnAddition = view.findViewById(R.id.btnAddition)
        btnSubtraction = view.findViewById(R.id.btnSubtraction)
        btnMultiplication = view.findViewById(R.id.btnMultiplication)
        btnInverse = view.findViewById(R.id.btnInverse)
        btnDeterminant = view.findViewById(R.id.btnDeterminant)
        btnExponentiation = view.findViewById(R.id.btnExponentiation)
        editMatrixARows = view.findViewById(R.id.editMatrixARows)
        editMatrixACols = view.findViewById(R.id.editMatrixACols)
        editMatrixBRows = view.findViewById(R.id.editMatrixBRows)
        editMatrixBCols = view.findViewById(R.id.editMatrixBCols)
        layoutMatrixBSize = view.findViewById(R.id.layoutMatrixBSize)
        tableMatrixA = view.findViewById(R.id.tableMatrixA)
        tableMatrixB = view.findViewById(R.id.tableMatrixB)
        cardMatrixB = view.findViewById(R.id.cardMatrixB)

        // Multiplication Method UI
        cardMultiplicationMethods = view.findViewById(R.id.cardMultiplicationMethods)
        btnBruteForce = view.findViewById(R.id.btnBruteForce)
        btnDivideConquer = view.findViewById(R.id.btnDivideConquer)

        // Exponentiation UI
        cardExponentInput = view.findViewById(R.id.cardExponentInput)
        editExponent = view.findViewById(R.id.editExponent)

        // Common UI
        btnClear = view.findViewById(R.id.btnClear)
        btnSolve = view.findViewById(R.id.btnSolve)
        progressLoading = view.findViewById(R.id.progressLoading)
        cardError = view.findViewById(R.id.cardError)
        textError = view.findViewById(R.id.textError)
        layoutResult = view.findViewById(R.id.layoutResult)
        textResult = view.findViewById(R.id.textResult)
        scrollResultMatrix = view.findViewById(R.id.scrollResultMatrix)
        tableResultMatrix = view.findViewById(R.id.tableResultMatrix)
        btnShowSteps = view.findViewById(R.id.btnShowSteps)
        cardSteps = view.findViewById(R.id.cardSteps)
        recyclerSteps = view.findViewById(R.id.recyclerSteps)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MatrixSPLViewModel::class.java]
    }

    private fun setupUI() {
        setupModeButtons()
        setupSPLUI()
        setupMatrixOpsUI()
        setupMultiplicationMethodsUI()
        setupExponentiationUI()
        setupCommonUI()
        setupRecyclerView()
    }

    private fun setupModeButtons() {
        btnSPLMode.setOnClickListener {
            viewModel.setOperationMode(OperationMode.SPL)
        }

        btnMatrixMode.setOnClickListener {
            viewModel.setOperationMode(OperationMode.MATRIX_OPERATIONS)
        }
    }

    private fun setupSPLUI() {
        btnGaussJordan.setOnClickListener {
            viewModel.setSPLMethod(SPLMethod.GAUSS_JORDAN)
        }
        btnCramer.setOnClickListener {
            viewModel.setSPLMethod(SPLMethod.CRAMER)
        }

        // Size input with number restriction
        editSPLSize.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        editSPLSize.filters = arrayOf(createSizeInputFilter())
        editSPLSize.addTextChangedListener(createIntTextWatcher { size ->
            if (size in 2..20) {
                viewModel.setSPLSize(size)
            }
        })
    }

    private fun setupMatrixOpsUI() {
        btnAddition.setOnClickListener {
            viewModel.setMatrixOperation(MatrixOperation.ADDITION)
        }
        btnSubtraction.setOnClickListener {
            viewModel.setMatrixOperation(MatrixOperation.SUBTRACTION)
        }
        btnMultiplication.setOnClickListener {
            viewModel.setMatrixOperation(MatrixOperation.MULTIPLICATION)
        }
        btnInverse.setOnClickListener {
            viewModel.setMatrixOperation(MatrixOperation.INVERSE)
        }
        btnDeterminant.setOnClickListener {
            viewModel.setMatrixOperation(MatrixOperation.DETERMINANT)
        }
        btnExponentiation.setOnClickListener {
            viewModel.setMatrixOperation(MatrixOperation.EXPONENTIATION)
        }

        editMatrixARows.filters = arrayOf(createMatrixSizeInputFilter())
        editMatrixACols.filters = arrayOf(createMatrixSizeInputFilter())
        editMatrixBRows.filters = arrayOf(createMatrixSizeInputFilter())
        editMatrixBCols.filters = arrayOf(createMatrixSizeInputFilter())

        editMatrixARows.addTextChangedListener(createMatrixSizeWatcher { rows ->
            val cols = editMatrixACols.text.toString().toIntOrNull() ?: 2
            if (rows in 1..10) {
                viewModel.setMatrixASize(rows, cols)
            }
        })

        editMatrixACols.addTextChangedListener(createMatrixSizeWatcher { cols ->
            val rows = editMatrixARows.text.toString().toIntOrNull() ?: 2
            if (cols in 1..10) {
                viewModel.setMatrixASize(rows, cols)
            }
        })

        editMatrixBRows.addTextChangedListener(createMatrixSizeWatcher { rows ->
            val cols = editMatrixBCols.text.toString().toIntOrNull() ?: 2
            if (rows in 1..10) {
                viewModel.setMatrixBSize(rows, cols)
            }
        })

        editMatrixBCols.addTextChangedListener(createMatrixSizeWatcher { cols ->
            val rows = editMatrixBRows.text.toString().toIntOrNull() ?: 2
            if (cols in 1..10) {
                viewModel.setMatrixBSize(rows, cols)
            }
        })
    }

    private fun setupMultiplicationMethodsUI() {
        btnBruteForce.setOnClickListener {
            viewModel.setMultiplicationMethod(MultiplicationMethod.BRUTE_FORCE)
        }

        btnDivideConquer.setOnClickListener {
            viewModel.setMultiplicationMethod(MultiplicationMethod.DIVIDE_AND_CONQUER)
        }
    }

    private fun setupExponentiationUI() {
        // Exponent input with number restriction
        editExponent.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        editExponent.filters = arrayOf(createExponentInputFilter())
        editExponent.addTextChangedListener(createIntTextWatcher { exponent ->
            if (exponent >= 0) {
                viewModel.setMatrixExponent(exponent)
            }
        })
    }

    private fun setupCommonUI() {
        btnSolve.setOnClickListener {
            when (viewModel.currentMode.value) {
                OperationMode.SPL -> viewModel.solveSPL()
                OperationMode.MATRIX_OPERATIONS -> viewModel.performMatrixOperation()
                else -> {}
            }
        }

        btnClear.setOnClickListener {
            viewModel.clearAll()
        }

        btnShowSteps.setOnClickListener {
            viewModel.toggleSteps()
        }
    }

    private fun setupRecyclerView() {
        stepsAdapter = StepsAdapter()
        recyclerSteps.layoutManager = LinearLayoutManager(requireContext())
        recyclerSteps.adapter = stepsAdapter
    }

    private fun observeViewModel() {
        viewModel.currentMode.observe(viewLifecycleOwner) { mode ->
            updateModeUI(mode)
        }

        viewModel.splMethod.observe(viewLifecycleOwner) { method ->
            updateSPLMethodUI(method)
        }

        viewModel.splSize.observe(viewLifecycleOwner) { size ->
            createSPLMatrixInput(size)
        }

        viewModel.splSolution.observe(viewLifecycleOwner) { solution ->
            solution?.let { displaySPLResult(it) }
        }

        viewModel.matrixOperation.observe(viewLifecycleOwner) { operation ->
            updateMatrixOperationUI(operation)
        }

        viewModel.showMultiplicationMethods.observe(viewLifecycleOwner) { show ->
            cardMultiplicationMethods.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.multiplicationMethod.observe(viewLifecycleOwner) { method ->
            updateMultiplicationMethodUI(method)
        }

        viewModel.showExponentInput.observe(viewLifecycleOwner) { show ->
            cardExponentInput.visibility = if (show) View.VISIBLE else View.GONE
            android.util.Log.d("MatrixFragment", "showExponentInput: $show")
        }

        viewModel.matrixExponent.observe(viewLifecycleOwner) { exponent ->
            if (editExponent.text.toString().toIntOrNull() != exponent) {
                editExponent.setText(exponent.toString())
            }
        }

        viewModel.matrixARows.observe(viewLifecycleOwner) { rows ->
            val cols = viewModel.matrixACols.value ?: 2
            createMatrixAInput(rows, cols)
        }

        viewModel.matrixACols.observe(viewLifecycleOwner) { cols ->
            val rows = viewModel.matrixARows.value ?: 2
            createMatrixAInput(rows, cols)
        }

        viewModel.matrixBRows.observe(viewLifecycleOwner) { rows ->
            val cols = viewModel.matrixBCols.value ?: 2
            createMatrixBInput(rows, cols)
        }

        viewModel.matrixBCols.observe(viewLifecycleOwner) { cols ->
            val rows = viewModel.matrixBRows.value ?: 2
            createMatrixBInput(rows, cols)
        }

        viewModel.needsTwoMatrices.observe(viewLifecycleOwner) { needsTwo ->
            cardMatrixB.visibility = if (needsTwo) View.VISIBLE else View.GONE
            layoutMatrixBSize.visibility = if (needsTwo) View.VISIBLE else View.GONE
        }

        viewModel.matrixResult.observe(viewLifecycleOwner) { result ->
            result?.let { displayMatrixResult(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSolve.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                textError.text = error
                cardError.visibility = View.VISIBLE
            } else {
                cardError.visibility = View.GONE
            }
        }

        viewModel.showSteps.observe(viewLifecycleOwner) { showSteps ->
            cardSteps.visibility = if (showSteps) View.VISIBLE else View.GONE
            btnShowSteps.text = if (showSteps) getString(R.string.hide_steps) else getString(R.string.show_steps)
        }
    }

    private fun updateModeUI(mode: OperationMode) {
        when (mode) {
            OperationMode.SPL -> {
                updateButtonSelection(btnSPLMode, btnMatrixMode)
                layoutSPL.visibility = View.VISIBLE
                layoutMatrixOps.visibility = View.GONE
            }
            OperationMode.MATRIX_OPERATIONS -> {
                updateButtonSelection(btnMatrixMode, btnSPLMode)
                layoutSPL.visibility = View.GONE
                layoutMatrixOps.visibility = View.VISIBLE
            }
        }
        hideResults()
    }

    private fun updateSPLMethodUI(method: SPLMethod) {
        when (method) {
            SPLMethod.GAUSS_JORDAN -> updateButtonSelection(btnGaussJordan, btnCramer)
            SPLMethod.CRAMER -> updateButtonSelection(btnCramer, btnGaussJordan)
        }
    }

    private fun updateMatrixOperationUI(operation: MatrixOperation) {
        val operationButtons = listOf(btnAddition, btnSubtraction, btnMultiplication, btnInverse, btnDeterminant, btnExponentiation)
        operationButtons.forEach { button ->
            button.isSelected = false
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            button.strokeWidth = 2
            button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.on_surface)
        }

        val selectedButton = when (operation) {
            MatrixOperation.ADDITION -> btnAddition
            MatrixOperation.SUBTRACTION -> btnSubtraction
            MatrixOperation.MULTIPLICATION -> btnMultiplication
            MatrixOperation.INVERSE -> btnInverse
            MatrixOperation.DETERMINANT -> btnDeterminant
            MatrixOperation.EXPONENTIATION -> btnExponentiation
        }

        selectedButton.isSelected = true
        selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_primary))
        selectedButton.strokeWidth = 0
    }

    private fun updateMultiplicationMethodUI(method: MultiplicationMethod) {
        when (method) {
            MultiplicationMethod.BRUTE_FORCE -> updateButtonSelection(btnBruteForce, btnDivideConquer)
            MultiplicationMethod.DIVIDE_AND_CONQUER -> updateButtonSelection(btnDivideConquer, btnBruteForce)
        }
    }

    private fun updateButtonSelection(selected: MaterialButton, deselected: MaterialButton) {
        selected.isSelected = true
        selected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
        selected.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_primary))
        selected.strokeWidth = 0

        deselected.isSelected = false
        deselected.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        deselected.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        deselected.strokeWidth = 2
        deselected.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.on_surface)
    }

    private fun createSPLMatrixInput(size: Int) {
        tableSPLMatrix.removeAllViews()
        layoutSPLConstants.removeAllViews()

        for (i in 0 until size) {
            val row = TableRow(requireContext())
            for (j in 0 until size) {
                val editText = createMatrixEditText()
                editText.addTextChangedListener(createTextWatcher { value ->
                    viewModel.updateSPLMatrixElement(i, j, value)
                })
                row.addView(editText)
            }
            tableSPLMatrix.addView(row)
        }

        for (i in 0 until size) {
            val editText = createMatrixEditText()
            editText.hint = "0"
            editText.addTextChangedListener(createTextWatcher { value ->
                viewModel.updateSPLConstant(i, value)
            })
            layoutSPLConstants.addView(editText)
        }
    }

    private fun createMatrixAInput(rows: Int, cols: Int) {
        tableMatrixA.removeAllViews()

        for (i in 0 until rows) {
            val row = TableRow(requireContext())
            for (j in 0 until cols) {
                val editText = createMatrixEditText()
                editText.addTextChangedListener(createTextWatcher { value ->
                    viewModel.updateMatrixAElement(i, j, value)
                })
                row.addView(editText)
            }
            tableMatrixA.addView(row)
        }
    }

    private fun createMatrixBInput(rows: Int, cols: Int) {
        tableMatrixB.removeAllViews()

        for (i in 0 until rows) {
            val row = TableRow(requireContext())
            for (j in 0 until cols) {
                val editText = createMatrixEditText()
                editText.addTextChangedListener(createTextWatcher { value ->
                    viewModel.updateMatrixBElement(i, j, value)
                })
                row.addView(editText)
            }
            tableMatrixB.addView(row)
        }
    }

    private fun createSizeInputFilter(): android.text.InputFilter {
        return android.text.InputFilter { source, start, end, dest, dstart, dend ->
            try {
                val newText = dest.toString().substring(0, dstart) +
                        source.subSequence(start, end) +
                        dest.toString().substring(dend)

                if (newText.isEmpty()) {
                    return@InputFilter null
                }

                val number = newText.toIntOrNull()
                if (number != null && number in 1..20) {
                    null
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    private fun createMatrixSizeInputFilter(): android.text.InputFilter {
        return android.text.InputFilter { source, start, end, dest, dstart, dend ->
            try {
                val newText = dest.toString().substring(0, dstart) +
                        source.subSequence(start, end) +
                        dest.toString().substring(dend)

                if (newText.isEmpty()) {
                    return@InputFilter null
                }

                val number = newText.toIntOrNull()
                if (number != null && number in 1..10) {
                    null
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    private fun createExponentInputFilter(): android.text.InputFilter {
        return android.text.InputFilter { source, start, end, dest, dstart, dend ->
            try {
                val newText = dest.toString().substring(0, dstart) +
                        source.subSequence(start, end) +
                        dest.toString().substring(dend)

                if (newText.isEmpty()) {
                    return@InputFilter null
                }

                val number = newText.toIntOrNull()
                if (number != null && number in 0..20) {
                    null
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    private fun createMatrixEditText(): TextInputEditText {
        val editText = TextInputEditText(requireContext())
        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4, 4, 4, 4)
        }
        editText.layoutParams = params
        editText.width = 120
        editText.gravity = android.view.Gravity.CENTER
        editText.textSize = 14f
        editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        editText.hint = getString(R.string.matrix_element_hint)
        editText.setPadding(12, 12, 12, 12)
        editText.setBackgroundResource(R.drawable.square_background)

        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_NORMAL

        editText.filters = arrayOf(createStrictNumberInputFilter())

        return editText
    }

    private fun createStrictNumberInputFilter(): android.text.InputFilter {
        return android.text.InputFilter { source, start, end, dest, dstart, dend ->
            try {
                val newText = dest.toString().substring(0, dstart) +
                        source.subSequence(start, end) +
                        dest.toString().substring(dend)

                if (newText.isEmpty()) {
                    return@InputFilter null
                }

                val allowedChars = "0123456789.-"
                for (char in source) {
                    if (char !in allowedChars) {
                        return@InputFilter ""
                    }
                }

                if (newText == "-") {
                    return@InputFilter null
                }

                if (newText == ".") {
                    return@InputFilter null
                }

                if (newText == "-.") {
                    return@InputFilter null
                }

                val validPatterns = listOf(
                    "^-?\\d*\\.?\\d*$".toRegex(),
                    "^-?\\.\\d+$".toRegex(),
                    "^-?\\d+\\.$".toRegex()
                )

                val isValidPattern = validPatterns.any { it.matches(newText) }

                if (isValidPattern) {
                    val number = newText.toDoubleOrNull()
                    if (number != null && kotlin.math.abs(number) <= 1000000) {
                        null
                    } else if (newText.matches("^-?\\.?\\d*\\.?\\d*$".toRegex())) {
                        null
                    } else {
                        ""
                    }
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    private fun createTextWatcher(onValueChanged: (Double) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toDoubleOrNull() ?: 0.0
                onValueChanged(value)
            }
        }
    }

    private fun createIntTextWatcher(onValueChanged: (Int) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toIntOrNull() ?: 0
                if (value > 0) {
                    onValueChanged(value)
                }
            }
        }
    }

    private fun createMatrixSizeWatcher(onValueChanged: (Int) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toIntOrNull() ?: 0
                if (value in 1..10) {
                    onValueChanged(value)
                }
            }
        }
    }

    private fun displaySPLResult(solution: SPLSolution) {
        layoutResult.visibility = View.VISIBLE
        scrollResultMatrix.visibility = View.GONE

        when (solution.solutionType) {
            SolutionType.UNIQUE -> {
                textResult.text = solution.message
                textResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            }
            SolutionType.INFINITE -> {
                textResult.text = solution.message
                textResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            }
            SolutionType.NO_SOLUTION -> {
                textResult.text = solution.message
                textResult.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            }
        }

        stepsAdapter.updateSteps(solution.steps)

        hideError()
    }

    private fun displayMatrixResult(result: MatrixResult) {
        layoutResult.visibility = View.VISIBLE

        if (result.success) {
            var message = result.message

            if (result.multiplicationMethod != null) {
                val methodText = when (result.multiplicationMethod) {
                    MultiplicationMethod.BRUTE_FORCE -> "Brute Force"
                    MultiplicationMethod.DIVIDE_AND_CONQUER -> "Divide & Conquer"
                }
                message += " (Metode: $methodText)"
            }

            textResult.text = message
            textResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))

            if (result.result != null) {
                scrollResultMatrix.visibility = View.VISIBLE
                displayResultMatrix(result.result)
            } else {
                scrollResultMatrix.visibility = View.GONE
            }
        } else {
            textResult.text = result.message
            textResult.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            scrollResultMatrix.visibility = View.GONE
        }

        stepsAdapter.updateSteps(result.steps)

        hideError()
    }

    private fun displayResultMatrix(matrix: Matrix) {
        tableResultMatrix.removeAllViews()

        for (i in 0 until matrix.rows) {
            val row = TableRow(requireContext())
            for (j in 0 until matrix.cols) {
                val textView = TextView(requireContext())
                val params = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(6, 6, 6, 6)
                }

                textView.layoutParams = params
                textView.text = formatNumber(matrix.get(i, j))
                textView.textSize = 14f
                textView.setPadding(12, 12, 12, 12)
                textView.setBackgroundResource(R.drawable.square_background)
                textView.gravity = android.view.Gravity.CENTER
                textView.minWidth = 80
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface))

                row.addView(textView)
            }
            tableResultMatrix.addView(row)
        }
    }

    private fun formatNumber(number: Double): String {
        val normalizedNumber = if (kotlin.math.abs(number) < 1e-10) 0.0 else number

        return if (normalizedNumber == normalizedNumber.toInt().toDouble()) {
            normalizedNumber.toInt().toString()
        } else {
            String.format("%.3f", normalizedNumber).trimEnd('0').trimEnd('.')
        }
    }

    private fun hideResults() {
        layoutResult.visibility = View.GONE
        cardSteps.visibility = View.GONE
        viewModel.clearResults()
    }

    private fun hideError() {
        cardError.visibility = View.GONE
        viewModel.clearError()
    }
}