package com.example.testblur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import jp.wasabeef.glide.transformations.internal.FastBlur
import jp.wasabeef.glide.transformations.internal.RSBlur
import java.lang.Exception

const val BLUR_RADIUS = 125

class BlurryBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var backgroundImage: Bitmap? = null
    private var radius = 50f // TODO improve dp

    private var strokeWidthGradientLine = 5f
    private val halfStrokeWidth = strokeWidthGradientLine / 2

    @ColorRes
    private var startColor: Int = R.color.secondary_text// TODO improve

    private var endColor = Color.TRANSPARENT

    private var paint = Paint()
    private val overlayPaint = Paint()

    @ColorRes
    private var overlayColor: Int = R.color.gray_white

    private val overlayRect by lazy { RectF(0f, 0f, width.toFloat(), height.toFloat()) }

    private val gradient by lazy {
        LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(ContextCompat.getColor(context,startColor), endColor), // Установите цвета градиента
            floatArrayOf(0f, 1f), // Установите позиции цветов градиента
            Shader.TileMode.CLAMP
        )
    }

    private val roundedRect by lazy { RectF(
        halfStrokeWidth, halfStrokeWidth,
        width.toFloat() - halfStrokeWidth, height.toFloat() - halfStrokeWidth
    ) }

    init {
        context.obtainStyledAttributes(attrs,R.styleable.BlurryBackgroundView,defStyleAttr,0).let {
            radius = it.getFloat(R.styleable.BlurryBackgroundView_cornerRadiusView,radius)
            overlayColor = it.getResourceId(R.styleable.BlurryBackgroundView_backgroundColorOverlay,overlayColor)
            startColor = it.getResourceId(R.styleable.BlurryBackgroundView_startColorGradient,startColor)
            strokeWidthGradientLine = it.getFloat(R.styleable.BlurryBackgroundView_strokeWidthGradient,strokeWidthGradientLine)
            it.recycle()
        }
        backgroundImage = (background as? BitmapDrawable)?.bitmap
    }

    fun updateBackgroundFromView() {
        val location = IntArray(2)
        getLocationOnScreen(location)

        val viewX = location[0]
        val viewY = location[1]
        val width = width
        val height = height

        val rootView = rootView
        val screenshot = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(screenshot)
        rootView.draw(canvas)

        val croppedBitmap = Bitmap.createBitmap(screenshot, viewX, viewY, width, height)

        val bitmap = applyBlur(croppedBitmap)
        Canvas(bitmap).apply {
            drawColor(ContextCompat.getColor(context, overlayColor))
        }
//        backgroundImage = applyBlur(croppedBitmap)

        backgroundImage = createRoundedBitmap(bitmap)

        backgroundImage.let {
            background = BitmapDrawable(resources, it)
        }

//        createColorBackground(canvas)

//        createGradientLine()
//
//        canvas.drawRoundRect(roundedRect, radius, radius, paint)
    }

//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        backgroundImage?.let {
//            canvas.drawBitmap(it, 0f, 0f, null)
//        }
//
//        createColorBackground(canvas)
//
//        createGradientLine()
//
//        canvas.drawRoundRect(roundedRect, radius, radius, paint)
//    }

    private fun createColorBackground(canvas:Canvas){
        overlayPaint.color = ContextCompat.getColor(context, overlayColor)
        canvas.drawRoundRect(overlayRect, radius, radius, overlayPaint)
    }

    private fun createGradientLine(){
        paint.shader = gradient
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthGradientLine // Установите желаемую толщину линии
    }

    private fun applyBlur(sourceBitmap: Bitmap): Bitmap {
        return try {
            RSBlur.blur(context, sourceBitmap, BLUR_RADIUS)
        } catch (e: Exception) {
            FastBlur.blur(sourceBitmap, BLUR_RADIUS, true)
        }
    }

    private fun createRoundedBitmap(sourceBitmap: Bitmap?): Bitmap? {
        sourceBitmap ?: return null

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), radius, radius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)

        return output
    }
}