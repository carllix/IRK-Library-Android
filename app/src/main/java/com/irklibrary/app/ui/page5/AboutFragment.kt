package com.irklibrary.app.ui.page5

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.irklibrary.app.R
import androidx.core.net.toUri

class AboutFragment : Fragment() {
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var llEmail: LinearLayout
    private lateinit var llLine: LinearLayout
    private lateinit var llInstagram: LinearLayout
    private lateinit var llLinkedin: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupClickListeners()
    }

    private fun initViews(view: View) {
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo)
        llEmail = view.findViewById(R.id.ll_email)
        llLine = view.findViewById(R.id.ll_line)
        llInstagram = view.findViewById(R.id.ll_instagram)
        llLinkedin = view.findViewById(R.id.ll_linkedin)
    }

    private fun setupClickListeners() {
        llEmail.setOnClickListener {
            val email = getString(R.string.about_email)
            copyToClipboard(email, "Email")
            openEmailApp(email)
        }

        llLine.setOnClickListener {
            val lineId = getString(R.string.about_line_id)
            copyToClipboard(lineId, "Line ID")
            showToast("Line ID disalin ke clipboard")
        }

        llInstagram.setOnClickListener {
            val username = getString(R.string.about_instagram)
            openInstagramProfile(username)
        }

        llLinkedin.setOnClickListener {
            val username = getString(R.string.about_linkedin)
            openLinkedinProfile(username)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openEmailApp(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:$email".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "Hai")
            }

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                showToast("Email app tidak ditemukan")
            }
        } catch (e: Exception) {
            showToast("Tidak dapat membuka email app")
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openInstagramProfile(username: String) {
        try {
            val instagramIntent = Intent(Intent.ACTION_VIEW,
                "http://instagram.com/_u/$username".toUri())
            instagramIntent.setPackage("com.instagram.android")

            if (instagramIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(instagramIntent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, "https://instagram.com/$username".toUri())
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            showToast("Tidak dapat membuka Instagram")
        }
    }

    private fun openLinkedinProfile(username: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.linkedin.com/in/$username".toUri())
            startActivity(intent)
        } catch (e: Exception) {
            showToast("Tidak dapat membuka LinkedIn")
        }
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}