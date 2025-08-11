package com.irklibrary.app.ui.page3

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.irklibrary.app.R

class HuffmanFragment : Fragment() {

    private lateinit var viewModel: HuffmanViewModel

    // Views
    private lateinit var tilInput: TextInputLayout
    private lateinit var etInput: TextInputEditText
    private lateinit var btnProcess: MaterialButton
    private lateinit var btnClear: MaterialButton
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var llResultsContainer: LinearLayout
    private lateinit var tvCompressionInfo: TextView

    // Tree Animation Section
    private lateinit var llTreeHeader: LinearLayout
    private lateinit var ivTreeExpand: ImageView
    private lateinit var llTreeContent: LinearLayout
    private lateinit var huffmanTreeView: HuffmanTreeView
    private lateinit var btnResetZoom: MaterialButton

    // Construction Steps Section
    private lateinit var llStepsHeader: LinearLayout
    private lateinit var ivStepsExpand: ImageView
    private lateinit var rvConstructionSteps: RecyclerView
    private lateinit var stepsAdapter: ConstructionStepsAdapter

    // Encoding Section
    private lateinit var llEncodingHeader: LinearLayout
    private lateinit var ivEncodingExpand: ImageView
    private lateinit var llEncodingContent: LinearLayout
    private lateinit var rvCodeTable: RecyclerView
    private lateinit var codeAdapter: HuffmanCodeAdapter
    private lateinit var tvEncodedText: TextView
    private lateinit var btnCopyEncoded: MaterialButton

    private var isTreeExpanded = false
    private var isStepsExpanded = false
    private var isEncodingExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_huffman, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerViews()
        setupViewModel()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        // Input section
        tilInput = view.findViewById(R.id.til_input)
        etInput = view.findViewById(R.id.et_input)
        btnProcess = view.findViewById(R.id.btn_process)
        btnClear = view.findViewById(R.id.btn_clear)
        progressBar = view.findViewById(R.id.progress_bar)
        llResultsContainer = view.findViewById(R.id.ll_results_container)
//        tvCompressionInfo = view.findViewById(R.id.tv_compression_info)

        // Tree animation section
        llTreeHeader = view.findViewById(R.id.ll_tree_header)
        ivTreeExpand = view.findViewById(R.id.iv_tree_expand)
        llTreeContent = view.findViewById(R.id.ll_tree_content)
        huffmanTreeView = view.findViewById(R.id.huffman_tree_view)
        btnResetZoom = view.findViewById(R.id.btn_reset_zoom)

        // Construction steps section
        llStepsHeader = view.findViewById(R.id.ll_steps_header)
        ivStepsExpand = view.findViewById(R.id.iv_steps_expand)
        rvConstructionSteps = view.findViewById(R.id.rv_construction_steps)

        // Encoding section
        llEncodingHeader = view.findViewById(R.id.ll_encoding_header)
        ivEncodingExpand = view.findViewById(R.id.iv_encoding_expand)
        llEncodingContent = view.findViewById(R.id.ll_encoding_content)
        rvCodeTable = view.findViewById(R.id.rv_code_table)
        tvEncodedText = view.findViewById(R.id.tv_encoded_text)
        btnCopyEncoded = view.findViewById(R.id.btn_copy_encoded)
    }

    private fun setupRecyclerViews() {
        stepsAdapter = ConstructionStepsAdapter()
        rvConstructionSteps.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stepsAdapter
        }

        codeAdapter = HuffmanCodeAdapter { character ->
            huffmanTreeView.highlightCharacterPath(character)
        }
        rvCodeTable.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = codeAdapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HuffmanViewModel::class.java]
    }

    private fun setupClickListeners() {
        btnProcess.setOnClickListener {
            val inputText = etInput.text.toString()
            val validationError = viewModel.validateInput(inputText)

            if (validationError != null) {
                tilInput.error = validationError
                return@setOnClickListener
            }

            tilInput.error = null
            viewModel.processText(inputText)
        }

        btnClear.setOnClickListener {
            etInput.text?.clear()
            tilInput.error = null
            viewModel.clearResult()
            llResultsContainer.visibility = View.GONE
        }

        btnResetZoom.setOnClickListener {
            huffmanTreeView.resetZoomAndPan()
        }

        btnCopyEncoded.setOnClickListener {
            copyToClipboard(tvEncodedText.text.toString(), "Encoded text")
        }

        llTreeHeader.setOnClickListener { toggleTreeExpansion() }
        llStepsHeader.setOnClickListener { toggleStepsExpansion() }
        llEncodingHeader.setOnClickListener { toggleEncodingExpansion() }
    }

    private fun observeViewModel() {
        viewModel.isProcessing.observe(viewLifecycleOwner) { isProcessing ->
            progressBar.visibility = if (isProcessing) View.VISIBLE else View.GONE
            btnProcess.isEnabled = !isProcessing
            btnProcess.text = if (isProcessing) "Processing..." else "Process"
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                tilInput.error = error
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.huffmanResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                displayResults(result)
                llResultsContainer.visibility = View.VISIBLE
            } else {
                llResultsContainer.visibility = View.GONE
            }
        }
    }

    private fun displayResults(result: com.irklibrary.app.data.models.HuffmanResult) {
        huffmanTreeView.setHuffmanTree(result.huffmanTree)
        stepsAdapter.updateSteps(result.constructionSteps)
        codeAdapter.updateCodes(result.huffmanCodes)
        tvEncodedText.text = result.encodedText

        if (!isTreeExpanded) {
            toggleTreeExpansion()
        }
    }

    private fun toggleTreeExpansion() {
        isTreeExpanded = !isTreeExpanded
        animateExpansion(llTreeContent, ivTreeExpand, isTreeExpanded)
    }

    private fun toggleStepsExpansion() {
        isStepsExpanded = !isStepsExpanded
        animateExpansion(rvConstructionSteps, ivStepsExpand, isStepsExpanded)
    }

    private fun toggleEncodingExpansion() {
        isEncodingExpanded = !isEncodingExpanded
        animateExpansion(llEncodingContent, ivEncodingExpand, isEncodingExpanded)
    }

    private fun animateExpansion(targetView: View, arrowView: ImageView, isExpanding: Boolean) {
        if (isExpanding) {
            targetView.visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(arrowView, "rotation", 0f, 180f)
            animator.duration = 300
            animator.start()
        } else {
            targetView.visibility = View.GONE
            val animator = ObjectAnimator.ofFloat(arrowView, "rotation", 180f, 0f)
            animator.duration = 300
            animator.start()
        }
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}