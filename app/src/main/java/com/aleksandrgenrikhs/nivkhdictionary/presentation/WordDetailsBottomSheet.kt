package com.aleksandrgenrikhs.nivkhdictionary.presentation

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aleksandrgenrikhs.nivkhdictionary.R
import com.aleksandrgenrikhs.nivkhdictionary.databinding.WordDetailsBottomsheetBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class WordDetailsBottomSheet(
    private val word: Word
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var player: MediaPlayer? = null

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
            viewLifecycleOwner.lifecycleScope.launch {
                nvWord.text = word.locales["nv"]?.value ?: "Nnivh word"
                enWord.text = word.locales["en"]?.value ?: "English word"
                ruWord.text = word.locales["ru"]?.value ?: "Russian word"
                speakButton.isVisible = player == null
                //getPlayer()
                //  sound()
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

    private fun getPlayer(): MediaPlayer? {
        if (player == null) {
            player = initPlayer()
        }
        return player
    }

    private fun initPlayer(): MediaPlayer? {
        val nvLocale = word.locales[Language.NIVKH.code] ?: return null
        val audioPath = nvLocale.audioPath
        if (audioPath != null) {
            return MediaPlayer.create(context, Uri.parse(audioPath))
        } else {
            return null
        }
    }

    private fun play() {
        player?.start()
    }

    private fun sound() {
        binding.speakButton.setOnClickListener {
            if (player?.isPlaying == true) return@setOnClickListener
            try {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    play()
                }
            } catch (e: Exception) {
                e.printStackTrace()
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