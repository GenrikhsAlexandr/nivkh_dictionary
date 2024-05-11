package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.WordDetailsBottomsheetBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
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
        binding.favoritesButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onFavoriteButtonClicked()
                delay(1)
                dismiss()
            }
        }
        subscribe()
        playWord()
        return binding.root
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                if (isFavorite) {
                    binding.favoritesButton.setIconResource(R.drawable.ic_favorites)
                } else {
                    binding.favoritesButton.setIconResource(R.drawable.ic_favorites_no_selected)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastMessage.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastMessageError.collect { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
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
            viewModel.isClickable.collect { isClickable ->
                binding.speakButton.isClickable = isClickable
            }
        }
    }

    private fun playWord() {
        binding.speakButton.setOnClickListener {
            viewModel.playWord()
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.destroyPlayer()
    }
}