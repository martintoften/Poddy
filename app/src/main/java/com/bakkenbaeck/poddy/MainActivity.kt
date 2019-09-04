package com.bakkenbaeck.poddy

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_sheet.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainPagerAdapter: MainPagerAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNav()
        initSheetView()
    }

    private fun initSheetView() {
        sheetBehavior = BottomSheetBehavior.from(sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun initBottomNav() {
        val tabs = listOf(MainScreen.Search, MainScreen.Queue, MainScreen.Podcast)
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)
        mainPagerAdapter.setItems(tabs)

        val defaultScreen = MainScreen.Search
        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        viewPager.adapter = mainPagerAdapter
        viewPager.offscreenPageLimit = tabs.count()
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val selectedScreen = mainPagerAdapter.getItems()[position]
                selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
            }
        })
    }

    private fun scrollToScreen(mainScreen: MainScreen) {
        val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
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
