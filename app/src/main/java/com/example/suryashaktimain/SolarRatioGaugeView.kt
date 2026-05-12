package com.example.suryashaktimain

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.min

class SolarRatioGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val arcBounds = RectF()
    private val textBounds = Rect()
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val solarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val exportPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mainTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var solarUsedKwh = 0.0
    private var gridKwh = 0.0
    private var exportKwh = 0.0
    private var independenceScore = 0.0

    init {
        trackPaint.setupStroke(color(R.color.panel_raised))
        solarPaint.setupStroke(color(R.color.solar_yellow))
        gridPaint.setupStroke(color(R.color.grid_red))
        exportPaint.setupStroke(color(R.color.meter_green))

        mainTextPaint.apply {
            color = color(R.color.text_white)
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        labelPaint.apply {
            color = color(R.color.text_muted)
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    fun setEnergyBudget(
        solarUsedKwh: Double,
        gridKwh: Double,
        exportKwh: Double,
        independenceScore: Double
    ) {
        this.solarUsedKwh = solarUsedKwh
        this.gridKwh = gridKwh
        this.exportKwh = exportKwh
        this.independenceScore = independenceScore.coerceIn(0.0, 100.0)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewSize = min(width, height).toFloat()
        val strokeWidth = viewSize * 0.085f
        val radiusPadding = strokeWidth + 12f
        val centerX = width / 2f
        val centerY = height / 2f + 8f
        val arcSize = viewSize - radiusPadding * 2f

        arcBounds.set(
            centerX - arcSize / 2f,
            centerY - arcSize / 2f,
            centerX + arcSize / 2f,
            centerY + arcSize / 2f
        )

        trackPaint.strokeWidth = strokeWidth
        solarPaint.strokeWidth = strokeWidth
        gridPaint.strokeWidth = strokeWidth
        exportPaint.strokeWidth = strokeWidth

        val startAngle = 135f
        val maxSweep = 270f
        canvas.drawArc(arcBounds, startAngle, maxSweep, false, trackPaint)

        val total = (solarUsedKwh + gridKwh + exportKwh).coerceAtLeast(0.1)
        val solarSweep = ((solarUsedKwh / total) * maxSweep).toFloat()
        val gridSweep = ((gridKwh / total) * maxSweep).toFloat()
        val exportSweep = ((exportKwh / total) * maxSweep).toFloat()

        var nextAngle = startAngle
        if (solarSweep > 0f) {
            canvas.drawArc(arcBounds, nextAngle, solarSweep, false, solarPaint)
            nextAngle += solarSweep
        }
        if (gridSweep > 0f) {
            canvas.drawArc(arcBounds, nextAngle, gridSweep, false, gridPaint)
            nextAngle += gridSweep
        }
        if (exportSweep > 0f) {
            canvas.drawArc(arcBounds, nextAngle, exportSweep, false, exportPaint)
        }

        drawCenteredText(canvas, centerX, centerY)
        drawLegend(canvas)
    }

    private fun drawCenteredText(canvas: Canvas, centerX: Float, centerY: Float) {
        mainTextPaint.textSize = width.coerceAtMost(height) * 0.18f
        labelPaint.textSize = width.coerceAtMost(height) * 0.055f

        val score = "${"%.0f".format(independenceScore)}%"
        mainTextPaint.getTextBounds(score, 0, score.length, textBounds)
        canvas.drawText(score, centerX, centerY + textBounds.height() / 3f, mainTextPaint)
        canvas.drawText("SOLAR SHARE", centerX, centerY + textBounds.height() + 28f, labelPaint)
    }

    private fun drawLegend(canvas: Canvas) {
        val legendY = height - 16f
        val itemGap = width / 3f
        val firstX = itemGap * 0.5f

        labelPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            12f,
            resources.displayMetrics
        )
        drawLegendItem(canvas, firstX, legendY, solarPaint, "Solar")
        drawLegendItem(canvas, firstX + itemGap, legendY, gridPaint, "Grid")
        drawLegendItem(canvas, firstX + itemGap * 2f, legendY, exportPaint, "Export")
    }

    private fun drawLegendItem(canvas: Canvas, x: Float, y: Float, paint: Paint, label: String) {
        dotPaint.apply {
            color = paint.color
            style = Paint.Style.FILL
        }
        canvas.drawCircle(x - 26f, y - 4f, 7f, dotPaint)
        canvas.drawText(label, x + 8f, y, labelPaint)
    }

    private fun Paint.setupStroke(paintColor: Int) {
        color = paintColor
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private fun color(colorRes: Int): Int {
        return resources.getColor(colorRes, context.theme)
    }
}
