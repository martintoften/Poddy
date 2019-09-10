package com.bakkenbaeck.poddy.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getPlayIcon
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.navigation.BottomNav
import com.bakkenbaeck.poddy.presentation.navigation.MainPagerAdapter
import com.bakkenbaeck.poddy.service.ACTION_START
import com.bakkenbaeck.poddy.service.EPISODE
import com.bakkenbaeck.poddy.service.PlayerService
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
        BottomNav(
            pagerAdapter,
            bottomNavigationView,
            viewPager
        )
    }

    private fun initSheetView() {
        sheetBehavior = BottomSheetBehavior.from(sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun initObservers() {
        mainViewModel.playerUpdates.observe(this, Observer {
            handlePlayerUpdates(it)
        })
    }

    private fun handlePlayerUpdates(action: ViewPlayerAction) {
        val drawable = action.getPlayIcon()
        sheet.apply {
            playBig.setImageResource(drawable)
            playSmall.setImageResource(drawable)
            episodeName.text = action.episode.title
            image.loadWithRoundCorners(action.episode.image, R.dimen.radius_small)
            thumbnail.loadWithRoundCorners(action.episode.image)
        }
    }
}
