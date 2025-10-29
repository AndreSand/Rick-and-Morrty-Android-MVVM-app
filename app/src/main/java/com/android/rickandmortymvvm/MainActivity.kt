package com.android.rickandmortymvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.rickandmortymvvm.mvi.CharacterScreen
import com.android.rickandmortymvvm.mvi.CharacterViewModel
import com.android.rickandmortymvvm.ui.theme.RickandMortyMVVMTheme
import dagger.hilt.android.AndroidEntryPoint
//import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Main Activity using MVI Architecture
 * 
 * MVI Flow:
 * Activity → CharacterScreen (View) → CharacterViewModel → CharacterState
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Create ViewModel using viewModel() factory
            val viewModel: CharacterViewModel = viewModel()

            RickandMortyMVVMTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Use MVI CharacterScreen instead of MVVM MainScreen
                    CharacterScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RickandMortyMVVMTheme {
        Greeting("Android")
    }
}
