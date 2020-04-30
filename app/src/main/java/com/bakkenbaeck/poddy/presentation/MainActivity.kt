package com.bakkenbaeck.poddy.presentation

import android.animation.ValueAnimator
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.*
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.player.*
import com.bakkenbaeck.poddy.presentation.service.PlayerService
import com.bakkenbaeck.poddy.util.OnProgressChangesListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player_sheet.*
import kotlinx.android.synthetic.main.player_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun init(inState: Bundle?) {
        if (inState == null) {
            setupBottomNavigationBar()
        }

        initView()
        initSheetView()
        initObservers()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.queue, R.navigation.podcast,  R.navigation.search)
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.navHostContainer,
            intent = intent
        )
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun initView() {
        sheet.playBig.setOnClickListener { handlePlayClicked() }
        player.setOnPlayListener { handlePlayClicked() }
        player.setOnRewindListener { handleBackwardClicked() }
        sheet.back.setOnClickListener { handleBackwardClicked() }
        sheet.forward.setOnClickListener { handleForwardClicked() }
        player.setOnFastForwardListener { handleForwardClicked() }
    }

    private fun handleBackwardClicked() {
        val drawable = sheet.back.drawable as? AnimatedVectorDrawable?
        drawable?.start()
        sheet.player.animateBackwardButton()
        startForegroundService<PlayerService>(ACTION_REWIND)
    }

    private fun handleForwardClicked() {
        val drawable = sheet.forward.drawable as? AnimatedVectorDrawable?
        drawable?.start()
        sheet.player.animateForwardButton()
        startForegroundService<PlayerService>(ACTION_FAST_FORWARD)
    }

    private fun handlePlayClicked() {
        val currentEpisode = mainViewModel.getCurrentEpisode()?.episode ?: return

        startForegroundService<PlayerService> {
            action = ACTION_START
            putExtra(EPISODE, currentEpisode)
        }
    }

    private fun initSheetView() {
        sheetBehavior = BottomSheetBehavior.from(sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val newAlpha = 1f - slideOffset
                player.alpha = newAlpha
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
        val peekHeight = if (queueSize == 0) dpToPx(R.dimen.nav_height)
        else dpToPx(R.dimen.peek_height_expanded)

        ValueAnimator.ofInt(sheetBehavior.peekHeight, peekHeight).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Int
                sheetBehavior.peekHeight = value
                val layoutParams = navHostContainer.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.bottomMargin = value
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
            episodeName.text = action.episode.title
            image.loadWithRoundCorners(action.episode.image, R.dimen.radius_small)
            player.setThumbnail(action.episode.image)

            playBig.setImageResource(drawable)
            val playBigDrawable = playBig.drawable as? AnimatedVectorDrawable?
            playBigDrawable?.start()

            player.setPlayImageResource(drawable)
            val playSmallDrawable = player.getPlayDrawable() as? AnimatedVectorDrawable?
            playSmallDrawable?.start()
        }
    }
}
