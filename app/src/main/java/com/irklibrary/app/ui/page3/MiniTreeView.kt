package com.irklibrary.app.ui.page3

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.irklibrary.app.R
import com.irklibrary.app.data.models.HuffmanNode
import kotlin.math.*

class MiniTreeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.primary)
    }

    private val nodeStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.outline)
        strokeWidth = 2f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.on_primary)
        textSize = 16f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.outline)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.secondary)
        textSize = 14f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private var rootNode: HuffmanNode? = null
    private var showLabels: Boolean = false
    private val nodeWidth = 80f
    private val nodeHeight = 50f
    private val levelHeight = 70f
    private val nodePositions = mutableMapOf<HuffmanNode, Pair<Float, Float>>()

    fun setTree(node: HuffmanNode?, showLabels: Boolean = false) {
        this.rootNode = node
        this.showLabels = showLabels
        calculatePositions()
        invalidate()
    }

    private fun calculatePositions() {
        nodePositions.clear()
        rootNode?.let { root ->
            val treeWidth = calculateTreeWidth(root)
            val startX = treeWidth / 2
            positionNodes(root, startX, nodeHeight / 2 + 10f)
        }
    }

    private fun calculateTreeWidth(node: HuffmanNode): Float {
        if (node.isLeaf()) {
            return nodeWidth + 20f
        }

        val leftWidth = node.left?.let { calculateTreeWidth(it) } ?: 0f
        val rightWidth = node.right?.let { calculateTreeWidth(it) } ?: 0f

        return max(nodeWidth + 20f, leftWidth + rightWidth + 30f)
    }

    private fun positionNodes(node: HuffmanNode, x: Float, y: Float) {
        nodePositions[node] = Pair(x, y)

        if (!node.isLeaf()) {
            val leftWidth = node.left?.let { calculateTreeWidth(it) } ?: 0f
            val rightWidth = node.right?.let { calculateTreeWidth(it) } ?: 0f

            node.left?.let { leftChild ->
                val leftX = x - (leftWidth + rightWidth) / 3.5f
                positionNodes(leftChild, leftX, y + levelHeight)
            }

            node.right?.let { rightChild ->
                val rightX = x + (leftWidth + rightWidth) / 3.5f
                positionNodes(rightChild, rightX, y + levelHeight)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = 120
        val minHeight = if (rootNode?.isLeaf() == true) 80 else 180

        val desiredWidth = rootNode?.let {
            val treeWidth = calculateTreeWidth(it).toInt() + 40
            maxOf(treeWidth, minWidth)
        } ?: minWidth

        val desiredHeight = rootNode?.let {
            val treeHeight = calculateTreeHeight(it) * levelHeight.toInt() + 60
            maxOf(treeHeight, minHeight)
        } ?: minHeight

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    private fun calculateTreeHeight(node: HuffmanNode): Int {
        if (node.isLeaf()) return 1

        val leftHeight = node.left?.let { calculateTreeHeight(it) } ?: 0
        val rightHeight = node.right?.let { calculateTreeHeight(it) } ?: 0

        return 1 + max(leftHeight, rightHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rootNode?.let { root ->
            drawConnections(canvas, root)
            drawNodes(canvas, root)
        }
    }

    private fun drawConnections(canvas: Canvas, node: HuffmanNode) {
        if (node.isLeaf()) return

        val nodePos = nodePositions[node] ?: return
        val nodeX = nodePos.first
        val nodeY = nodePos.second

        node.left?.let { leftChild ->
            val leftPos = nodePositions[leftChild] ?: return@let
            canvas.drawLine(nodeX, nodeY + nodeHeight/2, leftPos.first, leftPos.second - nodeHeight/2, linePaint)

            if (showLabels) {
                val midX = (nodeX + leftPos.first) / 2 - 15
                val midY = (nodeY + leftPos.second) / 2
                canvas.drawText("0", midX, midY, labelPaint)
            }

            drawConnections(canvas, leftChild)
        }

        node.right?.let { rightChild ->
            val rightPos = nodePositions[rightChild] ?: return@let
            canvas.drawLine(nodeX, nodeY + nodeHeight/2, rightPos.first, rightPos.second - nodeHeight/2, linePaint)

            if (showLabels) {
                val midX = (nodeX + rightPos.first) / 2 + 15
                val midY = (nodeY + rightPos.second) / 2
                canvas.drawText("1", midX, midY, labelPaint)
            }

            drawConnections(canvas, rightChild)
        }
    }

    private fun drawNodes(canvas: Canvas, node: HuffmanNode) {
        val nodePos = nodePositions[node] ?: return
        val nodeX = nodePos.first
        val nodeY = nodePos.second

        val rect = RectF(
            nodeX - nodeWidth/2,
            nodeY - nodeHeight/2,
            nodeX + nodeWidth/2,
            nodeY + nodeHeight/2
        )

        canvas.drawRoundRect(rect, 4f, 4f, nodePaint)
        canvas.drawRoundRect(rect, 4f, 4f, nodeStrokePaint)

        val nodeText = if (node.character != null) {
            node.character.toString()
        } else {
            node.nodeId
        }

        val adjustedTextSize = when {
            nodeText.length <= 2 -> 16f
            nodeText.length <= 4 -> 14f
            nodeText.length <= 6 -> 12f
            else -> 10f
        }

        textPaint.textSize = adjustedTextSize
        canvas.drawText(nodeText, nodeX, nodeY - 5, textPaint)

        textPaint.textSize = 12f
        canvas.drawText("(${node.frequency})", nodeX, nodeY + 15, textPaint)

        if (!node.isLeaf()) {
            node.left?.let { drawNodes(canvas, it) }
            node.right?.let { drawNodes(canvas, it) }
        }
    }
}