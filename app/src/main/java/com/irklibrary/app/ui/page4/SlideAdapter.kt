package com.irklibrary.app.ui.page4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.SlideWithMatkulModel

class SlideAdapter(
    private val onSlideClick: (SlideWithMatkulModel) -> Unit
) : ListAdapter<SlideWithMatkulModel, SlideAdapter.SlideViewHolder>(SlideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slide_card, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_slide_title)
        private val matkulTextView: TextView = itemView.findViewById(R.id.tv_matkul)
        private val matkulCodeTextView: TextView = itemView.findViewById(R.id.tv_matkul_code)
        private val seeButton: Button = itemView.findViewById(R.id.btn_see_slide)

        fun bind(slide: SlideWithMatkulModel) {
            titleTextView.text = slide.judul
            matkulTextView.text = slide.matkul.substringAfter(" ") // Hapus kode mata kuliah
            matkulCodeTextView.text = slide.matkulCode

            seeButton.setOnClickListener {
                onSlideClick(slide)
            }

            itemView.setOnClickListener {
                onSlideClick(slide)
            }
        }
    }

    private class SlideDiffCallback : DiffUtil.ItemCallback<SlideWithMatkulModel>() {
        override fun areItemsTheSame(
            oldItem: SlideWithMatkulModel,
            newItem: SlideWithMatkulModel
        ): Boolean {
            return oldItem.link == newItem.link
        }

        override fun areContentsTheSame(
            oldItem: SlideWithMatkulModel,
            newItem: SlideWithMatkulModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}