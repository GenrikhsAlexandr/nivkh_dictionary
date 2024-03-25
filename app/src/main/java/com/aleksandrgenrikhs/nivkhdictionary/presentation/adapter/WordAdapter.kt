package com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aleksandrgenrikhs.nivkhdictionary.databinding.ListItemWordBinding
import com.aleksandrgenrikhs.nivkhdictionary.domain.Word
import com.aleksandrgenrikhs.nivkhdictionary.domain.WordListItem

class WordAdapter(
    val onWordClick: (Word) -> Unit,
    val locale: String
) : ListAdapter<WordListItem, WordAdapter.WordViewHolder>(WordDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            ListItemWordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    inner class WordViewHolder(private val binding: ListItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listItem: WordListItem) {
            binding.listItemWord.text = listItem.getTitle(locale)
            binding.listItemWord.setOnClickListener {
                onWordClick(listItem.word)
            }
        }
    }
}