package com.bakkenbaeck.poddy.presentation.navigation

import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNav(
    private val pagerAdapter: MainPagerAdapter,
    private val bottomNavigationView: BottomNavigationView,
    private val viewPager: ViewPager
) : BottomNavigationView.OnNavigationItemSelectedListener {

    init {
        initBottomNav()
    }

    private fun initBottomNav() {
        val tabs = listOf(MainScreen.Search, MainScreen.Queue, MainScreen.Podcast)
        pagerAdapter.setItems(tabs)

        val defaultScreen = MainScreen.Search
        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = tabs.count()
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val selectedScreen = pagerAdapter.getItems()[position]
                selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
            }
        })
    }

    private fun scrollToScreen(mainScreen: MainScreen) {
        val screenPosition = pagerAdapter.getItems().indexOf(mainScreen)
        if (screenPosition != viewPager.currentItem) {
            viewPager.currentItem = screenPosition
        }
    }

    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int) {
        bottomNavigationView.setOnNavigationItemSelectedListener(null)
        bottomNavigationView.selectedItemId = menuItemId
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        getMainScreenForMenuItem(menuItem.itemId)?.let {
            scrollToScreen(it)
            return true
        }
        return false
    }
}
