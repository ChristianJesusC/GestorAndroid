package com.chiu.renovadoproyecto1.core.network

sealed class NetworkState {
    object Unknown : NetworkState()
    object NoConnection : NetworkState()
    object Wifi : NetworkState()
    object Mobile : NetworkState()
}