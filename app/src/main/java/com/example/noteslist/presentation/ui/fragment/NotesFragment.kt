package com.example.noteslist.presentation.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.App
import com.example.noteslist.R
import com.example.noteslist.component.NotesComponent
import com.example.noteslist.domain.mapper.toParcel
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.Settings
import com.example.noteslist.presentation.state.NotesState
import com.example.noteslist.presentation.ui.adapter.NotesAdapter
import com.example.noteslist.presentation.ui.decoration.NotesSpacingDecoration
import com.example.noteslist.presentation.viewmodel.NotesViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private val viewModel: NotesViewModel by viewModels {
        val app = requireActivity().application as App
        val component = NotesComponent(app.appComponent)
        component.provideFactory()
    }

    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var searchContainer: View
    private lateinit var search: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var settingsFab: FloatingActionButton
    private lateinit var adapter: NotesAdapter
    private lateinit var decoration: NotesSpacingDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecycler()
        setupListeners()
        setupInsets()
        observeState()
    }

    private fun initViews(view: View) {
        shimmerLayout = view.findViewById(R.id.shimmerLayout)
        searchContainer = view.findViewById(R.id.searchContainer)
        search = view.findViewById(R.id.search)
        recyclerView = view.findViewById(R.id.recycler)
        fab = view.findViewById(R.id.fab)
        settingsFab = view.findViewById(R.id.settingsFab)
    }

    private fun setupRecycler() {
        adapter = NotesAdapter(
            onNoteClick = { openEditor(it) },
            onNoteLongClick = { viewModel.toggleRead(it) },
            onStackClick = { viewModel.toggleStack(it) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        decoration =
            NotesSpacingDecoration(resources.getDimensionPixelSize(R.dimen.decoration_spacing))
        recyclerView.addItemDecoration(decoration)
    }

    private fun setupListeners() {

        search.doAfterTextChanged {
            viewModel.setQuery(it?.toString().orEmpty())
        }

        search.setOnFocusChangeListener { _, hasFocus ->
            searchContainer.animate()
                .scaleX(if (hasFocus) 1.02f else 1f)
                .scaleY(if (hasFocus) 1.02f else 1f)
                .setDuration(150)
                .start()
        }

        fab.setOnClickListener { openEditor(null) }

        settingsFab.setOnClickListener { openSettings() }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                handleFabVisibility(dy)
            }
        })
    }

    private fun handleFabVisibility(dy: Int) {
        if (dy > 0) {
            fab.hide()
            settingsFab.hide()
        } else {
            fab.show()
            settingsFab.show()
        }
    }

    private fun setupInsets() {
        recyclerView.doOnLayout {

            ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
                applyInsets(v, insets)
            }

            ViewCompat.requestApplyInsets(recyclerView)
        }
    }

    private fun applyInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {

        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val horizontal = resources.getDimensionPixelSize(R.dimen.note_item_spacing)

        searchContainer.translationY = bars.top.toFloat()

        val topOffset = searchContainer.height + bars.top

        v.setPadding(
            horizontal + bars.left,
            topOffset,
            horizontal + bars.right,
            bars.bottom
        )

        fab.translationY = -bars.bottom.toFloat()
        settingsFab.translationY = -bars.bottom.toFloat()

        return insets
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiItems.collect { state ->
                renderState(state)
            }
        }
    }

    private fun renderState(state: NotesState) {

        if (state.showShimmer) {
            shimmerLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            return
        }

        shimmerLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        adapter.updateSettings(
            Settings(
                stackSpacing = state.stackSpacing,
                stackMaxVisible = state.stackMaxVisible
            )
        )

        adapter.submitList(state.items)
        recyclerView.invalidateItemDecorations()
    }

    fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    private fun openEditor(note: Note?) {

        val bundle = Bundle().apply {
            putParcelable("note", note?.toParcel())
        }

        if (isLandscape()) {
            val fragment = EditorFragment().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.editor_container, fragment)
                .commit()
        } else {
            findNavController().navigate(R.id.editorFragment, bundle)
        }
    }

    private fun openSettings() {
        SettingsBottomSheet()
            .show(parentFragmentManager, "settings")
    }
}