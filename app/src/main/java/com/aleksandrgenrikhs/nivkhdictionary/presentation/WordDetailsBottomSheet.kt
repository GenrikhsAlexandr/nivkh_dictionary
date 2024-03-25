package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
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
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.utils.NetworkConnected
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
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
        ).apply {
            viewLifecycleOwner.lifecycleScope.launch {
                nvWord.text = word.locales[Language.NIVKH.code]?.value ?: "Nnivh word"
                enWord.text = word.locales[Language.ENGLISH.code]?.value ?: "English word"
                ruWord.text = word.locales[Language.RUSSIAN.code]?.value ?: "Russian word"
            }
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
        sound()
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

    private fun createPlayer(): MediaPlayer? {
        val nvLocale = word.locales[Language.NIVKH.code] ?: return null
        val url = "${nvLocale.audioPath}"
        return MediaPlayer.create(context, Uri.parse(url))
    }

    private fun sound() {
        binding.speakButton.setOnClickListener {
            if (NetworkConnected.isNetworkConnected(requireContext())) {
                if (player?.isPlaying == true) return@setOnClickListener
                try {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        createPlayer()?.start()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    context, R.string.error_message,
                    Toast.LENGTH_LONG
                ).show()
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