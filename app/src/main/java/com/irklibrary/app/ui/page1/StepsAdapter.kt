package com.irklibrary.app.ui.page1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.*

class StepsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var steps: List<Any> = emptyList()

    companion object {
        private const val TYPE_MATRIX_STEP = 0
        private const val TYPE_CRAMER_STEP = 1
    }

    fun updateSteps(newSteps: List<Any>) {
        steps = newSteps
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (steps[position]) {
            is MatrixStep -> TYPE_MATRIX_STEP
            is CramerStep -> TYPE_CRAMER_STEP
            else -> TYPE_MATRIX_STEP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MATRIX_STEP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_matrix_step, parent, false)
                MatrixStepViewHolder(view)
            }
            TYPE_CRAMER_STEP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_cramer_step, parent, false)
                CramerStepViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MatrixStepViewHolder -> holder.bind(steps[position] as MatrixStep)
            is CramerStepViewHolder -> holder.bind(steps[position] as CramerStep)
        }
    }

    override fun getItemCount(): Int = steps.size

    class MatrixStepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textStepNumber: TextView = itemView.findViewById(R.id.textStepNumber)
        private val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        private val textOperation: TextView = itemView.findViewById(R.id.textOperation)
        private val tableMatrix: TableLayout = itemView.findViewById(R.id.tableMatrix)

        fun bind(step: MatrixStep) {
            textStepNumber.text = step.stepNumber.toString()
            textDescription.text = step.description

            if (step.operation.isNotEmpty()) {
                textOperation.text = step.operation
                textOperation.visibility = View.VISIBLE
            } else {
                textOperation.visibility = View.GONE
            }

            displayMatrix(step.matrix)
        }

        private fun displayMatrix(matrix: Matrix) {
            tableMatrix.removeAllViews()

            for (i in 0 until matrix.rows) {
                val row = TableRow(itemView.context)

                for (j in 0 until matrix.cols) {
                    val textView = TextView(itemView.context)
                    val params = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(6, 6, 6, 6)
                    }

                    textView.layoutParams = params
                    textView.text = formatNumber(matrix.get(i, j))
                    textView.textSize = 12f
                    textView.setPadding(8, 8, 8, 8)
                    textView.setBackgroundResource(R.drawable.square_background)
                    textView.gravity = android.view.Gravity.CENTER
                    textView.minWidth = 60

                    row.addView(textView)
                }

                tableMatrix.addView(row)
            }
        }

        private fun formatNumber(number: Double): String {
            val normalizedNumber = if (kotlin.math.abs(number) < 1e-10) 0.0 else number

            return if (normalizedNumber == normalizedNumber.toInt().toDouble()) {
                normalizedNumber.toInt().toString()
            } else {
                String.format("%.2f", normalizedNumber).trimEnd('0').trimEnd('.')
            }
        }
    }

    class CramerStepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textStepNumber: TextView = itemView.findViewById(R.id.textStepNumber)
        private val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        private val textVariable: TextView = itemView.findViewById(R.id.textVariable)
        private val textCalculation: TextView = itemView.findViewById(R.id.textCalculation)
        private val textDeterminant: TextView = itemView.findViewById(R.id.textDeterminant)
        private val tableMatrix: TableLayout = itemView.findViewById(R.id.tableMatrix)

        fun bind(step: CramerStep) {
            textStepNumber.text = step.stepNumber.toString()
            textDescription.text = step.description

            if (step.variable.isNotEmpty() && !step.description.contains("determinan", true)) {
                textVariable.text = "Untuk ${step.variable}:"
                textVariable.visibility = View.VISIBLE
            } else {
                textVariable.visibility = View.GONE
            }

            if (step.calculation.isNotEmpty()) {
                textCalculation.text = step.calculation
                textCalculation.visibility = View.VISIBLE
            } else {
                textCalculation.visibility = View.GONE
            }

            if (step.determinant != null) {
                when {
                    step.description.contains("Hasil perhitungan", true) -> {
                        textDeterminant.text = "${step.variable} = ${formatNumber(step.determinant)}"
                    }
                    step.description.contains("Hasil determinan", true) -> {
                        textDeterminant.text = "Determinan = ${formatNumber(step.determinant)}"
                    }
                    step.variable.isNotEmpty() && step.description.contains("Hitung nilai", true) -> {
                        textDeterminant.text = "${step.variable} = ${formatNumber(step.determinant)}"
                    }
                    else -> {
                        textDeterminant.text = "Determinan = ${formatNumber(step.determinant)}"
                    }
                }
                textDeterminant.visibility = View.VISIBLE
                textDeterminant.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.primary)
                )
            } else {
                textDeterminant.visibility = View.GONE
            }

            if (step.matrix != null) {
                displayMatrix(step.matrix)
                tableMatrix.visibility = View.VISIBLE
            } else {
                tableMatrix.visibility = View.GONE
            }
        }

        private fun displayMatrix(matrix: Matrix) {
            tableMatrix.removeAllViews()

            for (i in 0 until matrix.rows) {
                val row = TableRow(itemView.context)

                for (j in 0 until matrix.cols) {
                    val textView = TextView(itemView.context)
                    val params = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(6, 6, 6, 6)
                    }

                    textView.layoutParams = params
                    textView.text = formatNumber(matrix.get(i, j))
                    textView.textSize = 12f
                    textView.setPadding(8, 8, 8, 8)
                    textView.setBackgroundResource(R.drawable.square_background)
                    textView.gravity = android.view.Gravity.CENTER
                    textView.minWidth = 60

                    row.addView(textView)
                }

                tableMatrix.addView(row)
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
    }
}