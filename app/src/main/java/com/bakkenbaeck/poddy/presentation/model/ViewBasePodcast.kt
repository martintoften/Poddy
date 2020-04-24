package com.bakkenbaeck.poddy.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface ViewBasePodcast {
    val id: String
    val image: String
    val title: String
    val description: String
}

@Parcelize
data class ViewBasePodcastImpl(
    override val id: String,
    override val image: String,
    override val title: String,
    override val description: String
) : ViewBasePodcast, Parcelable

fun ViewBasePodcast.toViewModel(): ViewBasePodcastImpl {
    return ViewBasePodcastImpl(
        id = id,
        image = image,
        title = title,
        description = description
    )
}