package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentNivkhBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.presentation.ErrorActivity
import com.aleksandrgenrikhs.nivkhdictionary.presentation.MainViewModel
import com.aleksandrgenrikhs.nivkhdictionary.presentation.WordDetailsBottomSheet
import com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter.WordAdapter
import com.aleksandrgenrikhs.nivkhdictionary.utils.ResultState
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.NIVKH
import kotlinx.coroutines.launch
import javax.inject.Inject

class NivkhFragment : Fragment() {

    companion object {
        fun newInstance() = NivkhFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavoriteFragment.value = false
        }
        binding.rvWord.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.rvWord.adapter = adapter
        subscribe()
        getLocale()
       // refresh()
        getWordFirstStartApp()
    }

    private fun getLocale() {
        val locale = NIVKH
        viewModel.setLocale(locale)
    }

    private fun getWordFirstStartApp() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (viewModel.getWordStartApp()) {
                is ResultState.Error -> startErrorActivity()
                is ResultState.Success -> viewModel.getWords()
            }
        }
    }

    /*private fun refresh() {
        val swipeRefresh: SwipeRefreshLayout = binding.swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.ic_launcher_background)
        swipeRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                when (val updateWord = viewModel.updateWords()) {
                    is ResultState.Success -> {
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
*/
    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.words.collect { words ->
                adapter.submitList(words)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isProgressBarVisible.collect {
                binding.progressBar.isVisible = it
                println("progressBar =$it")
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRvWordVisible.collect {
                binding.rvWord.isVisible = it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onDestroy()
    }

    private fun startErrorActivity() {
        val intent = Intent(requireContext(), ErrorActivity::class.java)
        startActivity(intent)
    }
}