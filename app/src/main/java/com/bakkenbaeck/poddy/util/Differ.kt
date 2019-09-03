package com.bakkenbaeck.poddy.util

import androidx.recyclerview.widget.DiffUtil

interface Diffable {
    val id: String
}

class Differ<T : Diffable>(
    private val old: List<T>,
    private val new: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size
    override fun getNewListSize() = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
    }
}
