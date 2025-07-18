package com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chiu.renovadoproyecto1.core.hardware.domain.Camera.CapturePhotoUseCase
import com.chiu.renovadoproyecto1.core.network.domain.usecase.CheckNetworkUseCase
import com.chiu.renovadoproyecto1.core.offline.domain.usecase.SaveOfflineJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.CreateJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.DeleteJuegoUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.GetJuegosUseCase
import com.chiu.renovadoproyecto1.features.juegos.domain.usecase.UpdateJuegoUseCase
import com.chiu.renovadoproyecto1.features.login.domain.repository.TokenRepository
import com.chiu.renovadoproyecto1.core.offline.domain.repository.OfflineRepository


class JuegosViewModelFactory(
    private val getJuegosUseCase: GetJuegosUseCase,
    private val createJuegoUseCase: CreateJuegoUseCase,
    private val updateJuegoUseCase: UpdateJuegoUseCase,
    private val deleteJuegoUseCase: DeleteJuegoUseCase,
    private val tokenRepository: TokenRepository,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val checkNetworkUseCase: CheckNetworkUseCase,
    private val saveOfflineJuegoUseCase: SaveOfflineJuegoUseCase,
    private val offlineRepository: OfflineRepository,

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return JuegosViewModel(
            getJuegosUseCase = getJuegosUseCase,
            createJuegoUseCase = createJuegoUseCase,
            updateJuegoUseCase = updateJuegoUseCase,
            deleteJuegoUseCase = deleteJuegoUseCase,
            tokenRepository = tokenRepository,
            capturePhotoUseCase = capturePhotoUseCase,
            checkNetworkUseCase = checkNetworkUseCase,
            saveOfflineJuegoUseCase = saveOfflineJuegoUseCase,
            offlineRepository = offlineRepository

        ) as T
    }
}