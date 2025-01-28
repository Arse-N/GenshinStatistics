import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.genshinstatistics.R

abstract class ItemSwiper(
    private val context: Context
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_remove)
    private val editIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_edit)
    private val deleteColor = ContextCompat.getColor(context, R.color.pyro)
    private val editColor = ContextCompat.getColor(context, R.color.light_gold)

    private val swipeLimitPx = 200f

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeLimitPx / viewHolder.itemView.width
    }
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        val cornerRadius = 20f
        val margin = 80
        val iconScaleFactorEdit = 1.5f
        val iconScaleFactorDelete = 1.2f
        val limitedDx = when {
            dX > swipeLimitPx -> swipeLimitPx
            dX < -swipeLimitPx -> -swipeLimitPx
            else -> dX
        }

        if (limitedDx > 0) { // Swipe to the right (edit)
            paint.color = editColor
            val path = Path().apply {
                addRoundRect(
                    RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.left + limitedDx,
                        itemView.bottom.toFloat()
                    ),
                    floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius),
                    Path.Direction.CW
                )
            }
            c.drawPath(path, paint)

            editIcon?.let {
                val iconWidth = it.intrinsicWidth * iconScaleFactorEdit
                val iconHeight = it.intrinsicHeight * iconScaleFactorEdit
                val iconMargin = (itemView.height - iconHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconLeft = itemView.left + margin
                val iconRight = iconLeft + iconWidth
                val iconBottom = iconTop + iconHeight
                it.setBounds(iconLeft, iconTop.toInt(), iconRight.toInt(), iconBottom.toInt())
                it.draw(c)
            }
        } else if (limitedDx < 0) { // Swipe to the left (delete)
            paint.color = deleteColor
            val path = Path().apply {
                addRoundRect(
                    RectF(
                        itemView.right + limitedDx,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    ),
                    floatArrayOf(0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f),
                    Path.Direction.CW
                )
            }
            c.drawPath(path, paint)

            deleteIcon?.let {
                val iconWidth = it.intrinsicWidth * iconScaleFactorDelete
                val iconHeight = it.intrinsicHeight * iconScaleFactorDelete
                val iconMargin = (itemView.height - iconHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconRight = itemView.right - margin
                val iconLeft = iconRight - iconWidth
                val iconBottom = iconTop + iconHeight
                it.setBounds(iconLeft.toInt(), iconTop.toInt(), iconRight, iconBottom.toInt())
                it.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, limitedDx, dY, actionState, isCurrentlyActive)
    }



}
