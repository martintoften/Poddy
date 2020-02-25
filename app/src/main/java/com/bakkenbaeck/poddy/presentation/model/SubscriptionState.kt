package com.bakkenbaeck.poddy.presentation.model

sealed class SubscriptionState
class Subscribed : SubscriptionState()
class Unsubscribed : SubscriptionState()
