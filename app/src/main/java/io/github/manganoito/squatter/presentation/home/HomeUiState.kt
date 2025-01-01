package io.github.manganoito.squatter.presentation.home

sealed interface HomeUiState {
    data object Initial : HomeUiState
}
