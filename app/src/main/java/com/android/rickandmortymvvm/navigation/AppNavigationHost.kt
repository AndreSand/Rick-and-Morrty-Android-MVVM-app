package com.android.rickandmortymvvm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.android.rickandmortymvvm.view.CharacterDetailScreen
import com.android.rickandmortymvvm.view.MainScreen
import com.android.rickandmortymvvm.viewmodel.AppViewModel

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {

    val backStack = remember { mutableStateListOf<Any>(CharacterList) }
    
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider { 
            entry<CharacterList> {
                MainScreen(
                    viewModel = viewModel,
                    onCharacterClick = { characterId ->
                        backStack.add(CharacterDetail(characterId))
                    },
                    modifier = modifier
                )
            }
            
            entry<CharacterDetail> { destination ->
                CharacterDetailScreen(
                    characterId = destination.characterId,
                    viewModel = viewModel,
                    onBackClick = {
                        backStack.remove(destination)
                    },
                    modifier = modifier
                )
            }
        }
    )
}
