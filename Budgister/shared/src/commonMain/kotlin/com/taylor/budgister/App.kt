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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlin.math.abs
import kotlin.math.round
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

private enum class Screen {
    Landing, CheckRegister, Lists, Budget, Notes
}

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    val repository = remember { ListsRepository(driverFactory) }
    val notesRepository = remember { NotesRepository(driverFactory) }
    val budgetRepository = remember { BudgetRepository(driverFactory) }
    val registerRepository = remember { RegisterRepository(driverFactory) }

    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Landing) }

        when (currentScreen) {
            Screen.Landing -> LandingScreen(
                onNavigate = { currentScreen = it }
            )
            Screen.CheckRegister -> CheckRegisterScreen(
                repository = registerRepository,
                onBack = { currentScreen = Screen.Landing }
            )
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

        Text("Subtotal: ${formatMoney(sectionTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                Text(formatMoney(category.amount), fontSize = 14.sp)
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

private fun formatMoney(value: Double): String {
    val negative = value < 0
    val cents = round(abs(value) * 100).toLong()
    val dollars = cents / 100
    val remainder = (cents % 100).toString().padStart(2, '0')
    return (if (negative) "-" else "") + "\$" + dollars + "." + remainder
}

@Composable
private fun CheckRegisterScreen(repository: RegisterRepository, onBack: () -> Unit) {
    val transactions by repository.getTransactions().collectAsState(initial = emptyList())
    val startingBalance by repository.getStartingBalance().collectAsState(initial = 0.0)
    val hasStartingBalance by repository.hasStartingBalance().collectAsState(initial = true)

    var isEditingStartingBalance by remember { mutableStateOf(false) }
    var startingBalanceText by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var dateText by remember { mutableStateOf("") }
    var payeeText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isDeposit by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val currentBalance = startingBalance + transactions.sumOf { it.amount }
    val clearedBalance = startingBalance +
            transactions.filter { it.isCleared == 1L }.sumOf { it.amount }

    val runningBalances = remember(transactions, startingBalance) {
        var running = startingBalance
        transactions.map {
            running += it.amount
            running
        }
    }
    val rows = transactions.zip(runningBalances).reversed()

    fun clearForm() {
        editingId = null
        dateText = ""
        payeeText = ""
        categoryText = ""
        amountText = ""
        isDeposit = false
        showForm = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Check Register", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Current: ${formatMoney(currentBalance)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Cleared: ${formatMoney(clearedBalance)}", fontSize = 14.sp)
                    }

                    if (hasStartingBalance && !isEditingStartingBalance) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Starting balance: ${formatMoney(startingBalance)}",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                startingBalanceText = startingBalance.toString()
                                isEditingStartingBalance = true
                            }) {
                                Text("Change", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompactTextField(
                                value = startingBalanceText,
                                onValueChange = { startingBalanceText = it },
                                label = "Starting balance",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val value = startingBalanceText.toDoubleOrNull()
                                if (value != null) {
                                    repository.setStartingBalance(value)
                                    startingBalanceText = ""
                                    isEditingStartingBalance = false
                                }
                            }) {
                                Text("Set")
                            }
                            if (hasStartingBalance) {
                                TextButton(onClick = {
                                    startingBalanceText = ""
                                    isEditingStartingBalance = false
                                }) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!showForm) {
                        Button(
                            onClick = { showForm = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ New transaction")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DatePickerField(
                                dateText = dateText,
                                onDateChange = { dateText = it },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CompactTextField(
                                value = amountText,
                                onValueChange = { amountText = it },
                                label = "Amount",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.width(110.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CompactTextField(
                                value = payeeText,
                                onValueChange = { payeeText = it },
                                label = "Payee",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CompactTextField(
                                value = categoryText,
                                onValueChange = { categoryText = it },
                                label = "Category",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { isDeposit = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp),
                                colors = if (!isDeposit) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("Withdrawal")
                            }
                            Button(
                                onClick = { isDeposit = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp),
                                colors = if (isDeposit) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("Deposit")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    val parsed = amountText.toDoubleOrNull()
                                    if (payeeText.isNotBlank() && parsed != null) {
                                        val signed = if (isDeposit) abs(parsed) else -abs(parsed)
                                        val id = editingId
                                        if (id != null) {
                                            repository.updateTransaction(id, dateText, payeeText, categoryText, signed)
                                        } else {
                                            repository.addTransaction(dateText, payeeText, categoryText, signed)
                                        }
                                        clearForm()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (editingId != null) "Save" else "Add")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { clearForm() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(rows) { (transaction, balance) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            editingId = transaction.id
                            dateText = transaction.transactionDate
                            payeeText = transaction.payee
                            categoryText = transaction.category
                            amountText = abs(transaction.amount).toString()
                            isDeposit = transaction.amount >= 0
                            showForm = true
                            scope.launch { listState.animateScrollToItem(0) }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = transaction.isCleared == 1L,
                        onCheckedChange = { checked ->
                            repository.setCleared(transaction.id, checked)
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            transaction.payee,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${transaction.transactionDate} • ${transaction.category}",
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatMoney(transaction.amount), fontSize = 15.sp)
                        Text("Bal ${formatMoney(balance)}", fontSize = 11.sp)
                    }
                    Text(
                        text = "✕",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clickable { repository.deleteTransaction(transaction.id) }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    dateText: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showPicker = true }, modifier = modifier) {
        Text(if (dateText.isBlank()) "Date" else dateText)
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateTextToMillis(dateText)
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onDateChange(millisToDateText(it)) }
                    showPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

// The picker returns milliseconds since 1970 (at midnight UTC). These two functions
// convert to and from "YYYY-MM-DD" using plain Kotlin, so they work on every platform.
private fun millisToDateText(millis: Long): String {
    val z = millis.floorDiv(86_400_000L) + 719_468L
    val era = z.floorDiv(146_097L)
    val doe = z - era * 146_097L
    val yoe = (doe - doe / 1_460L + doe / 36_524L - doe / 146_096L) / 365L
    val doy = doe - (365L * yoe + yoe / 4L - yoe / 100L)
    val mp = (5L * doy + 2L) / 153L
    val day = doy - (153L * mp + 2L) / 5L + 1L
    val month = if (mp < 10L) mp + 3L else mp - 9L
    val year = yoe + era * 400L + if (month <= 2L) 1L else 0L
    return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

private fun dateTextToMillis(text: String): Long? {
    val parts = text.split("-")
    if (parts.size != 3) return null
    val year = parts[0].toLongOrNull() ?: return null
    val month = parts[1].toLongOrNull() ?: return null
    val day = parts[2].toLongOrNull() ?: return null
    val y = if (month <= 2L) year - 1L else year
    val era = y.floorDiv(400L)
    val yoe = y - era * 400L
    val doy = (153L * (if (month > 2L) month - 3L else month + 9L) + 2L) / 5L + day - 1L
    val doe = yoe * 365L + yoe / 4L - yoe / 100L + doy
    return (era * 146_097L + doe - 719_468L) * 86_400_000L
}

@Composable
private fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                innerTextField()
            }
        }
    )
}