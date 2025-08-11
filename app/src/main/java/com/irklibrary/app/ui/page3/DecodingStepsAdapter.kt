package com.irklibrary.app.ui.page3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.DecodingStep

class DecodingStepsAdapter(
    private var steps: List<DecodingStep> = emptyList()
) : RecyclerView.Adapter<DecodingStepsAdapter.DecodingViewHolder>() {

    fun updateSteps(newSteps: List<DecodingStep>) {
        steps = newSteps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecodingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_decoding_step, parent, false)
        return DecodingViewHolder(view)
    }

    override fun onBindViewHolder(holder: DecodingViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount(): Int = steps.size

    class DecodingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStepNumber: TextView = itemView.findViewById(R.id.tv_decoding_step_number)
        private val tvCurrentBits: TextView = itemView.findViewById(R.id.tv_current_bits)
        private val tvMatchedCode: TextView = itemView.findViewById(R.id.tv_matched_code)
        private val tvDecodedChar: TextView = itemView.findViewById(R.id.tv_decoded_char)
        private val tvRemainingBits: TextView = itemView.findViewById(R.id.tv_remaining_bits)
        private val tvCurrentDecoded: TextView = itemView.findViewById(R.id.tv_current_decoded)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_decoding_description)

        fun bind(step: DecodingStep) {
            tvStepNumber.text = "Step ${step.stepNumber}"
            tvCurrentBits.text = step.currentBits
            tvMatchedCode.text = step.matchedCode

            val displayChar = if (step.decodedCharacter == ' ') "'_' (spasi)" else "'${step.decodedCharacter}'"
            tvDecodedChar.text = displayChar

            tvRemainingBits.text = if (step.remainingBits.isEmpty()) "Selesai" else step.remainingBits
            tvCurrentDecoded.text = step.currentDecoded
            tvDescription.text = step.description
        }
    }
}