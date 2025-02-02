package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.aleksandrgenrikhs.nivkhalphabetcompose.presentation.ui.theme.NivkhDictionaryTheme
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentHomeBinding
import com.aleksandrgenrikhs.nivkhdictionary.presentation.mainscreen.TabLanguage

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return ComposeView(requireContext()).apply {
            setContent {
                NivkhDictionaryTheme {
                    TabLanguage()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
