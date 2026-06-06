package com.example.noteslist.presentation.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.example.noteslist.R
import com.example.noteslist.domain.model.Note
import java.time.format.DateTimeFormatter

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.noteViewStyle,
    defStyleRes: Int = R.style.NoteStyle_NotRead
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    private val contentStartOffset =
        resources.getDimensionPixelSize(R.dimen.note_content_start_offset)
    private val bottomPadding =
        resources.getDimensionPixelSize(R.dimen.note_bottom_padding)
    private val verticalSpacing =
        resources.getDimensionPixelSize(R.dimen.note_vertical_spacing)
    private val iconSize =
        resources.getDimensionPixelSize(R.dimen.note_icon_size)
    private val doneMargin =
        resources.getDimensionPixelSize(R.dimen.note_done_margin)
    private val headerExtraHeight =
        resources.getDimensionPixelSize(R.dimen.note_header_extra_height)

    private var isRead = false
    private var noteTextColor = context.getColor(R.color.text_primary)
    private var noteCornerRadius = resources.getDimension(R.dimen.note_corner_radius)
    private var noteElevation = resources.getDimension(R.dimen.note_elevation)

    private val titleView = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setTextColor(context.getColor(R.color.note_title_color))
        setTypeface(typeface, Typeface.BOLD)
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    private val contentView = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        setTextColor(context.getColor(R.color.note_content_color))
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }

    private val timeView = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTextColor(context.getColor(R.color.note_time_color))
    }

    private val importantIcon = ImageView(context).apply {
        setImageResource(android.R.drawable.btn_star_big_on)
        setColorFilter(context.getColor(R.color.note_star_color))
        visibility = GONE
        alpha = 0.9f
    }

    private val doneIcon = TextView(context).apply {
        text = context.getString(R.string.check_mark)
        textSize = 12f
        setTextColor(context.getColor(R.color.white))
        gravity = Gravity.CENTER
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(context.getColor(R.color.note_done_color))
        }
        visibility = GONE
    }

    private val headerBackground = View(context).apply {
        background = GradientDrawable().apply {
            setColor(context.getColor(R.color.note_header_color))
            cornerRadii = floatArrayOf(
                doneMargin.toFloat(), doneMargin.toFloat(),
                doneMargin.toFloat(), doneMargin.toFloat(),
                0f, 0f, 0f, 0f
            )
        }
    }

    var onNoteClick: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.NoteView,
            defStyleAttr,
            defStyleRes,
        ).apply {
            try {
                noteTextColor = getColor(
                    R.styleable.NoteView_noteTextColor,
                    context.getColor(R.color.black)
                )
                noteCornerRadius = getDimension(
                    R.styleable.NoteView_noteCornerRadius,
                    doneMargin.toFloat()
                )
                noteElevation = getDimension(
                    R.styleable.NoteView_noteElevation,
                    noteElevation
                )
            } finally {
                recycle()
            }
        }

        elevation = noteElevation
        titleView.setTextColor(noteTextColor)
        contentView.setTextColor(noteTextColor)

        addView(headerBackground)
        addView(importantIcon)
        addView(titleView)
        addView(contentView)
        addView(timeView)
        addView(doneIcon)

        setPadding(0, 0, 0, bottomPadding)
        setOnClickListener { onNoteClick?.invoke() }

        updateBackground()
    }

    fun render(state: Note) {
        titleView.text = state.title
        contentView.text = state.text
        timeView.text = state.createdAt.format(formatter)
        importantIcon.visibility = if (state.isImportant) VISIBLE else GONE
        isRead = state.isRead
        updateBackground()

        contentView.doOnLayout {
            applyFade()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val availableWidth = if (widthMode == MeasureSpec.UNSPECIFIED) {
            Int.MAX_VALUE / 4
        } else {
            widthSize
        }
        val contentWidth = maxOf(0, availableWidth - contentStartOffset)

        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            contentWidth,
            MeasureSpec.AT_MOST
        )
        val childHeightSpec = if (heightMode == MeasureSpec.UNSPECIFIED) {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        } else {
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST)
        }

        importantIcon.measure(
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY)
        )

        titleView.measure(childWidthSpec, childHeightSpec)
        val headerHeight = titleView.measuredHeight + headerExtraHeight

        val headerBgWidthSpec = if (widthMode == MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        } else {
            MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)
        }
        headerBackground.measure(
            headerBgWidthSpec,
            MeasureSpec.makeMeasureSpec(
                headerHeight,
                MeasureSpec.EXACTLY
            )
        )

        contentView.measure(childWidthSpec, childHeightSpec)
        timeView.measure(childWidthSpec, childHeightSpec)

        doneIcon.measure(
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY)
        )

        val desiredHeight =
            paddingTop +
                    headerBackground.measuredHeight +
                    verticalSpacing +
                    contentView.measuredHeight +
                    verticalSpacing +
                    timeView.measuredHeight +
                    paddingBottom

        val maxChildContentWidth = maxOf(
            titleView.measuredWidth,
            contentView.measuredWidth,
            timeView.measuredWidth
        )
        val desiredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            val widthFromContent = contentStartOffset + maxChildContentWidth
            val widthFromDone = doneMargin + doneIcon.measuredWidth + paddingRight
            maxOf(widthFromContent, widthFromDone, headerBackground.measuredWidth)
        }

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        val left = contentStartOffset
        val headerHeight = headerBackground.measuredHeight
        val centerY = headerHeight / 2
        var headerLeft = left

        if (importantIcon.isVisible) {
            importantIcon.layout(
                headerLeft,
                centerY - importantIcon.measuredHeight / 2,
                headerLeft + importantIcon.measuredWidth,
                centerY + importantIcon.measuredHeight / 2
            )
            headerLeft += importantIcon.measuredWidth + verticalSpacing
        }

        titleView.layout(
            headerLeft,
            centerY - titleView.measuredHeight / 2,
            headerLeft + titleView.measuredWidth,
            centerY + titleView.measuredHeight / 2
        )

        headerBackground.layout(0, 0, width, headerBackground.measuredHeight)
        top += headerBackground.measuredHeight + verticalSpacing

        contentView.layout(
            left,
            top,
            left + contentView.measuredWidth,
            top + contentView.measuredHeight
        )
        top += contentView.measuredHeight + verticalSpacing

        timeView.layout(
            left,
            top,
            left + timeView.measuredWidth,
            top + timeView.measuredHeight
        )

        doneIcon.layout(
            width - doneMargin - doneIcon.measuredWidth,
            height - doneMargin - doneIcon.measuredHeight,
            width - doneMargin,
            height - doneMargin
        )
    }

    private fun updateBackground() {
        val color =
            if (isRead) context.getColor(R.color.note_read)
            else context.getColor(R.color.note_not_read)

        background = GradientDrawable().apply {
            setColor(color)
            cornerRadius = noteCornerRadius
        }

        doneIcon.visibility = if (isRead) VISIBLE else GONE

        if (isRead) {
            titleView.paintFlags = titleView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            titleView.alpha = 0.7f
            contentView.alpha = 0.5f
        } else {
            titleView.paintFlags = titleView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            titleView.alpha = 1f
            contentView.alpha = 1f
        }
    }

    private fun applyFade() {
        if (contentView.layout.lineCount >= 2) {
            val shader = LinearGradient(
                contentView.width * 0.8f,
                0f,
                contentView.width.toFloat(),
                0f,
                intArrayOf(noteTextColor, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
            contentView.paint.shader = shader
        } else {
            contentView.paint.shader = null
        }
    }
}