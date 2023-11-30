package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentRussianBinding

class RussianFragment : Fragment() {

    companion object {
        fun newInstance() = RussianFragment()
    }

    private var _binding: FragmentRussianBinding? = null
    private val binding: FragmentRussianBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRussianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}