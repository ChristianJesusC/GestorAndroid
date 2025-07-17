package com.chiu.renovadoproyecto1.core.network.domain.repository

import kotlinx.coroutines.flow.Flow
import com.chiu.renovadoproyecto1.core.network.NetworkState

interface NetworkRepository {
    fun getNetworkState(): Flow<NetworkState>
    fun isConnected(): Flow<Boolean>
    fun getConnectionType(): Flow<String>
    fun hasInternet(): Boolean
    fun checkNetworkStatus(): NetworkState
}
