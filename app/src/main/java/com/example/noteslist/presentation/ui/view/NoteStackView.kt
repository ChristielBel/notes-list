package com.example.noteslist.presentation.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.example.noteslist.R
import com.example.noteslist.data.datastore.MAX_VISIBLE
import com.example.noteslist.domain.model.Note

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        private const val ITEM_DELAY = 20L
        private const val STEP_DURATION = 40L
        private const val BASE_DURATION = 200L
        private const val MAX_DURATION = 800L

        private const val BUTTON_DELAY = 100L
        private const val BUTTON_ANIM_DURATION = 200L
    }

    private val stackHorizontalOffset =
        resources.getDimensionPixelSize(R.dimen.note_stack_horizontal_offset)
    private val stackScaleStep = 0.05f
    private var stackSpacing =
        resources.getDimensionPixelSize(R.dimen.note_stack_default_spacing)
    private var stackMaxVisible = MAX_VISIBLE

    private var expanded = false
    private var isAnimating = false

    private val notes = mutableListOf<Note>()
    private val noteViews = mutableListOf<NoteView>()
    private val viewById = mutableMapOf<Long, NoteView>()

    private var onNoteClick: ((Note) -> Unit)? = null
    private var onNoteLongClick: ((Note) -> Unit)? = null
    private var onStackClick: (() -> Unit)? = null

    private val interpolator = PathInterpolator(
        0.4f,
        0.1f,
        0.2f,
        1f,
    )
    private var collapseView: TextView? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.NoteStackView) {
            stackSpacing = getDimensionPixelSize(
                R.styleable.NoteStackView_stackSpacing,
                resources.getDimensionPixelSize(R.dimen.note_stack_default_spacing)
            )

            stackMaxVisible = getInt(
                R.styleable.NoteStackView_stackMaxVisible,
                3
            )
        }

        clipChildren = false
        clipToPadding = false

        setOnClickListener {
            onStackClick?.invoke()
        }
    }

    fun setOnStackClick(block: () -> Unit) {
        onStackClick = block
    }

    fun setExpanded(expanded: Boolean, animated: Boolean) {
        this.expanded = expanded

        if (animated && expanded) {
            startExpandAnimation()
        } else {
            cancelAnimations()
            updateVisibility()
        }
    }

    fun setStackSpacing(value: Int) {
        if (stackSpacing != value) {
            stackSpacing = value
            requestLayout()
        }
    }

    fun setMaxVisible(value: Int) {
        if (stackMaxVisible != value) {
            stackMaxVisible = value
            updateVisibility()
            requestLayout()
        }
    }

    private fun cancelAnimations() {
        noteViews.forEach { it.animate().cancel() }
        collapseView?.animate()?.cancel()
        isAnimating = false
    }

    fun submitNotes(
        list: List<Note>,
        onNoteClick: ((Note) -> Unit)?,
        onNoteLongClick: ((Note) -> Unit)?
    ) {
        this.onNoteClick = onNoteClick
        this.onNoteLongClick = onNoteLongClick

        val newIds = list.map { it.id }.toSet()
        val oldIds = notes.map { it.id }.toSet()

        (oldIds - newIds).forEach { id ->
            viewById[id]?.let {
                removeView(it)
                noteViews.remove(it)
                viewById.remove(id)
            }
        }

        list.forEachIndexed { index, note ->
            val existing = viewById[note.id]

            if (existing == null) {
                val view = NoteView(context)
                bind(view, note)

                addView(view, index)
                noteViews.add(index, view)
                viewById[note.id] = view
            } else {
                bind(existing, note)
            }
        }

        notes.clear()
        notes.addAll(list)

        if (collapseView == null) {
            collapseView = TextView(context).apply {
                text = context.getString(R.string.notes_collapse)
                setPadding(16, 16, 16, 16)
            }
            addView(collapseView)
        }

        if (!isAnimating) {
            updateVisibility()
        }
    }

    private fun bind(view: NoteView, note: Note) {
        view.render(note)

        view.setOnClickListener {
            if (expanded) onNoteClick?.invoke(note)
        }

        view.setOnLongClickListener {
            if (expanded) {
                onNoteLongClick?.invoke(note)
                true
            } else false
        }
    }

    private fun startExpandAnimation() {
        cancelAnimations()
        isAnimating = true

        requestLayout()

        post {
            runExpandAnimation()
        }
    }

    private fun runExpandAnimation() {
        val count = noteViews.size
        val duration = calculateDuration(count)

        noteViews.forEach { it.visibility = VISIBLE }
        collapseView?.visibility = INVISIBLE

        val startTops = noteViews.map { it.top }

        requestLayout()

        post {
            noteViews.forEachIndexed { index, view ->

                val startTop = startTops[index]
                val endTop = view.top

                val delta = startTop - endTop

                view.translationY = delta.toFloat()

                view.animate()
                    .translationY(0f)
                    .setStartDelay(index * ITEM_DELAY)
                    .setDuration(duration)
                    .setInterpolator(interpolator)
                    .start()
            }

            val elementsTime = calculateWaveTime(count)

            postDelayed({
                animateCollapseButton()
            }, elementsTime + BUTTON_DELAY)

            postDelayed({
                isAnimating = false
                updateVisibility()
            }, elementsTime + BUTTON_DELAY + BUTTON_ANIM_DURATION)
        }
    }

    private fun calculateDuration(count: Int): Long {
        return minOf(BASE_DURATION + count * STEP_DURATION, MAX_DURATION)
    }

    private fun calculateWaveTime(count: Int): Long {
        val duration = calculateDuration(count)
        val lastStartDelay = (count - 1) * ITEM_DELAY
        return lastStartDelay + duration
    }

    private fun animateCollapseButton() {
        collapseView?.let { view ->
            view.apply {
                visibility = VISIBLE
                alpha = 0f
                scaleX = 0.7f
                scaleY = 0.7f

                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(BUTTON_ANIM_DURATION)
                    .setInterpolator(interpolator)
                    .start()
            }
        }
    }

    private fun updateVisibility() {
        if (isAnimating) return

        val visible =
            if (expanded) notes.size
            else minOf(notes.size, stackMaxVisible)

        noteViews.forEachIndexed { index, view ->
            view.visibility = if (index < visible) VISIBLE else INVISIBLE
            view.translationY = 0f
        }

        collapseView?.visibility = if (expanded) VISIBLE else GONE

        requestLayout()
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        val visible =
            if (expanded) noteViews.size
            else minOf(noteViews.size, stackMaxVisible)

        var totalHeight = 0

        val childWidthSpec =
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)

        for (i in 0 until visible) {
            val child = noteViews[i]
            child.measure(childWidthSpec, heightMeasureSpec)

            totalHeight += if (expanded) {
                child.measuredHeight + stackSpacing
            } else {
                if (i == 0) child.measuredHeight else stackSpacing
            }
        }

        collapseView?.let {
            if (expanded) {
                it.measure(childWidthSpec, heightMeasureSpec)
                totalHeight += it.measuredHeight
            }
        }

        setMeasuredDimension(width, totalHeight)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int,
    ) {
        val visible =
            if (expanded) noteViews.size
            else minOf(noteViews.size, stackMaxVisible)

        var top = 0

        for (i in 0 until visible) {
            val child = noteViews[i]

            val left = if (expanded) 0 else i * stackHorizontalOffset
            val scale = if (expanded) 1f else 1f - i * stackScaleStep

            child.scaleX = scale
            child.scaleY = scale

            child.layout(
                left,
                top,
                left + child.measuredWidth,
                top + child.measuredHeight
            )

            val collapsedStep = minOf(
                (child.measuredHeight * 0.5f).toInt(),
                stackSpacing
            )

            top += if (expanded) {
                child.measuredHeight + stackSpacing
            } else {
                collapsedStep
            }
        }

        collapseView?.let {
            if (expanded) {
                it.layout(0, top, it.measuredWidth, top + it.measuredHeight)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return !expanded
    }
}