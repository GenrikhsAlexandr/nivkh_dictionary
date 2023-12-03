package com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem

class WordDiffUtil : DiffUtil.ItemCallback<WordListItem>() {

    override fun areItemsTheSame(oldItem: WordListItem, newItem: WordListItem): Boolean {
        return oldItem.word.id == newItem.word.id
    }

    override fun areContentsTheSame(oldItem: WordListItem, newItem: WordListItem): Boolean {
        return oldItem == newItem
    }
}
