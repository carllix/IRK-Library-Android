package com.irklibrary.app.ui.page4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.irklibrary.app.R

class PDFViewerFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    private var slideTitle: String? = null
    private var pdfUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            slideTitle = it.getString(ARG_TITLE)
            pdfUrl = it.getString(ARG_PDF_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pdf_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupToolbar()
        setupWebView()
        loadPDF()
    }

    private fun initViews(view: View) {
        titleTextView = view.findViewById(R.id.tv_slide_title)
        backButton = view.findViewById(R.id.btn_back)
        webView = view.findViewById(R.id.webview)
        progressBar = view.findViewById(R.id.progress_bar)
    }

    private fun setupToolbar() {
        titleTextView.text = slideTitle

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: android.webkit.WebResourceRequest?,
                    error: android.webkit.WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun loadPDF() {
        pdfUrl?.let { url ->
            val googleDocsUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=$url"
            webView.loadUrl(googleDocsUrl)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.destroy()
    }

    companion object {
        private const val ARG_TITLE = "slide_title"
        private const val ARG_PDF_URL = "pdf_url"

        fun newInstance(title: String, pdfUrl: String) = PDFViewerFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_PDF_URL, pdfUrl)
            }
        }
    }
}