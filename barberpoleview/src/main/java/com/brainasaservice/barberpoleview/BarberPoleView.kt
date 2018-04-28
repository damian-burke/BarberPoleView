package com.brainasaservice.barberpoleview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class BarberPoleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attrs,
        defStyleAttr
) {

    var lineRotation: Int = 45
        set(value) {
            field = value
            if (field != value) {
                lineRotationRad = Math.toRadians(field.toDouble())
                calculateRotatedBoundaries()
                invalidate()
            }
        }

    var animationSpeed: Long = 400L

    private var lineRotationRad: Double = Math.toRadians(lineRotation.toDouble())
        set(value) {
            field = value
            lineRotationSin = Math.abs(Math.sin(lineRotationRad))
            lineRotationCos = Math.abs(Math.cos(lineRotationRad))
        }

    private var animated: Boolean = true

    private var lineRotationSin: Double = Math.abs(Math.sin(lineRotationRad))
    private var lineRotationCos: Double = Math.abs(Math.cos(lineRotationRad))

    private var boundaries: Rect = Rect(0, 0, width, height)

    private var rotatedWidth: Int = 0
    private var rotatedHeight: Int = 0
    private var rotatedRect: Rect? = null
    private var xShift: Int = 0

    private var lineWidth = 5
        set(value) {
            field = value
            colors = colors.map {
                it.apply { strokeWidth = field.toFloat() }
            }
        }

    private var animationOffset = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var colors: List<Paint>

    private var animator: ValueAnimator?

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BarberPoleView)

            val colorsId = typedArray.getResourceId(
                    R.styleable.BarberPoleView_colors,
                    0
            )

            if (colorsId != 0) {
                colors = typedArray.resources.getIntArray(colorsId).map {
                    Paint().apply {
                        color = it
                        strokeWidth = lineWidth.toFloat()
                        flags = Paint.ANTI_ALIAS_FLAG
                    }
                }
            } else {
                colors = arrayOf(Color.RED, Color.WHITE).map {
                    Paint().apply {
                        color = it
                        strokeWidth = lineWidth.toFloat()
                        flags = Paint.ANTI_ALIAS_FLAG
                    }
                }
            }

            lineWidth = typedArray.getDimensionPixelSize(
                    R.styleable.BarberPoleView_line_width,
                    context.resources.getDimensionPixelSize(R.dimen.default_line_width)
            )

            lineRotation = typedArray.getInteger(
                    R.styleable.BarberPoleView_line_rotation,
                    context.resources.getInteger(R.integer.default_line_rotation)
            )

            animated = typedArray.getBoolean(
                    R.styleable.BarberPoleView_animated,
                    true
            )

            animationSpeed = typedArray.getInteger(
                    R.styleable.BarberPoleView_animation_speed,
                    context.resources.getInteger(R.integer.default_animation_speed)
            ).toLong()

            typedArray.recycle()
        } else {
            colors = arrayOf(Color.RED, Color.WHITE, Color.BLUE, Color.WHITE).map {
                Paint().apply {
                    color = it
                    strokeWidth = lineWidth.toFloat()
                    flags = Paint.ANTI_ALIAS_FLAG
                }
            }

            lineWidth = context.resources.getDimensionPixelSize(R.dimen.default_line_width)

            lineRotation = context.resources.getInteger(R.integer.default_line_rotation)

            animated = true

            animationSpeed = context.resources.getInteger(R.integer.default_animation_speed).toLong()
        }

        if (animated) {
            animator = createAnimator()
            animator?.start()
        } else {
            animator = null
        }
    }

    private fun createAnimator() = ValueAnimator.ofFloat(0.0f, lineWidth.toFloat() * colors.size).apply {
        duration = colors.size * animationSpeed
        repeatMode = ValueAnimator.RESTART
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            animationOffset = it.animatedValue as Float
        }
    }.also {
        animator?.end()
    }

    private fun calculateRotatedBoundaries() {
        val oldWidth = width
        val oldHeight = height

        val newHeight = oldWidth * lineRotationSin + oldHeight * lineRotationCos
        val newWidth = oldWidth * lineRotationCos + oldHeight * lineRotationSin

        rotatedWidth = newWidth.toInt()
        rotatedHeight = newHeight.toInt()

        rotatedRect = Rect(
                (width - rotatedWidth),
                (height - rotatedHeight),
                (width + rotatedWidth),
                (height + rotatedHeight)
        )

        xShift = rotatedRect?.left?.let { Math.abs(it) } ?: 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        boundaries = Rect(0, 0, w, h)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            if (animator?.isRunning == false) {
                animator?.start()
            }
        } else {
            animator?.cancel()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var counter = 0

        if (rotatedWidth == rotatedHeight && rotatedHeight == 0) {
            calculateRotatedBoundaries()
        }

        canvas?.let { c ->
            c.clipRect(boundaries)
            c.save()
            c.rotate(lineRotation.toFloat(), c.width / 2f, c.height / 2f)
            c.translate(-xShift.toFloat(), 0f)

            rotatedRect?.let {
                for (i in (it.left - xShift).coerceAtLeast(0)..it.right + xShift step lineWidth) {
                    c.drawLine(
                            i.toFloat() + animationOffset,
                            it.bottom.toFloat(),
                            i.toFloat() + animationOffset,
                            it.top.toFloat(),
                            colors[counter++ % colors.size]
                    )
                }
            }

            c.restore()
        }
    }
}
