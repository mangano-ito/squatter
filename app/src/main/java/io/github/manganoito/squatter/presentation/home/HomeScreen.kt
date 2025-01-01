package io.github.manganoito.squatter.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import io.github.manganoito.squatter.presentation.theme.SquatterTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreen(
        uiState = viewModel.uiState,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
) {
    // TODO
    Text("Home")
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    SquatterTheme {
        HomeScreen(
            uiState = HomeUiState.Initial,
        )
    }
}