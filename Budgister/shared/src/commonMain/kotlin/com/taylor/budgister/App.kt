package com.taylor.budgister

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
fun App(driverFactory: DatabaseDriverFactory) {
    val repository = remember { ListsRepository(driverFactory) }

    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Landing) }

        when (currentScreen) {
            Screen.Landing -> LandingScreen(
                onNavigate = { currentScreen = it }
            )
            Screen.CheckRegister -> PlaceholderScreen("Check Register") {
                currentScreen = Screen.Landing
            }
            Screen.Lists -> ListsScreen(
                repository = repository,
                onBack = { currentScreen = Screen.Landing }
            )
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

@Composable
private fun ListsScreen(repository: ListsRepository, onBack: () -> Unit) {
    val lists by repository.getAllLists().collectAsState(initial = emptyList())
    var newListTitle by remember { mutableStateOf("") }
    var selectedListId by remember { mutableStateOf<Long?>(null) }

    if (selectedListId != null) {
        ListDetailScreen(
            repository = repository,
            listId = selectedListId!!,
            onBack = { selectedListId = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
    ) {
        Text("Lists", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newListTitle,
                onValueChange = { newListTitle = it },
                label = { Text("New list name") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newListTitle.isNotBlank()) {
                        repository.createList(newListTitle)
                        newListTitle = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(lists) { list ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = list.title,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedListId = list.id }
                    )
                    Button(onClick = { repository.deleteList(list.id) }) {
                        Text("Delete")
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
private fun ListDetailScreen(repository: ListsRepository, listId: Long, onBack: () -> Unit) {
    val items by repository.getItemsForList(listId).collectAsState(initial = emptyList())
    var newItemText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                label = { Text("New item") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newItemText.isNotBlank()) {
                        repository.addItem(listId, newItemText, items.size)
                        newItemText = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isChecked == 1L,
                        onCheckedChange = { checked ->
                            repository.toggleItemChecked(item.id, checked)
                        }
                    )
                    Text(
                        text = item.content,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { repository.deleteItem(item.id) }) {
                        Text("X")
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}