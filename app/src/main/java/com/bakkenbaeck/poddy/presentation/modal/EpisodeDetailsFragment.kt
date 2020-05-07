package com.bakkenbaeck.poddy.presentation.modal

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getPlayIcon
import com.bakkenbaeck.poddy.extensions.getScreenHeight
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.presentation.feed.DetailViewModel
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.player.ACTION_START
import com.bakkenbaeck.poddy.presentation.player.EPISODE
import com.bakkenbaeck.poddy.presentation.service.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.detail_sheet.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EpisodeDetailsFragment : BaseBottomDialogFragment() {

    private val detailViewModel: DetailViewModel by viewModel()

    override fun getLayout() = R.layout.detail_sheet

    override fun init(bundle: Bundle?) {
        initViewHeight()
        initView()
        initObservers()
        val episode = getEpisode() ?: return
        updateSheetStateToExpanded(episode)
        detailViewModel.setCurrentEpisode(episode)
    }

    private fun getEpisode(): ViewEpisode? {
        val arguments = arguments ?: return null
        val args = EpisodeDetailsFragmentArgs.fromBundle(arguments)
        return args.episode
    }

    private fun initViewHeight() {
        val dialog = dialog as BottomSheetDialog
        dialog.behavior.peekHeight = getScreenHeight()
    }

    private fun initView() {
        play.setOnClickListener { handlePlayClicked() }
        download.setOnClickListener { handleDownloadClicked() }
        queue.setOnClickListener { detailViewModel.addToQueue() }
    }

    private fun handleDownloadClicked() {
        val selectedEpisode = getEpisode() ?: return
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

    private fun initObservers() {
        detailViewModel.playerUpdates.observe(this, Observer {
            handlePlayerUpdates(it)
        })
    }

    private fun handlePlayerUpdates(action: ViewPlayerAction) {
        when (action) {
            is ViewPlayerAction.Progress -> {} // Do nothing
            else -> updatePlayerUi(action)
        }
    }

    private fun updatePlayerUi(action: ViewPlayerAction) {
        val drawable = action.getPlayIcon() ?: return
        play.setImageResource(drawable)
        val playBigDrawable = play.drawable as? AnimatedVectorDrawable?
        playBigDrawable?.start()
    }

    private fun updateSheetStateToExpanded(episode: ViewEpisode) {
        image.loadWithRoundCorners(episode.image)
        episodeName.text = episode.title
        description.text = HtmlCompat.fromHtml(
            episode.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        downloadProgress.text = ""

        val isSelectedEpisodePlaying = detailViewModel.isEpisodePlaying(episode)
        val playResource = if (isSelectedEpisodePlaying) R.drawable.ic_pause_to_play else R.drawable.ic_play_to_pause
        play.setImageResource(playResource)
        val playBigDrawable = play.drawable as? AnimatedVectorDrawable?
        playBigDrawable?.start()
    }
}
