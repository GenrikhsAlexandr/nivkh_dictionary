package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.WordDetailsBottomsheetBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class WordDetailsBottomSheet(
    private val word: Word
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as ComponentProvider).provideComponent()
            .inject(this)
    }

    companion object {
        private const val TAG = "WordDetailsBottomSheet"

        fun show(word: Word, fragmentManager: FragmentManager) {
            val wordDetailsBottomSheet = WordDetailsBottomSheet(word)
            wordDetailsBottomSheet.show(
                fragmentManager,
                TAG
            )
        }
    }

    private var _binding: WordDetailsBottomsheetBinding? = null
    private val binding: WordDetailsBottomsheetBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = WordDetailsBottomsheetBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            nvWord.text = word.locales["nv"]?.value ?: "Nnivh word"
            enWord.text = word.locales["en"]?.value ?: "English word"
            ruWord.text = word.locales["ru"]?.value ?: "Russian word"
        }
        binding.btSaved.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onFavoriteButtonClicked()
                delay(100)
                dismiss()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isWordDetail.value = true
        }
        getWord(word)
        subscribe()
        viewModel.isSearchViewVisible.value = false
        return binding.root
    }

    private fun getWord(word: Word) {
        viewModel.isFavoritesWord(word)
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isIconClick.collect { isIconClick ->
                if (isIconClick) {
                    binding.btSaved.setIconResource(R.drawable.ic_favorites)
                } else {
                    binding.btSaved.setIconResource(R.drawable.ic_favorites_no_selected)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                if (isFavorite) {
                    viewModel.isIconClick.value = true
                    binding.btSaved.setIconResource(R.drawable.ic_favorites)
                } else {
                    viewModel.isIconClick.value = false
                    binding.btSaved.setIconResource(R.drawable.ic_favorites_no_selected)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastMessage.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}