package com.irklibrary.app.ui.page4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.irklibrary.app.R
import com.irklibrary.app.data.models.SlideWithMatkulModel

class ReferencesFragment : Fragment() {

    private lateinit var viewModel: ReferencesViewModel
    private lateinit var slideAdapter: SlideAdapter

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var filterSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var emptyStateText: TextView
    private lateinit var resultCountText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_references, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initViewModel()
        initRecyclerView()
        setupSearchFunctionality()
        setupFilterSpinner()
        observeViewModel()
    }

    private fun initViews(view: View) {
        searchEditText = view.findViewById(R.id.et_search)
        clearButton = view.findViewById(R.id.btn_clear_search)
        filterSpinner = view.findViewById(R.id.spinner_filter)
        recyclerView = view.findViewById(R.id.rv_slides)
        progressBar = view.findViewById(R.id.progress_bar)
        emptyStateLayout = view.findViewById(R.id.layout_empty_state)
        emptyStateText = view.findViewById(R.id.tv_empty_state)
        resultCountText = view.findViewById(R.id.tv_result_count)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ReferencesViewModel::class.java]
    }

    private fun initRecyclerView() {
        slideAdapter = SlideAdapter { slide ->
            openPDFViewer(slide)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = slideAdapter
        }
    }

    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.updateSearchQuery(s.toString())
                updateClearButtonVisibility(s.toString())
            }
        })

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            viewModel.clearSearch()
        }
    }

    private fun setupFilterSpinner() {
        viewModel.matkulOptions.observe(viewLifecycleOwner) { options ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                options
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            filterSpinner.adapter = adapter

            filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.updateSelectedMatkul(options[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun observeViewModel() {
        viewModel.filteredSlides.observe(viewLifecycleOwner) { slides ->
            slideAdapter.submitList(slides)
            updateEmptyState(slides)
            updateResultCount(slides.size)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            updateClearButtonVisibility(query)
        }
    }

    private fun updateClearButtonVisibility(query: String) {
        clearButton.visibility = if (query.isNotBlank()) View.VISIBLE else View.GONE
    }

    private fun updateEmptyState(slides: List<SlideWithMatkulModel>) {
        if (slides.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            val query = viewModel.searchQuery.value
            val selectedMatkul = viewModel.selectedMatkul.value

            emptyStateText.text = when {
                !query.isNullOrBlank() -> "Tidak ditemukan slide dengan judul \"$query\""
                selectedMatkul != "Semua" -> "Tidak ada slide untuk mata kuliah $selectedMatkul"
                else -> "Tidak ada slide tersedia"
            }
        } else {
            emptyStateLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun updateResultCount(count: Int) {
        resultCountText.text = when {
            count == 0 -> ""
            else -> "$count slide ditemukan"
        }
        resultCountText.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    private fun openPDFViewer(slide: SlideWithMatkulModel) {
        val fragment = PDFViewerFragment.newInstance(slide.judul, slide.link)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}