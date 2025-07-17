package com.chiu.renovadoproyecto1.core.network.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import com.chiu.renovadoproyecto1.core.network.domain.repository.NetworkRepository
import com.chiu.renovadoproyecto1.core.network.NetworkState
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class NetworkRepositoryImpl(
    private val context: Context
) : NetworkRepository {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun getNetworkState(): Flow<NetworkState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val state = getCurrentNetworkState()
                trySend(state)
                Log.d("NetworkRepository", "Network available: $state")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(NetworkState.NoConnection)
                Log.d("NetworkRepository", "Network lost")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val state = getNetworkStateFromCapabilities(networkCapabilities)
                trySend(state)
                Log.d("NetworkRepository", "Network capabilities changed: $state")
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)
        trySend(getCurrentNetworkState())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    override fun isConnected(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)
        trySend(isCurrentlyConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    override fun getConnectionType(): Flow<String> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val type = getCurrentConnectionType()
                trySend(type)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend("Sin conexión")
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val type = getConnectionTypeFromCapabilities(networkCapabilities)
                trySend(type)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)
        trySend(getCurrentConnectionType())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    override fun hasInternet(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    override fun checkNetworkStatus(): NetworkState {
        return getCurrentNetworkState()
    }

    private fun getCurrentNetworkState(): NetworkState {
        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkState.NoConnection
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkState.NoConnection

        return getNetworkStateFromCapabilities(networkCapabilities)
    }

    private fun getNetworkStateFromCapabilities(capabilities: NetworkCapabilities): NetworkState {
        return when {
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkState.NoConnection
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.Wifi
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.Mobile
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkState.Wifi
            else -> NetworkState.Unknown
        }
    }

    private fun isCurrentlyConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun getCurrentConnectionType(): String {
        val activeNetwork = connectivityManager.activeNetwork ?: return "Sin conexión"
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "Sin conexión"

        return getConnectionTypeFromCapabilities(networkCapabilities)
    }

    private fun getConnectionTypeFromCapabilities(capabilities: NetworkCapabilities): String {
        return when {
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> "Sin conexión"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Datos móviles"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Desconocido"
        }
    }
}