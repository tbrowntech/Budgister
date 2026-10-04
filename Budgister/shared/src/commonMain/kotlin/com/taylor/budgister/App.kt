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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape

private enum class Screen {
    Landing, CheckRegister, Lists, Budget, Notes
}

@Composable
@Preview
fun App(driverFactory: DatabaseDriverFactory) {
    val repository = remember { ListsRepository(driverFactory) }
    val notesRepository = remember { NotesRepository(driverFactory) }
    val budgetRepository = remember { BudgetRepository(driverFactory) }

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
            Screen.Budget -> BudgetScreen(
                repository = budgetRepository,
                onBack = { currentScreen = Screen.Landing }
            )
            Screen.Notes -> NotesScreen(
                notesRepository,
                onBack = { currentScreen = Screen.Landing }
            )
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

@Composable
private fun NotesScreen(repository: NotesRepository, onBack: () -> Unit) {
    val notes by repository.getAllNotes().collectAsState(initial = emptyList())
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    if (isCreatingNew) {
        NoteEditScreen(
            repository = repository,
            noteId = null,
            onBack = { isCreatingNew = false }
        )
        return
    }

    if (selectedNoteId != null) {
        NoteEditScreen(
            repository = repository,
            noteId = selectedNoteId,
            onBack = { selectedNoteId = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
    ) {
        Text("Notes", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isCreatingNew = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New Note")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(notes) { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selectedNoteId = note.id },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = note.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = note.body.take(40) + if (note.body.length > 40) "..." else "",
                            fontSize = 14.sp
                        )
                    }
                    Button(onClick = { repository.deleteNote(note.id) }) {
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
private fun NoteEditScreen(repository: NotesRepository, noteId: Long?, onBack: () -> Unit) {
    val existingNote by if (noteId != null) {
        repository.getNoteById(noteId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var hasLoadedExisting by remember { mutableStateOf(false) }

    if (existingNote != null && !hasLoadedExisting) {
        title = existingNote!!.title
        body = existingNote!!.body
        hasLoadedExisting = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Note") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() || body.isNotBlank()) {
                    if (noteId != null) {
                        repository.updateNote(noteId, title, body)
                    } else {
                        repository.createNote(title, body)
                    }
                }
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}

@Composable
private fun BudgetScreen(repository: BudgetRepository, onBack: () -> Unit) {
    val period by repository.getPeriod().collectAsState(initial = BudgetPeriod.MONTHLY)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
    ) {
        Text("Budget", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Period", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            BudgetPeriod.entries.forEach { p ->
                val label = when (p) {
                    BudgetPeriod.WEEKLY -> "Weekly"
                    BudgetPeriod.BI_MONTHLY -> "Bi-Monthly"
                    BudgetPeriod.MONTHLY -> "Monthly"
                }
                Button(
                    onClick = { repository.setPeriod(p) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    colors = if (period == p) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text(label, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val sectionCount = when (period) {
            BudgetPeriod.MONTHLY -> 1
            BudgetPeriod.BI_MONTHLY -> 2
            BudgetPeriod.WEEKLY -> 4
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sectionCount) { index ->
                BudgetSectionCard(
                    repository = repository,
                    period = period,
                    sectionIndex = index,
                    showLabelField = period != BudgetPeriod.MONTHLY
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
private fun BudgetSectionCard(
    repository: BudgetRepository,
    period: BudgetPeriod,
    sectionIndex: Int,
    showLabelField: Boolean
) {
    val categories by repository.getCategoriesForSection(period, sectionIndex)
        .collectAsState(initial = emptyList())
    val savedLabel by repository.getLabel(period, sectionIndex).collectAsState(initial = "")

    var labelText by remember(period, sectionIndex) { mutableStateOf(savedLabel) }
    var hasLoadedLabel by remember(period, sectionIndex) { mutableStateOf(false) }
    if (!hasLoadedLabel) {
        labelText = savedLabel
        hasLoadedLabel = true
    }

    var newCategoryName by remember(period, sectionIndex) { mutableStateOf("") }
    var newCategoryAmount by remember(period, sectionIndex) { mutableStateOf("") }
    var editingCategoryId by remember(period, sectionIndex) { mutableStateOf<Long?>(null) }
    var editName by remember(period, sectionIndex) { mutableStateOf("") }
    var editAmount by remember(period, sectionIndex) { mutableStateOf("") }

    val sectionTotal = categories.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        if (showLabelField) {
            OutlinedTextField(
                value = labelText,
                onValueChange = {
                    labelText = it
                    repository.setLabel(period, sectionIndex, it)
                },
                label = { Text(sectionDefaultLabel(period, sectionIndex)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Text("Monthly Budget", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text("Subtotal: $${"%.2f".format(sectionTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (editingCategoryId != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Category") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = editAmount,
                    onValueChange = { editAmount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.width(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val amountValue = editAmount.toDoubleOrNull() ?: 0.0
                        if (editName.isNotBlank()) {
                            repository.updateCategory(editingCategoryId!!, editName, amountValue)
                        }
                        editingCategoryId = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { editingCategoryId = null },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = newCategoryAmount,
                    onValueChange = { newCategoryAmount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.width(100.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val amountValue = newCategoryAmount.toDoubleOrNull() ?: 0.0
                        if (newCategoryName.isNotBlank()) {
                            repository.addCategory(period, sectionIndex, newCategoryName, amountValue, categories.size)
                            newCategoryName = ""
                            newCategoryAmount = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Add")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        categories.forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.name, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text("$${"%.2f".format(category.amount)}", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    editingCategoryId = category.id
                    editName = category.name
                    editAmount = category.amount.toString()
                }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(onClick = { repository.deleteCategory(category.id) }) {
                    Text("X")
                }
            }
        }
    }
}

private fun sectionDefaultLabel(period: BudgetPeriod, index: Int): String {
    return when (period) {
        BudgetPeriod.BI_MONTHLY -> "Paycheck ${index + 1} date"
        BudgetPeriod.WEEKLY -> "Week ${index + 1} date"
        BudgetPeriod.MONTHLY -> ""
    }
}