package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentEnglishBinding

class EnglishFragment : Fragment() {

    companion object {
        fun newInstance() = EnglishFragment()
    }

    private var _binding: FragmentEnglishBinding? = null
    private val binding: FragmentEnglishBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnglishBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}