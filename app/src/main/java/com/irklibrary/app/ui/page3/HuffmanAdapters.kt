package com.irklibrary.app.ui.page3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.HuffmanCode
import com.irklibrary.app.data.models.HuffmanNode
import com.irklibrary.app.data.models.HuffmanTreeStep

class HuffmanNodeAdapter(
    private var nodes: List<HuffmanNode> = emptyList(),
    private val highlightStyle: NodeHighlightStyle = NodeHighlightStyle.NONE
) : RecyclerView.Adapter<HuffmanNodeAdapter.NodeViewHolder>() {

    enum class NodeHighlightStyle {
        NONE, SELECTED, NEW_NODE
    }

    fun updateNodes(newNodes: List<HuffmanNode>) {
        nodes = newNodes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_huffman_node, parent, false)
        return NodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.bind(nodes[position], highlightStyle)
    }

    override fun getItemCount(): Int = nodes.size

    class NodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNodeCharacter: TextView = itemView.findViewById(R.id.tv_node_character)
        private val tvNodeFrequency: TextView = itemView.findViewById(R.id.tv_node_frequency)

        fun bind(node: HuffmanNode, highlightStyle: NodeHighlightStyle) {
            tvNodeCharacter.text = when {
                node.character != null -> {
                    node.nodeId
                }
                else -> {
                    if (node.nodeId.length <= 3) {
                        node.nodeId
                    } else {
                        "${node.nodeId.take(2)}..."
                    }
                }
            }

            tvNodeFrequency.text = "(${node.frequency})"

            when (highlightStyle) {
                NodeHighlightStyle.SELECTED -> {
                    itemView.alpha = 0.7f
                }
                NodeHighlightStyle.NEW_NODE -> {
                    itemView.alpha = 1.0f
                }
                NodeHighlightStyle.NONE -> {
                    itemView.alpha = 1.0f
                }
            }
        }
    }
}

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
        private val rvTreeCollection: RecyclerView = itemView.findViewById(R.id.rv_tree_collection)
        private val rvPriorityQueueNodes: RecyclerView = itemView.findViewById(R.id.rv_priority_queue_nodes)
        private val llNodesSection: View = itemView.findViewById(R.id.ll_nodes_section)

        private val treeCollectionAdapter = TreeCollectionAdapter()
        private val priorityQueueAdapter = HuffmanNodeAdapter(highlightStyle = HuffmanNodeAdapter.NodeHighlightStyle.NONE)

        init {
            setupRecyclerViews()
        }

        private fun setupRecyclerViews() {
            rvTreeCollection.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = treeCollectionAdapter
            }

            rvPriorityQueueNodes.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = priorityQueueAdapter
            }
        }

        fun bind(step: HuffmanTreeStep) {
            tvStepNumber.text = step.stepNumber.toString()
            tvStepDescription.text = step.description

            val isLastStep = step.currentTrees.size == 1 &&
                    step.description.contains("sisi-sisi kiri") &&
                    step.description.contains("label")

            treeCollectionAdapter.updateTrees(step.currentTrees, isLastStep)

            priorityQueueAdapter.updateNodes(step.availableNodes)
            llNodesSection.visibility = View.GONE
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
            val displayChar = if (code.character == ' ') "_" else code.character.toString()
            tvCharacter.text = displayChar

            tvFrequency.text = code.frequency.toString()
            tvHuffmanCode.text = code.code

            itemView.setOnClickListener {
                onCharacterClick(code.character)
            }
        }
    }
}