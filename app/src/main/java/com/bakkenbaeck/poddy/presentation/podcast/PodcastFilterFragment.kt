package com.bakkenbaeck.poddy.presentation.podcast

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.modal.BaseBottomDialogFragment
import kotlinx.android.synthetic.main.podcast_filter_fragment.*

class PodcastFilterFragment : BaseBottomDialogFragment() {
    override fun getLayout() = R.layout.podcast_filter_fragment

    override fun init(bundle: Bundle?) {
        initAdapter()
    }

    private fun initAdapter() {
        val filters = listOf(
            PodcastFilter(1, "Podcast name"),
            PodcastFilter(4, "Episode release date"),
            PodcastFilter(2, "Date added"),
            PodcastFilter(3, "Random")
        )

        filterList.apply {
            adapter = PodcastFilterAdapter(filters.toMutableList()) {
                handleFilterClicked(it)
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun handleFilterClicked(filter: PodcastFilter) {

    }
}