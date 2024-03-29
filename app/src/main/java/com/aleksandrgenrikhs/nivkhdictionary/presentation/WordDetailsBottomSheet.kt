package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.media.MediaPlayer
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
    private var player: MediaPlayer? = null

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
        binding.btSaved.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onFavoriteButtonClicked()
                delay(100)
                dismiss()
            }
        }

        subscribe()
        sound()
        return binding.root
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                if (isFavorite) {
                    binding.btSaved.setIconResource(R.drawable.ic_favorites)
                } else {
                    binding.btSaved.setIconResource(R.drawable.ic_favorites_no_selected)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastMessage.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSelected.collect {
                with(binding) {
                    nvWord.text =
                        viewModel.isSelected.value?.locales?.get(Language.NIVKH.code)?.value ?: ""
                    ruWord.text =
                        viewModel.isSelected.value?.locales?.get(Language.RUSSIAN.code)?.value ?: ""
                    enWord.text =
                        viewModel.isSelected.value?.locales?.get(Language.ENGLISH.code)?.value ?: ""
                }
            }
        }
    }

    private fun sound() {
        binding.speakButton.setOnClickListener {
            viewModel.play()
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.wordDetailsDestroy()
    }
}