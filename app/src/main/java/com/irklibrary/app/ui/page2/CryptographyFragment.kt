package com.irklibrary.app.ui.page2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.irklibrary.app.R

class CrypthographyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matrix, container, false)
        val textView: TextView = view.findViewById(R.id.fragment_title)
        textView.text = "Ini adalah Halaman Crypthography"
        return view
    }
}