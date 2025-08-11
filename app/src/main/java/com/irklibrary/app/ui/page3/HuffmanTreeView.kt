package com.irklibrary.app.ui.page3

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.content.ContextCompat
import com.irklibrary.app.R
import com.irklibrary.app.data.models.HuffmanNode
import kotlin.math.*

class HuffmanTreeView @JvmOverloads constructor(
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
        strokeWidth = 3f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.on_primary)
        textSize = 28f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.outline)
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.secondary)
        textSize = 24f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private var huffmanTree: HuffmanNode? = null
    private var nodeWidth = 120f
    private var nodeHeight = 80f
    private var levelHeight = 160f
    private var minNodeSpacing = 160f
    private var minLevelSpacing = 180f
    private var highlightedPath = mutableListOf<HuffmanNode>()
    private var selectedCharacter: Char? = null

    // Pan and Zoom variables
    private var scaleFactor = 1f
    private var translateX = 0f
    private var translateY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    // Gesture detectors
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())

    // Node positions cache with better positioning
    private val nodePositions = mutableMapOf<HuffmanNode, Pair<Float, Float>>()
    private val nodeSubtreeWidths = mutableMapOf<HuffmanNode, Float>()

    // Tree bounds for better positioning
    private var treeBounds = RectF()

    fun setHuffmanTree(tree: HuffmanNode?) {
        this.huffmanTree = tree
        scaleFactor = 1f // Reset scale to normal
        calculateOptimalPositions()
        centerTree()
        invalidate()
    }

    fun highlightCharacterPath(character: Char) {
        selectedCharacter = character
        highlightedPath.clear()
        huffmanTree?.let { root ->
            findCharacterPath(root, character, mutableListOf())
        }
        invalidate()
    }

    private fun findCharacterPath(node: HuffmanNode, character: Char, currentPath: MutableList<HuffmanNode>): Boolean {
        currentPath.add(node)

        if (node.isLeaf()) {
            if (node.character == character) {
                highlightedPath.addAll(currentPath)
                return true
            }
        } else {
            // Try left
            node.left?.let {
                if (findCharacterPath(it, character, currentPath)) {
                    return true
                }
            }

            // Try right
            node.right?.let {
                if (findCharacterPath(it, character, currentPath)) {
                    return true
                }
            }
        }

        currentPath.removeAt(currentPath.size - 1)
        return false
    }

    private fun calculateOptimalPositions() {
        nodePositions.clear()
        nodeSubtreeWidths.clear()

        huffmanTree?.let { root ->
            // First pass: calculate subtree widths
            calculateSubtreeWidths(root)

            // Second pass: position nodes using optimal algorithm
            val rootX = 0f
            val rootY = 100f
            positionNodeOptimal(root, rootX, rootY, 0)

            // Third pass: adjust for any remaining collisions
            adjustForCollisions()

            calculateTreeBounds()
        }
    }

    private fun adjustForCollisions() {
        // Group nodes by level
        val nodesByLevel = mutableMapOf<Int, MutableList<Pair<HuffmanNode, Float>>>()

        nodePositions.forEach { (node, pos) ->
            val level = getNodeLevel(node)
            nodesByLevel.computeIfAbsent(level) { mutableListOf() }.add(node to pos.first)
        }

        // Sort nodes by X position in each level and adjust spacing
        nodesByLevel.forEach { (level, nodes) ->
            nodes.sortBy { it.second }

            // Adjust X positions to maintain minimum spacing
            for (i in 1 until nodes.size) {
                val currentNode = nodes[i].first
                val prevNode = nodes[i - 1].first

                val currentPos = nodePositions[currentNode]!!
                val prevPos = nodePositions[prevNode]!!

                val minRequiredX = prevPos.first + minLevelSpacing
                if (currentPos.first < minRequiredX) {
                    // Move current node and all nodes to its right
                    val adjustment = minRequiredX - currentPos.first
                    adjustNodeAndDescendants(currentNode, adjustment, 0f)

                    // Update the sorted list
                    for (j in i until nodes.size) {
                        val nodeToUpdate = nodes[j].first
                        val updatedPos = nodePositions[nodeToUpdate]!!
                        nodes[j] = nodeToUpdate to updatedPos.first
                    }
                }
            }
        }
    }

    private fun getNodeLevel(targetNode: HuffmanNode): Int {
        fun findLevel(node: HuffmanNode, level: Int): Int? {
            if (node == targetNode) return level

            node.left?.let {
                findLevel(it, level + 1)?.let { return it }
            }
            node.right?.let {
                findLevel(it, level + 1)?.let { return it }
            }

            return null
        }

        return huffmanTree?.let { findLevel(it, 0) } ?: 0
    }

    private fun adjustNodeAndDescendants(node: HuffmanNode, deltaX: Float, deltaY: Float) {
        val currentPos = nodePositions[node] ?: return
        nodePositions[node] = Pair(currentPos.first + deltaX, currentPos.second + deltaY)

        // Recursively adjust descendants
        if (!node.isLeaf()) {
            node.left?.let { adjustNodeAndDescendants(it, deltaX, deltaY) }
            node.right?.let { adjustNodeAndDescendants(it, deltaX, deltaY) }
        }
    }

    private fun calculateSubtreeWidths(node: HuffmanNode): Float {
        if (node.isLeaf()) {
            val width = max(nodeWidth + minNodeSpacing, minLevelSpacing)
            nodeSubtreeWidths[node] = width
            return width
        }

        val leftWidth = node.left?.let { calculateSubtreeWidths(it) } ?: 0f
        val rightWidth = node.right?.let { calculateSubtreeWidths(it) } ?: 0f

        // Ensure minimum spacing between subtrees
        val totalWidth = leftWidth + rightWidth + minLevelSpacing
        val minRequiredWidth = max(totalWidth, nodeWidth + minNodeSpacing)

        nodeSubtreeWidths[node] = minRequiredWidth
        return minRequiredWidth
    }

    private fun positionNodeOptimal(node: HuffmanNode, x: Float, y: Float, level: Int) {
        nodePositions[node] = Pair(x, y)

        if (!node.isLeaf()) {
            val nextY = y + levelHeight

            // Get subtree widths
            val leftSubtreeWidth = node.left?.let { nodeSubtreeWidths[it] } ?: 0f
            val rightSubtreeWidth = node.right?.let { nodeSubtreeWidths[it] } ?: 0f

            // Calculate optimal spacing to prevent overlapping
            val totalSpacing = leftSubtreeWidth + rightSubtreeWidth + minLevelSpacing
            val halfSpacing = totalSpacing / 2

            // Position left child with enough space
            node.left?.let { leftChild ->
                val leftOffset = leftSubtreeWidth / 2
                val leftX = x - halfSpacing + leftOffset
                positionNodeOptimal(leftChild, leftX, nextY, level + 1)
            }

            // Position right child with enough space
            node.right?.let { rightChild ->
                val rightOffset = rightSubtreeWidth / 2
                val rightX = x + halfSpacing - rightOffset
                positionNodeOptimal(rightChild, rightX, nextY, level + 1)
            }
        }
    }

    private fun calculateTreeBounds() {
        if (nodePositions.isEmpty()) return

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        nodePositions.values.forEach { (x, y) ->
            minX = min(minX, x - nodeWidth / 2)
            maxX = max(maxX, x + nodeWidth / 2)
            minY = min(minY, y - nodeHeight / 2)
            maxY = max(maxY, y + nodeHeight / 2)
        }

        // Add generous padding to ensure all nodes can be viewed
        val padding = 150f
        treeBounds = RectF(minX - padding, minY - padding, maxX + padding, maxY + padding)
    }

    private fun centerTree() {
        if (treeBounds.isEmpty) return

        val viewCenterX = width / 2f
        val treeCenterX = treeBounds.centerX()

        // Center horizontally but ensure all nodes are accessible
        translateX = viewCenterX - treeCenterX

        // Position tree from top with minimal constraint
        translateY = 100f - treeBounds.top

        // Don't apply any size constraints - let user zoom/pan freely
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (huffmanTree != null) {
            calculateOptimalPositions()
            centerTree()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!scaleDetector.isInProgress) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    translateX += dx
                    translateY += dy

                    // Apply very loose constraints to prevent tree from going too far
                    constrainTranslationLoose()

                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                val clickX = event.x
                val clickY = event.y
                val clickedNode = getNodeAtPosition(clickX, clickY)
                clickedNode?.let { node ->
                    if (node.isLeaf() && node.character != null) {
                        highlightCharacterPath(node.character!!)
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun constrainTranslationLoose() {
        val scaledTreeWidth = treeBounds.width() * scaleFactor
        val scaledTreeHeight = treeBounds.height() * scaleFactor

        // Very generous limits - allow viewing far beyond tree bounds
        val maxTranslateX = scaledTreeWidth + 500f
        val maxTranslateY = scaledTreeHeight + 500f

        // Very loose constraints - basically no real limitation
        translateX = translateX.coerceIn(-maxTranslateX, maxTranslateX)
        translateY = translateY.coerceIn(-maxTranslateY, maxTranslateY)
    }

    private fun getNodeAtPosition(x: Float, y: Float): HuffmanNode? {
        val canvasX = (x - translateX) / scaleFactor
        val canvasY = (y - translateY) / scaleFactor

        nodePositions.forEach { (node, pos) ->
            val nodeX = pos.first
            val nodeY = pos.second

            val left = nodeX - nodeWidth / 2
            val top = nodeY - nodeHeight / 2
            val right = nodeX + nodeWidth / 2
            val bottom = nodeY + nodeHeight / 2

            if (canvasX >= left && canvasX <= right && canvasY >= top && canvasY <= bottom) {
                return node
            }
        }

        return null
    }

    private fun constrainTranslation() {
        val scaledTreeWidth = treeBounds.width() * scaleFactor
        val scaledTreeHeight = treeBounds.height() * scaleFactor

        // Give much more freedom for panning to see all nodes
        val extraPadding = 400f
        val maxTranslateX = (scaledTreeWidth - width) / 2 + extraPadding
        val maxTranslateY = (scaledTreeHeight - height) / 2 + extraPadding

        // Allow negative values for panning in all directions
        translateX = translateX.coerceIn(-maxTranslateX, maxTranslateX)
        translateY = translateY.coerceIn(-maxTranslateY, maxTranslateY)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(translateX, translateY)
        canvas.scale(scaleFactor, scaleFactor)

        huffmanTree?.let { root ->
            drawTree(canvas, root)
        }

        canvas.restore()
    }

    private fun drawTree(canvas: Canvas, node: HuffmanNode) {
        // Draw connections first (behind nodes)
        drawConnections(canvas, node)

        // Then draw nodes (in front of connections)
        drawNodes(canvas, node)
    }

    private fun drawConnections(canvas: Canvas, node: HuffmanNode) {
        if (node.isLeaf()) return

        val nodePos = nodePositions[node] ?: return
        val nodeX = nodePos.first
        val nodeY = nodePos.second

        val isHighlighted = highlightedPath.contains(node)
        val highlightPaint = if (isHighlighted) {
            Paint(linePaint).apply {
                color = ContextCompat.getColor(context, R.color.primary)
                strokeWidth = 6f
            }
        } else linePaint

        // Draw left connection with smooth curve
        node.left?.let { leftChild ->
            val leftPos = nodePositions[leftChild] ?: return@let
            val leftX = leftPos.first
            val leftY = leftPos.second

            val isLeftHighlighted = highlightedPath.contains(leftChild)
            val connectionPaint = if (isHighlighted && isLeftHighlighted) highlightPaint else linePaint

            // Use smooth curves with better control points
            val path = Path().apply {
                moveTo(nodeX - nodeWidth * 0.2f, nodeY + nodeHeight / 2)

                val deltaX = abs(nodeX - leftX)
                val deltaY = abs(leftY - nodeY)

                // More pronounced curve for better separation
                val controlX1 = nodeX - deltaX * 0.2f
                val controlY1 = nodeY + deltaY * 0.4f
                val controlX2 = leftX + deltaX * 0.2f
                val controlY2 = leftY - deltaY * 0.4f

                cubicTo(controlX1, controlY1, controlX2, controlY2, leftX + nodeWidth * 0.2f, leftY - nodeHeight / 2)
            }

            canvas.drawPath(path, connectionPaint)

            // Draw "0" label with better positioning
            val midX = (nodeX + leftX) / 2 - 25
            val midY = (nodeY + leftY) / 2 - 15
            val labelColor = if (isHighlighted && isLeftHighlighted) {
                Paint(labelPaint).apply {
                    color = ContextCompat.getColor(context, R.color.primary)
                    style = Paint.Style.FILL_AND_STROKE
                    strokeWidth = 2f
                }
            } else labelPaint
            canvas.drawText("0", midX, midY, labelColor)

            drawConnections(canvas, leftChild)
        }

        // Draw right connection with smooth curve
        node.right?.let { rightChild ->
            val rightPos = nodePositions[rightChild] ?: return@let
            val rightX = rightPos.first
            val rightY = rightPos.second

            val isRightHighlighted = highlightedPath.contains(rightChild)
            val connectionPaint = if (isHighlighted && isRightHighlighted) highlightPaint else linePaint

            // Use smooth curves with better control points
            val path = Path().apply {
                moveTo(nodeX + nodeWidth * 0.2f, nodeY + nodeHeight / 2)

                val deltaX = abs(rightX - nodeX)
                val deltaY = abs(rightY - nodeY)

                // More pronounced curve for better separation
                val controlX1 = nodeX + deltaX * 0.2f
                val controlY1 = nodeY + deltaY * 0.4f
                val controlX2 = rightX - deltaX * 0.2f
                val controlY2 = rightY - deltaY * 0.4f

                cubicTo(controlX1, controlY1, controlX2, controlY2, rightX - nodeWidth * 0.2f, rightY - nodeHeight / 2)
            }

            canvas.drawPath(path, connectionPaint)

            // Draw "1" label with better positioning
            val midX = (nodeX + rightX) / 2 + 25
            val midY = (nodeY + rightY) / 2 - 15
            val labelColor = if (isHighlighted && isRightHighlighted) {
                Paint(labelPaint).apply {
                    color = ContextCompat.getColor(context, R.color.primary)
                    style = Paint.Style.FILL_AND_STROKE
                    strokeWidth = 2f
                }
            } else labelPaint
            canvas.drawText("1", midX, midY, labelColor)

            drawConnections(canvas, rightChild)
        }
    }

    private fun drawNodes(canvas: Canvas, node: HuffmanNode) {
        val nodePos = nodePositions[node] ?: return
        val nodeX = nodePos.first
        val nodeY = nodePos.second

        val isHighlighted = highlightedPath.contains(node)

        // Calculate rectangle bounds
        val left = nodeX - nodeWidth / 2
        val top = nodeY - nodeHeight / 2
        val right = nodeX + nodeWidth / 2
        val bottom = nodeY + nodeHeight / 2

        val rect = RectF(left, top, right, bottom)

        // Use different colors for highlighted nodes
        val fillPaint = if (isHighlighted) {
            Paint(nodePaint).apply {
                color = ContextCompat.getColor(context, R.color.primary_container)
            }
        } else nodePaint

        val strokePaint = if (isHighlighted) {
            Paint(nodeStrokePaint).apply {
                color = ContextCompat.getColor(context, R.color.primary)
                strokeWidth = 5f
            }
        } else nodeStrokePaint

        // Draw shadow for better visual depth
        val shadowPaint = Paint(nodePaint).apply {
            color = Color.BLACK
            alpha = 30
        }
        canvas.drawRoundRect(
            RectF(left + 4, top + 4, right + 4, bottom + 4),
            12f, 12f, shadowPaint
        )

        // Draw node rectangle with rounded corners
        canvas.drawRoundRect(rect, 12f, 12f, fillPaint)
        canvas.drawRoundRect(rect, 12f, 12f, strokePaint)

        // Draw node text
        val nodeText = if (node.character != null) {
            when (node.character) {
                ' ' -> "_"
                else -> node.character.toString()
            }
        } else {
            // Show combined characters for internal nodes
            node.nodeId.take(6)
        }

        val frequencyText = node.frequency.toString()

        // Use different text color for highlighted nodes
        val mainTextPaint = if (isHighlighted) {
            Paint(textPaint).apply {
                color = ContextCompat.getColor(context, R.color.primary)
                style = Paint.Style.FILL_AND_STROKE
                strokeWidth = 1f
            }
        } else textPaint

        // Draw character/node id
        canvas.drawText(nodeText, nodeX, nodeY - 8, mainTextPaint)

        // Draw frequency
        val smallTextPaint = Paint(textPaint).apply {
            textSize = 20f
            color = if (isHighlighted) {
                ContextCompat.getColor(context, R.color.primary)
            } else {
                ContextCompat.getColor(context, R.color.on_primary)
            }
        }
        canvas.drawText("($frequencyText)", nodeX, nodeY + 20, smallTextPaint)

        // Recursively draw child nodes
        if (!node.isLeaf()) {
            node.left?.let { drawNodes(canvas, it) }
            node.right?.let { drawNodes(canvas, it) }
        }
    }

    fun resetZoomAndPan() {
        scaleFactor = 1f
        centerTree()
        invalidate()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.3f, 3.0f)
            invalidate()
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            resetZoomAndPan()
            return true
        }
    }
}