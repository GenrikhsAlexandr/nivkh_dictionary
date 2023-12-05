package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentNivkhBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.presentation.MainViewModel
import com.aleksandrgenrikhs.nivkhdictionary.presentation.WordDetailsBottomSheet
import com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter.WordAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

class NivkhFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }


    companion object {
        fun newInstance() = NivkhFragment()
    }

    private var _binding: FragmentNivkhBinding? = null
    private val binding: FragmentNivkhBinding get() = _binding!!

    private val adapter: WordAdapter = WordAdapter(
        onWordClick = { word ->
            WordDetailsBottomSheet.show(
                word, fragmentManager = childFragmentManager
            )
        }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as ComponentProvider).provideComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNivkhBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllWords()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvWord.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.rvWord.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterWords.collect { words ->
                binding.progressBar.isVisible = words.isEmpty()
                binding.rvWord.isVisible = words.isNotEmpty()
                adapter.submitList(words)
            }
        }

        val swipeRefresh: SwipeRefreshLayout = binding.swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.ic_launcher_background)
        swipeRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllWords()
                }
            swipeRefresh.isRefreshing = false
        }
        getLocale()
    }

    private fun getLocale() {
        val locale = "nv"
        viewModel.setLocale(locale)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}