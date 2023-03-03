package com.analog.clock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.analog.clock.R
import com.analog.clock.toDp
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = R.style.ClockViewStyle
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private companion object {
        val CLOCK_RADIUS_DEFAULT = 130f.toDp()
        val CLOCK_RADIUS_MIN = 80f.toDp()
        val CLOCK_RADIUS_MAX = 180f.toDp()
        val CLOCK_BRUSH_STROKE_WIDTH = 4f.toDp()
        val HOURS_BRUSH_STROKE_WIDTH = 4f.toDp()
        val MINUTES_BRUSH_STROKE_WIDTH = 3f.toDp()
        val SECONDS_BRUSH_STROKE_WIDTH = 2f.toDp()
        val DIVIDER_BRUSH_STROKE_WIDTH = 2f.toDp()
        val NUMBER_BRUSH_STROKE_WIDTH = 1f.toDp()
        val POINT_CENTER_RADIUS = 8f.toDp()
        const val HOURS_HAND_SCALE = 0.5f
        const val HAND_SCALE = 0.75f
        const val TEXT_SIZE_SCALE = 1 / 15F
    }

    private val hourNumbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val mRect = Rect()
    private var clockRadius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var hourHandSize = 0f
    private var handSize = 0f
    private var clockColor = 0
    private var secondHandColor = 0
    private var clockNumberSize = 0f

    private val paintStrokeBrush: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }
    }

    private val paintFillBrush: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    init {
        attrs?.also { initAttrs(it, defStyleRes) }
        isSaveEnabled = true
        isSaveFromParentEnabled = true
    }

    private fun initAttrs(attrs: AttributeSet, defStyleRes: Int) {
        context.theme
            .obtainStyledAttributes(attrs, R.styleable.ClockView, 0, defStyleRes)
            .apply {
                try {
                    clockRadius =
                        getDimension(R.styleable.ClockView_clockRadius, CLOCK_RADIUS_DEFAULT)
                            .coerceIn(CLOCK_RADIUS_MIN, CLOCK_RADIUS_MAX)
                    centerX = clockRadius
                    centerY = clockRadius
                    hourHandSize = clockRadius * HOURS_HAND_SCALE
                    handSize = clockRadius * HAND_SCALE
                    clockColor = getColor(R.styleable.ClockView_clockColor, Color.BLACK)
                    secondHandColor =
                        getColor(R.styleable.ClockView_secondHandColor, Color.RED)
                    val clockNumbersSizeDefault = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,
                        clockRadius * TEXT_SIZE_SCALE,
                        resources.displayMetrics
                    )
                    clockNumberSize =
                        getDimension(R.styleable.ClockView_clockNumberSize, clockNumbersSizeDefault)
                } catch (e: Throwable) {
                    Log.e("ClockView", "Can't apply attributes", e)
                } finally {
                    recycle()
                }
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = clockRadius * 2
        val height = clockRadius * 2
        setMeasuredDimension(
            resolveSize(width.toInt(), widthMeasureSpec) + paddingLeft + paddingRight,
            resolveSize(height.toInt(), heightMeasureSpec) + paddingTop + paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClock(canvas)
        drawHands(canvas)
        postInvalidateDelayed(500)
    }

    private fun drawClock(canvas: Canvas) {
        paintStrokeBrush.apply {
            color = clockColor
            strokeWidth = CLOCK_BRUSH_STROKE_WIDTH
        }
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        canvas.drawCircle(
            centerX,
            centerY,
            clockRadius - paintStrokeBrush.strokeWidth / 2,
            paintStrokeBrush
        )

        paintFillBrush.color = clockColor
        canvas.drawCircle(
            centerX,
            centerY,
            POINT_CENTER_RADIUS,
            paintFillBrush
        )

        drawNumbers(canvas)
    }

    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val correctHours = if (hours > 12) hours - 12 else hours
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        drawHourHand((correctHours + minutes / 60.0) * 5f, canvas)
        drawMinuteHand(minutes, canvas)
        drawSecondHand(seconds, canvas)
    }

    private fun drawHourHand(hours: Double, canvas: Canvas) {
        paintFillBrush.apply {
            color = clockColor
            strokeWidth = HOURS_BRUSH_STROKE_WIDTH
        }
        val angle = calculateAngle(hours)
        canvas.drawLine(
            centerX,
            centerY,
            (centerX + cos(angle) * hourHandSize),
            (centerY + sin(angle) * hourHandSize),
            paintFillBrush
        )
    }

    private fun drawMinuteHand(minutes: Int, canvas: Canvas) {
        paintFillBrush.apply {
            color = clockColor
            strokeWidth = MINUTES_BRUSH_STROKE_WIDTH
        }
        val angle = calculateAngle(minutes.toDouble())
        canvas.drawLine(
            centerX,
            centerY,
            (centerX + cos(angle) * handSize),
            ((centerY + sin(angle) * hourHandSize)),
            paintFillBrush
        )
    }

    private fun drawSecondHand(seconds: Int, canvas: Canvas) {
        paintFillBrush.apply {
            color = secondHandColor
            strokeWidth = SECONDS_BRUSH_STROKE_WIDTH
        }
        val angle = calculateAngle(seconds.toDouble())
        canvas.drawLine(
            centerX,
            centerY,
            (centerX + cos(angle) * handSize),
            (centerY + sin(angle) * hourHandSize),
            paintFillBrush
        )
    }

    private fun drawNumbers(canvas: Canvas) {
        paintFillBrush.apply {
            color = clockColor
            strokeWidth = NUMBER_BRUSH_STROKE_WIDTH
            textSize = clockNumberSize
        }
        hourNumbers.forEach { hour ->
            val num = hour.toString()
            paintFillBrush.getTextBounds(num, 0, num.length, mRect)
            val angle = Math.PI / 6 * (hour - 3)
            val x = (centerX + (cos(angle) * clockRadius - mRect.width()) / 1.3).toFloat()
            val y = (centerY + (sin(angle) * clockRadius + mRect.height()) / 1.3).toFloat()
            canvas.drawText(num, x, y, paintFillBrush)
            paintFillBrush.apply {
                strokeWidth = DIVIDER_BRUSH_STROKE_WIDTH
                color = clockColor
            }
            canvas.drawLine(
                (centerX + cos(angle) * clockRadius).toFloat(),
                (centerY + sin(angle) * clockRadius).toFloat(),
                (centerX + cos(angle) * clockRadius / 1.1).toFloat(),
                (centerY + sin(angle) * clockRadius / 1.1).toFloat(),
                paintFillBrush
            )
        }
    }

    private fun calculateAngle(time: Double): Float {
        return (Math.PI * time / 30 - Math.PI / 2).toFloat()
    }

    fun setClockRadius(radius: Float) {
        clockRadius = radius.toDp().coerceIn(CLOCK_RADIUS_MIN, CLOCK_RADIUS_MAX)
        clockNumberSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            clockRadius * TEXT_SIZE_SCALE,
            resources.displayMetrics
        )
        centerX = clockRadius
        centerY = clockRadius
        hourHandSize = clockRadius * HOURS_HAND_SCALE
        handSize = clockRadius * HAND_SCALE
        requestLayout()
        invalidate()
    }

    private fun setTime() {

    }

//    override fun onSaveInstanceState(): Parcelable {
//        val state = super.onSaveInstanceState()
//        return SavedState(
//            time,
//            state
//        )
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        state as SavedState
//        super.onRestoreInstanceState(state.superState)
//        setTime(state.time)
//    }

    @Parcelize
    class SavedState(
        val time: Long,
        @IgnoredOnParcel val source: Parcelable? = null
    ) : BaseSavedState(source)
}