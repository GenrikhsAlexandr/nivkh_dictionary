package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.aleksandrgenrikhs.nivkhdictionary.databinding.WordDetailsBottomsheetBinding
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WordDetailsBottomSheet(
    private val word: Word
) : BottomSheetDialogFragment() {

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
        return binding.root
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