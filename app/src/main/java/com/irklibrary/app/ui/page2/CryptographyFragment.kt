package com.irklibrary.app.ui.page2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.irklibrary.app.R
import com.irklibrary.app.data.models.CryptographyTab

class CryptographyFragment : Fragment() {

    private lateinit var viewModel: CryptographyViewModel

    // Navigation elements
    private lateinit var btnCaesar: MaterialButton
    private lateinit var btnRsa: MaterialButton
    private lateinit var caesarContent: LinearLayout
    private lateinit var rsaContent: LinearLayout

    // Caesar Cipher elements
    private lateinit var etInputText: TextInputEditText
    private lateinit var etShift: TextInputEditText
    private lateinit var etResult: TextInputEditText
    private lateinit var btnEncrypt: MaterialButton
    private lateinit var btnDecrypt: MaterialButton
    private lateinit var btnClearCaesar: MaterialButton
    private lateinit var btnShowSteps: MaterialButton
    private lateinit var btnCopyResult: MaterialButton
    private lateinit var btnCopySteps: MaterialButton
    private lateinit var cardSteps: MaterialCardView
    private lateinit var tvSteps: TextView

    // RSA elements
    private lateinit var etPValue: TextInputEditText
    private lateinit var etQValue: TextInputEditText
    private lateinit var etRsaInputText: TextInputEditText
    private lateinit var etRsaResult: TextInputEditText
    private lateinit var btnGenerateKeys: MaterialButton
    private lateinit var btnRsaEncrypt: MaterialButton
    private lateinit var btnRsaDecrypt: MaterialButton
    private lateinit var btnClearRsa: MaterialButton
    private lateinit var btnShowRsaSteps: MaterialButton
    private lateinit var btnCopyRsaResult: MaterialButton
    private lateinit var cardKeys: MaterialCardView
    private lateinit var tvPublicKey: TextView
    private lateinit var tvPrivateKey: TextView
    private lateinit var layoutRsaSections: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cryptography, container, false)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[CryptographyViewModel::class.java]

        initViews(view)
        setupClickListeners()
        setupTextWatchers()
        observeViewModel()

        return view
    }

    private fun initViews(view: View) {
        // Navigation buttons
        btnCaesar = view.findViewById(R.id.btn_caesar)
        btnRsa = view.findViewById(R.id.btn_rsa)
        caesarContent = view.findViewById(R.id.caesar_content)
        rsaContent = view.findViewById(R.id.rsa_content)

        // Caesar cipher elements
        etInputText = view.findViewById(R.id.et_input_text)
        etShift = view.findViewById(R.id.et_shift)
        etResult = view.findViewById(R.id.et_result)
        btnEncrypt = view.findViewById(R.id.btn_encrypt)
        btnDecrypt = view.findViewById(R.id.btn_decrypt)
        btnClearCaesar = view.findViewById(R.id.btn_clear_caesar)
        btnShowSteps = view.findViewById(R.id.btn_show_steps)
        btnCopyResult = view.findViewById(R.id.btn_copy_result)
        btnCopySteps = view.findViewById(R.id.btn_copy_steps)
        cardSteps = view.findViewById(R.id.card_steps)
        tvSteps = view.findViewById(R.id.tv_steps)

        // RSA elements
        etPValue = view.findViewById(R.id.et_p_value)
        etQValue = view.findViewById(R.id.et_q_value)
        etRsaInputText = view.findViewById(R.id.et_rsa_input_text)
        etRsaResult = view.findViewById(R.id.et_rsa_result)
        btnGenerateKeys = view.findViewById(R.id.btn_generate_keys)
        btnRsaEncrypt = view.findViewById(R.id.btn_rsa_encrypt)
        btnRsaDecrypt = view.findViewById(R.id.btn_rsa_decrypt)
        btnClearRsa = view.findViewById(R.id.btn_clear_rsa)
        btnShowRsaSteps = view.findViewById(R.id.btn_show_rsa_steps)
        btnCopyRsaResult = view.findViewById(R.id.btn_copy_rsa_result)
        cardKeys = view.findViewById(R.id.card_keys)
        tvPublicKey = view.findViewById(R.id.tv_public_key)
        tvPrivateKey = view.findViewById(R.id.tv_private_key)
        layoutRsaSections = view.findViewById(R.id.layout_rsa_sections)
    }

    private fun setupClickListeners() {
        btnCaesar.setOnClickListener {
            viewModel.switchTab(CryptographyTab.CAESAR_CIPHER)
        }

        btnRsa.setOnClickListener {
            viewModel.switchTab(CryptographyTab.RSA)
        }

        btnEncrypt.setOnClickListener {
            viewModel.performEncryption()
        }

        btnDecrypt.setOnClickListener {
            viewModel.performDecryption()
        }

        btnClearCaesar.setOnClickListener {
            viewModel.clearCaesarCipher()
        }

        btnShowSteps.setOnClickListener {
            viewModel.toggleStepsVisibility()
        }

        btnCopyResult.setOnClickListener {
            copyToClipboard("Result", viewModel.copyResult())
        }

        btnCopySteps.setOnClickListener {
            copyToClipboard("Steps", viewModel.copySteps())
        }

        // RSA operations
        btnGenerateKeys.setOnClickListener {
            viewModel.generateRsaKeys()
        }

        btnRsaEncrypt.setOnClickListener {
            viewModel.performEncryption()
        }

        btnRsaDecrypt.setOnClickListener {
            viewModel.performDecryption()
        }

        btnClearRsa.setOnClickListener {
            viewModel.clearRsaCipher()
        }

        btnShowRsaSteps.setOnClickListener {
            viewModel.toggleStepsVisibility()
        }

        btnCopyRsaResult.setOnClickListener {
            copyToClipboard("RSA Result", viewModel.copyResult())
        }
    }

    private fun setupTextWatchers() {
        etInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateInputText(s.toString())
            }
        })

        etShift.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateShiftValue(s.toString())
            }
        })

        etPValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updatePValue(s.toString())
            }
        })

        etQValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateQValue(s.toString())
            }
        })

        etRsaInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateInputText(s.toString())
            }
        })
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: com.irklibrary.app.data.models.CryptographyState) {
        when (state.currentTab) {
            CryptographyTab.CAESAR_CIPHER -> showCaesarTab()
            CryptographyTab.RSA -> showRsaTab()
        }

        if (etInputText.text.toString() != state.inputText && state.currentTab == CryptographyTab.CAESAR_CIPHER) {
            etInputText.setText(state.inputText)
        }
        if (etRsaInputText.text.toString() != state.inputText && state.currentTab == CryptographyTab.RSA) {
            etRsaInputText.setText(state.inputText)
        }
        if (etShift.text.toString() != state.shiftValue) {
            etShift.setText(state.shiftValue)
        }
        if (etPValue.text.toString() != state.pValue) {
            etPValue.setText(state.pValue)
        }
        if (etQValue.text.toString() != state.qValue) {
            etQValue.setText(state.qValue)
        }

        when (state.currentTab) {
            CryptographyTab.CAESAR_CIPHER -> {
                etResult.setText(state.result)
                btnCopyResult.visibility = if (state.showCopyButton) View.VISIBLE else View.GONE
                btnShowSteps.visibility = if (state.showStepsButton) View.VISIBLE else View.GONE
                cardSteps.visibility = if (state.isStepsVisible) View.VISIBLE else View.GONE
                tvSteps.text = state.steps
            }
            CryptographyTab.RSA -> {
                etRsaResult.setText(state.result)
                btnCopyRsaResult.visibility = if (state.showCopyButton) View.VISIBLE else View.GONE

                if (state.publicKey != null && state.privateKey != null) {
                    cardKeys.visibility = View.VISIBLE
                    tvPublicKey.text = "Kunci Publik (n, e): (${state.publicKey.n}, ${state.publicKey.key})"
                    tvPrivateKey.text = "Kunci Privat (n, d): (${state.privateKey.n}, ${state.privateKey.key})"
                } else {
                    cardKeys.visibility = View.GONE
                    tvPublicKey.text = ""
                    tvPrivateKey.text = ""
                }

                btnShowRsaSteps.visibility = if (state.showStepsButton) View.VISIBLE else View.GONE
                layoutRsaSections.visibility = if (state.isStepsVisible) View.VISIBLE else View.GONE
                updateRsaSections(state.rsaStepSections)

                btnGenerateKeys.visibility = if (state.showGenerateKeys) View.VISIBLE else View.GONE
            }
        }

        val stepsButtonText = if (state.isStepsVisible) {
            "Sembunyikan Step-by-Step Process"
        } else {
            "Tampilkan Step-by-Step Process"
        }
        btnShowSteps.text = stepsButtonText
        btnShowRsaSteps.text = stepsButtonText

        btnEncrypt.isEnabled = !state.isLoading
        btnDecrypt.isEnabled = !state.isLoading
        btnClearCaesar.isEnabled = !state.isLoading
        btnRsaEncrypt.isEnabled = !state.isLoading
        btnRsaDecrypt.isEnabled = !state.isLoading
        btnClearRsa.isEnabled = !state.isLoading
        btnGenerateKeys.isEnabled = !state.isLoading

        if (state.isLoading) {
            when (state.currentTab) {
                CryptographyTab.CAESAR_CIPHER -> etResult.setText("Memproses...")
                CryptographyTab.RSA -> etRsaResult.setText("Memproses...")
            }
        }
    }

    private fun showCaesarTab() {
        caesarContent.visibility = View.VISIBLE
        rsaContent.visibility = View.GONE

        context?.let { ctx ->
            btnCaesar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.primary))
            btnCaesar.setTextColor(ContextCompat.getColor(ctx, R.color.on_primary))
            btnCaesar.strokeWidth = 0

            btnRsa.setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.transparent))
            btnRsa.setTextColor(ContextCompat.getColor(ctx, R.color.primary))
            btnRsa.strokeWidth = dpToPx(1f).toInt()
            btnRsa.strokeColor = ContextCompat.getColorStateList(ctx, R.color.outline)
        }
    }

    private fun showRsaTab() {
        caesarContent.visibility = View.GONE
        rsaContent.visibility = View.VISIBLE

        context?.let { ctx ->
            btnRsa.setBackgroundColor(ContextCompat.getColor(ctx, R.color.primary))
            btnRsa.setTextColor(ContextCompat.getColor(ctx, R.color.on_primary))
            btnRsa.strokeWidth = 0

            btnCaesar.setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.transparent))
            btnCaesar.setTextColor(ContextCompat.getColor(ctx, R.color.primary))
            btnCaesar.strokeWidth = dpToPx(1f).toInt()
            btnCaesar.strokeColor = ContextCompat.getColorStateList(ctx, R.color.outline)
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun updateRsaSections(sections: List<com.irklibrary.app.data.models.RsaStepSection>) {
        layoutRsaSections.removeAllViews()

        sections.forEachIndexed { index, section ->
            val sectionView = createExpandableSection(section, index)
            layoutRsaSections.addView(sectionView)
        }
    }

    private fun createExpandableSection(section: com.irklibrary.app.data.models.RsaStepSection, index: Int): MaterialCardView {
        val card = MaterialCardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            cardElevation = 4f
            radius = 8f
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val headerLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 32)
        }

        val titleText = TextView(requireContext()).apply {
            text = section.title
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val expandIcon = TextView(requireContext()).apply {
            text = if (section.isExpanded) "▼" else "▶"
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        headerLayout.addView(titleText)
        headerLayout.addView(expandIcon)

        val contentText = TextView(requireContext()).apply {
            text = section.content
            textSize = 12f
            typeface = android.graphics.Typeface.MONOSPACE
            visibility = if (section.isExpanded) View.VISIBLE else View.GONE
        }

        container.addView(headerLayout)
        container.addView(contentText)
        card.addView(container)

        headerLayout.setOnClickListener {
            viewModel.toggleRsaSection(index)
        }

        return card
    }

    private fun copyToClipboard(label: String, text: String) {
        if (text.isEmpty()) {
            showToast("Tidak ada teks untuk disalin")
            return
        }

        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clipData)

        showToast("$label berhasil disalin ke clipboard")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}