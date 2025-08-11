package com.irklibrary.app.ui.page3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.HuffmanNode

class TreeCollectionAdapter(
    private var trees: List<HuffmanNode> = emptyList(),
    private var showLabels: Boolean = false
) : RecyclerView.Adapter<TreeCollectionAdapter.TreeViewHolder>() {

    fun updateTrees(newTrees: List<HuffmanNode>, showLabels: Boolean = false) {
        trees = newTrees
        this.showLabels = showLabels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tree_collection, parent, false)
        return TreeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        holder.bind(trees[position], showLabels)
    }

    override fun getItemCount(): Int = trees.size

    class TreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val miniTreeView: MiniTreeView = itemView.findViewById(R.id.mini_tree_view)

        fun bind(tree: HuffmanNode, showLabels: Boolean) {
            miniTreeView.setTree(tree, showLabels)
        }
    }
}