package com.chiu.renovadoproyecto1.core.network.data.usecase

import kotlinx.coroutines.flow.Flow
import com.chiu.renovadoproyecto1.core.network.domain.usecase.CheckNetworkUseCase
import com.chiu.renovadoproyecto1.core.network.domain.repository.NetworkRepository
import com.chiu.renovadoproyecto1.core.network.NetworkState

class CheckNetworkUseCaseImpl(
    private val networkRepository: NetworkRepository
) : CheckNetworkUseCase {

    override fun getNetworkState(): Flow<NetworkState> {
        return networkRepository.getNetworkState()
    }

    override fun isConnected(): Flow<Boolean> {
        return networkRepository.isConnected()
    }

    override fun getConnectionType(): Flow<String> {
        return networkRepository.getConnectionType()
    }

    override fun hasInternet(): Boolean {
        return networkRepository.hasInternet()
    }

    override fun checkNetworkStatus(): NetworkState {
        return networkRepository.checkNetworkStatus()
    }
}