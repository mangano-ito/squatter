package io.github.manganoito.squatter.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.manganoito.squatter.logic.accelerometer.Accelerometer
import io.github.manganoito.squatter.logic.detector.SquatDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accelerometer: Accelerometer,
    private val squatDetector: SquatDetector,
) : ViewModel() {
    var uiState: HomeUiState by mutableStateOf(
        HomeUiState(
            count = 0,
        )
    )
        private set

    init {
        viewModelScope.launch(Dispatchers.Default) {
            squatDetector.run()
        }
        squatDetector.counter
            .onEach {
                uiState = uiState.copy(
                    count = it,
                )
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        accelerometer.unlisten()
        super.onCleared()
    }
}