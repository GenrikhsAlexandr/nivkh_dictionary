package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentNivkhBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.presentation.ErrorActivity
import com.aleksandrgenrikhs.nivkhdictionary.presentation.MainViewModel
import com.aleksandrgenrikhs.nivkhdictionary.presentation.WordDetailsBottomSheet
import com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter.WordAdapter
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ERROR_MESSAGE_KEY = "error_message"

class NivkhFragment : Fragment() {

    companion object {
        fun newInstance() = NivkhFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels() { viewModelFactory }
    private var _binding: FragmentNivkhBinding? = null
    private val binding: FragmentNivkhBinding get() = _binding!!
    private val adapter: WordAdapter = WordAdapter(
        onWordClick = {
            viewModel.onWordClicked(it)
            WordDetailsBottomSheet.show(
                fragmentManager = childFragmentManager
            )
        },
        locale = Language.NIVKH.code
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvWord.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.rvWord.adapter = adapter
        subscribe()
        refresh()
        getWords()
    }

    private fun getWords() {
        viewModel.viewModelScope.launch {
            when (val word = viewModel.getWordsForStart()) {
                is ResultState.Success -> viewModel.getWords()
                is ResultState.Error ->
                    startErrorActivity(word.message)
            }
        }
    }

    private fun refresh() {
        val swipeRefresh: SwipeRefreshLayout = binding.swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.ic_launcher_background)
        swipeRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                when (val updateWord = viewModel.updateWords()) {
                    is ResultState.Success -> {
                        viewModel.getWords()
                        Toast.makeText(
                            requireContext(),
                            R.string.update_words_title,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is ResultState.Error -> Toast.makeText(
                        requireContext(),
                        getString(updateWord.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                swipeRefresh.isRefreshing = false
            }
        }
    }
    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.words.collect { words ->
                adapter.submitList(words)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isProgressBarVisible.collect {
                binding.progressBar.isVisible = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRvWordVisible.collect {
                binding.rvWord.isVisible = it
            }
        }
    }

    private fun startErrorActivity(errorMessage: Int) {
        val intent = Intent(requireContext(), ErrorActivity::class.java)
        intent.putExtra(ERROR_MESSAGE_KEY, errorMessage)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.searchDestroy()
    }
}