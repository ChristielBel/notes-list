package com.example.noteslist.presentation.ui.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.noteslist.R
import java.time.format.DateTimeFormatter

class DateHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setTextAppearance(R.style.NoteDateHeaderText)

        val horizontal =
            resources.getDimensionPixelSize(R.dimen.date_header_horizontal)

        val vertical =
            resources.getDimensionPixelSize(R.dimen.date_header_vertical)

        setPadding(horizontal, vertical, horizontal, vertical)

        background = createDividerBackground()
    }

    fun bind(text: String) {
        this.text = text
    }

    private fun createDividerBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius =
                resources.getDimension(R.dimen.date_header_radius)

            setColor(context.getColor(R.color.note_header_color))
        }
    }

    companion object {
        private val formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }
}