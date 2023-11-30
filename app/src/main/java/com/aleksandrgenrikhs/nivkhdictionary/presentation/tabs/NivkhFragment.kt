package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentNivkhBinding

class NivkhFragment : Fragment() {

    companion object {
        fun newInstance() = NivkhFragment()
    }

    private var _binding: FragmentNivkhBinding? = null
    private val binding: FragmentNivkhBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNivkhBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}