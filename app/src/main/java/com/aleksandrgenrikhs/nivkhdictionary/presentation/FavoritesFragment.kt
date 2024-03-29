package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentFavoritesBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter.WordAdapter
import com.aleksandrgenrikhs.nivkhdictionary.utils.Strings.NIVKH
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesFragment : Fragment() {
    companion object {
        fun newInstance() = FavoritesFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by activityViewModels { viewModelFactory }
    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = _binding!!
    private val adapter: WordAdapter = WordAdapter(
        onWordClick = { word ->
            viewModel.onWordClicked(word)
            WordDetailsBottomSheet.show(
                fragmentManager = childFragmentManager
            )
        },
        locale = NIVKH
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
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvWord.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.rvWord.adapter = adapter
        subscribe()

    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoritesWords.collect { words ->
                binding.tvEmpty.isVisible = words.isEmpty()
                binding.rvWord.isVisible = words.isNotEmpty()
                adapter.submitList(words)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onDestroy()
    }
}
