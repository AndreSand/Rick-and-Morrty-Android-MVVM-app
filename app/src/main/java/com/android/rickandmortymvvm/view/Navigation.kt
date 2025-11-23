package com.android.rickandmortymvvm.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.android.rickandmortymvvm.viewmodel.AppViewModel

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {

    val backStack = rememberNavBackStack(HomeScreen)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeScreen> {
                AppUIScreen(
                    appViewModel = viewModel,
                    onNavigateToDetail = { characterId ->
                        backStack.add(DetailScreen(characterId))
                    },
                    modifier = modifier
                )
            }
            entry<DetailScreen> { detailScreen ->
                CharacterDetailsScreen(
                    appViewModel = viewModel,
                    characterId = detailScreen.characterId,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = modifier
                )
            }
        }
    )
}
