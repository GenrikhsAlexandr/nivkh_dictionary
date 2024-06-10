package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.WordDetailsBottomsheetBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

class WordDetailsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "WordDetailsBottomSheet"

        fun show(fragmentManager: FragmentManager) {
            val wordDetailsBottomSheet = WordDetailsBottomSheet()
            wordDetailsBottomSheet.show(
                fragmentManager,
                TAG
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by activityViewModels { viewModelFactory }
    private var _binding: WordDetailsBottomsheetBinding? = null
    private val binding: WordDetailsBottomsheetBinding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as ComponentProvider).provideComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = WordDetailsBottomsheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritesButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onFavoriteButtonClicked()
                dismiss()
            }
        }
        viewModel.initPlayer()
        subscribe()
        clickSpeakButton()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.isFavorite() == true) {
                    binding.favoritesButton.setIconResource(R.drawable.ic_favorites)
                } else {
                    binding.favoritesButton.setIconResource(R.drawable.ic_favorites_no_selected)
                }
            }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSelected.collect { word ->
                with(binding) {
                    nvWord.text =
                        word?.locales?.get(Language.NIVKH.code)?.value
                            ?: getString(R.string.no_data)
                    ruWord.text =
                        word?.locales?.get(Language.RUSSIAN.code)?.value
                            ?: getString(R.string.no_data)
                    enWord.text =
                        word?.locales?.get(Language.ENGLISH.code)?.value
                            ?: getString(R.string.no_data)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isButtonVisible.collect { isVisible ->
                binding.speakButton.isVisible = isVisible
            }
        }
    }

    private fun clickSpeakButton() {
        binding.speakButton.setOnClickListener {
            viewModel.speakWord()
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.destroyPlayer()
    }
}