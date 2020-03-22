package com.bakkenbaeck.poddy.presentation.modal

import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.bakkenbaeck.poddy.ACTION_START
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getScreenHeight
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.presentation.feed.FeedViewModel
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.service.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.detail_sheet.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val EPISODE = "EPISODE"

class DetailsFragment : BaseBottomDialogFragment() {

    companion object {
        fun newInstance(episode: ViewEpisode): DetailsFragment {
            return DetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EPISODE, episode)
                }
            }
        }
    }

    private val feedViewModel: FeedViewModel by viewModel()

    override fun getLayout() = R.layout.detail_sheet

    override fun init(bundle: Bundle?) {
        initViewHeight()
        initView()
        val episode = getEpisode()
        updateSheetStateToExpanded(episode)
    }

    private fun getEpisode(): ViewEpisode {
        return arguments?.getParcelable(EPISODE)
            ?: throw IllegalStateException("Episode can't be null")
    }

    private fun initViewHeight() {
        val dialog = dialog as BottomSheetDialog
        dialog.behavior.peekHeight = getScreenHeight()
    }

    private fun initView() {
        play.setOnClickListener { handlePlayClicked() }
        download.setOnClickListener { handleDownloadClicked() }
        queue.setOnClickListener { feedViewModel.addToQueue() }
    }

    private fun handleDownloadClicked() {
        val selectedEpisode = getEpisode()
        handleDownloadClicked(selectedEpisode)
    }

    private fun handlePlayClicked() {
        val episode = getEpisode()

        startForegroundService<PlayerService> {
            action = ACTION_START
            putExtra(EPISODE, episode)
        }
    }

    private fun handleDownloadClicked(episode: ViewEpisode) {
        startForegroundService<DownloadService> {
            putExtra(ID, episode.id)
            putExtra(URL, episode.audio)
            putExtra(NAME, episode.title)
        }
    }

    private fun updateSheetStateToExpanded(episode: ViewEpisode) {
        image.loadWithRoundCorners(episode.image)
        episodeName.text = episode.title
        description.text = HtmlCompat.fromHtml(
            episode.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        downloadProgress.text = ""

        val isSelectedEpisodePlaying = feedViewModel.isEpisodePlaying(episode)
        val playResource =
            if (isSelectedEpisodePlaying) R.drawable.ic_player_pause else R.drawable.ic_player_play
        play.setImageResource(playResource)

        feedViewModel.setCurrentEpisode(episode)
    }
}
