package rakib.hasan.connect.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PongView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private var cx = 0f
    private var cy = 0f
    private var dx = 5f
    private var dy = 5f
    private var radius = 50f

    init {
        paint.color = Color.CYAN
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(cx, cy, radius, paint)

        cx += dx
        cy += dy

        if (cx + radius > width) {
            cx = width - radius
            dx *= -1
        } else if (cx - radius < 0) {
            cx = radius
            dx *= -1
        }

        if (cy + radius > height) {
            cy = height - radius
            dy *= -1
        } else if (cy - radius < 0) {
            cy = radius
            dy *= -1
        }

        invalidate()
    }
}