package com.bakkenbaeck.poddy.presentation

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.bakkenbaeck.poddy.ACTION_SEEK_TO
import com.bakkenbaeck.poddy.ACTION_START
import com.bakkenbaeck.poddy.EPISODE
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.*
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.navigation.BottomNav
import com.bakkenbaeck.poddy.presentation.navigation.MainPagerAdapter
import com.bakkenbaeck.poddy.service.PlayerService
import com.bakkenbaeck.poddy.util.OnProgressChangesListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player_sheet.*
import kotlinx.android.synthetic.main.player_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        initView()
        initBottomNav()
        initSheetView()
        initObservers()
    }

    private fun initView() {
        sheet.playBig.setOnClickListener { handlePlayClicked() }
        sheet.playSmall.setOnClickListener { handlePlayClicked() }
    }

    private fun handlePlayClicked() {
        val currentEpisode = mainViewModel.getCurrentEpisode()?.episode ?: return

        startForegroundService<PlayerService> {
            action = ACTION_START
            putExtra(EPISODE, currentEpisode)
        }
    }

    private fun initBottomNav() {
        val pagerAdapter = MainPagerAdapter(supportFragmentManager)
        BottomNav(pagerAdapter, bottomNavigationView, viewPager)
    }

    private fun initSheetView() {
        sheetBehavior = BottomSheetBehavior.from(sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val newAlpha = 1f - slideOffset
                sheet.player.alpha = newAlpha
                sheet.smallProgressFront.alpha = newAlpha
                sheet.smallProgressBack.alpha = newAlpha
            }
        })

        sheet.progress.setOnSeekBarChangeListener(object : OnProgressChangesListener() {
            override fun onProgressChanged(progress: Int) {
                handleProgressChanged(progress)
            }
        })
    }

    private fun handleProgressChanged(progress: Int) {
        val currentEpisode = mainViewModel.getCurrentEpisode()?.episode ?: return

        startForegroundService<PlayerService> {
            action = ACTION_SEEK_TO
            putExtra(EPISODE, currentEpisode.copy(progress = progress.toLong()))
        }
    }

    private fun initObservers() {
        mainViewModel.playerUpdates.observe(this, Observer {
            handlePlayerUpdates(it)
        })
        mainViewModel.queueSize.observe(this, Observer {
            handleQueue(it)
        })
    }

    private fun handleQueue(queueSize: Int) {
        val peekHeight = if (queueSize == 0) dpToPx(R.dimen.peek_height_collapsed)
        else dpToPx(R.dimen.peek_height_expanded)

        ValueAnimator.ofInt(sheetBehavior.peekHeight, peekHeight).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Int
                val bottomMargin = value - dpToPx(R.dimen.nav_height)
                sheetBehavior.peekHeight = value
                val layoutParams = viewPager.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.bottomMargin = bottomMargin
            }
        }.start()
    }

    private fun handlePlayerUpdates(action: ViewPlayerAction) {
        when (action) {
            is ViewPlayerAction.Progress -> updateProgressUi(action)
            else -> updatePlayerUi(action)
        }
    }

    private fun updateProgressUi(action: ViewPlayerAction.Progress) {
        val progress = action.getProgressInPercent()
        sheet.progress.progress = progress
        sheet.progressText.text = action.getFormattedProgress()
        sheet.durationText.text = action.getFormattedDuration()
        val layoutParams = sheet.smallProgressFront.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = (root.width * action.getProgressInFraction()).toInt()
    }

    private fun updatePlayerUi(action: ViewPlayerAction) {
        val drawable = action.getPlayIcon() ?: return
        sheet.apply {
            playBig.setImageResource(drawable)
            playSmall.setImageResource(drawable)
            episodeName.text = action.episode.title
            image.loadWithRoundCorners(action.episode.image, R.dimen.radius_small)
            thumbnail.loadWithRoundCorners(action.episode.image, R.dimen.radius_small)
        }
    }
}
