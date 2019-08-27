package com.bakkenbaeck.poddy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bakkenbaeck.poddy.model.Search
import com.bakkenbaeck.poddy.presentation.search.SearchViewModel
import com.bakkenbaeck.poddy.util.TextListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
