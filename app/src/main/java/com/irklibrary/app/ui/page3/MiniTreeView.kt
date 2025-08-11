package com.irklibrary.app.ui.page3

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.irklibrary.app.R
import com.irklibrary.app.data.models.HuffmanNode

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

    private var nodes: List<HuffmanNode> = emptyList()
    private val nodeWidth = 60f
    private val nodeHeight = 40f
    private val nodeSpacing = 80f

    fun setNodes(nodeList: List<HuffmanNode>) {
        this.nodes = nodeList
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (nodes.isEmpty()) {
            200
        } else {
            (nodes.size * nodeSpacing + nodeWidth).toInt()
        }
        val height = 80

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (nodes.isEmpty()) return

        val centerY = height / 2f
        var currentX = nodeWidth / 2

        nodes.forEach { node ->
            drawMiniNode(canvas, node, currentX, centerY)
            currentX += nodeSpacing
        }
    }

    private fun drawMiniNode(canvas: Canvas, node: HuffmanNode, x: Float, y: Float) {
        // Calculate rectangle bounds
        val left = x - nodeWidth / 2
        val top = y - nodeHeight / 2
        val right = x + nodeWidth / 2
        val bottom = y + nodeHeight / 2

        val rect = RectF(left, top, right, bottom)

        // Draw node rectangle
        canvas.drawRoundRect(rect, 8f, 8f, nodePaint)
        canvas.drawRoundRect(rect, 8f, 8f, nodeStrokePaint)

        // Draw node text
        val nodeText = if (node.character != null) {
            when (node.character) {
                ' ' -> "_"
                else -> node.character.toString()
            }
        } else {
            node.nodeId.take(3)
        }

        // Draw character/node id
        val mainTextPaint = Paint(textPaint).apply {
            textSize = 12f
        }
        canvas.drawText(nodeText, x, y - 2, mainTextPaint)

        // Draw frequency
        val freqTextPaint = Paint(textPaint).apply {
            textSize = 10f
        }
        canvas.drawText("(${node.frequency})", x, y + 12, freqTextPaint)
    }
}