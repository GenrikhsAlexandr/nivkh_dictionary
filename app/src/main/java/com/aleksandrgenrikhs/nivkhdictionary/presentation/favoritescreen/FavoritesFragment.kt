package com.aleksandrgenrikhs.nivkhdictionary.presentation.favoritescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
    }


   //  val viewModel: FavoriteViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
          //      LaunchedEffect(Unit) { viewModel.getFavoritesWords() }
              //  val uiState by viewModel.viewState.collectAsStateWithLifecycle()
//                WordList(
//                    state = uiState,
//                    words = wordsList.favoriteWords,
//                    locale = Language.NIVKH.code,
//                    onWordClick = {
//                        viewModel.onWordClicked(it)
//                        WordDetailsBottomSheet.show(
//                            fragmentManager = childFragmentManager
//                        )
//                    }
//                )
            }
        }
    }
}
