package com.bakkenbaeck.poddy.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

abstract class BackableFragment : Fragment() {
    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)

        activity?.onBackPressedDispatcher?.addCallback(this) {
            val result = findNavController().navigateUp()
            if (!result) {
                activity?.finish()
            }
        }
    }
}
