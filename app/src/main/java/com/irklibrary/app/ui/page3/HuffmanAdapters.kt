package com.irklibrary.app.ui.page3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.HuffmanCode
import com.irklibrary.app.data.models.HuffmanTreeStep

class ConstructionStepsAdapter(
    private var steps: List<HuffmanTreeStep> = emptyList()
) : RecyclerView.Adapter<ConstructionStepsAdapter.StepViewHolder>() {

    fun updateSteps(newSteps: List<HuffmanTreeStep>) {
        steps = newSteps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_construction_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount(): Int = steps.size

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStepNumber: TextView = itemView.findViewById(R.id.tv_step_number)
        private val tvStepDescription: TextView = itemView.findViewById(R.id.tv_step_description)

        fun bind(step: HuffmanTreeStep) {
            tvStepNumber.text = step.stepNumber.toString()
            tvStepDescription.text = step.description
        }
    }
}

class HuffmanCodeAdapter(
    private var codes: List<HuffmanCode> = emptyList(),
    private val onCharacterClick: (Char) -> Unit = {}
) : RecyclerView.Adapter<HuffmanCodeAdapter.CodeViewHolder>() {

    fun updateCodes(newCodes: List<HuffmanCode>) {
        codes = newCodes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_huffman_code, parent, false)
        return CodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        holder.bind(codes[position], onCharacterClick)
    }

    override fun getItemCount(): Int = codes.size

    class CodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCharacter: TextView = itemView.findViewById(R.id.tv_character)
        private val tvFrequency: TextView = itemView.findViewById(R.id.tv_frequency)
        private val tvHuffmanCode: TextView = itemView.findViewById(R.id.tv_huffman_code)

        fun bind(code: HuffmanCode, onCharacterClick: (Char) -> Unit) {
            tvCharacter.text = when (code.character) {
                ' ' -> "_"
                else -> code.character.toString()
            }

            tvFrequency.text = code.frequency.toString()
            tvHuffmanCode.text = code.code

            itemView.setOnClickListener {
                onCharacterClick(code.character)
            }
        }
    }
}