package com.taylor.budgister

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class Screen {
    Landing, CheckRegister, Lists, Budget, Notes
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Landing) }

        when (currentScreen) {
            Screen.Landing -> LandingScreen(
                onNavigate = { currentScreen = it }
            )
            Screen.CheckRegister -> PlaceholderScreen("Check Register") {
                currentScreen = Screen.Landing
            }
            Screen.Lists -> PlaceholderScreen("Lists") {
                currentScreen = Screen.Landing
            }
            Screen.Budget -> PlaceholderScreen("Budget") {
                currentScreen = Screen.Landing
            }
            Screen.Notes -> PlaceholderScreen("Notes") {
                currentScreen = Screen.Landing
            }
        }
    }
}

@Composable
private fun LandingScreen(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Budgister",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your money, lists, and notes in one place",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onNavigate(Screen.CheckRegister) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check Register")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onNavigate(Screen.Budget) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Budget")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onNavigate(Screen.Lists) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lists")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onNavigate(Screen.Notes) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Notes")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("This screen is coming soon.")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}