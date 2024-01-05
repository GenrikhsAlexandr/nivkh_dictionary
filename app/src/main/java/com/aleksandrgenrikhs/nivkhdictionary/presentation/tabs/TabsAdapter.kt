package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> RussianFragment.newInstance()
            2 -> EnglishFragment.newInstance()
            else -> NivkhFragment.newInstance()
        }
    }
}