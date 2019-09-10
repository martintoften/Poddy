package com.bakkenbaeck.poddy.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.navigation.BottomNav
import com.bakkenbaeck.poddy.presentation.navigation.MainPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_sheet.*

class MainActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNav()
        initSheetView()
    }

    private fun initBottomNav() {
        val pagerAdapter =
            MainPagerAdapter(supportFragmentManager)
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
}
