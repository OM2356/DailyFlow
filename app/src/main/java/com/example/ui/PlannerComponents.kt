package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*

// --- Custom Colors for Categories ---
val WorkColor = Color(0xFF3F51B5)
val PersonalColor = Color(0xFFE91E63)
val HealthColor = Color(0xFF4CAF50)
val StudyColor = Color(0xFFFF9800)
val FinanceColor = Color(0xFF009688)
val SocialColor = Color(0xFF9C27B0)

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Work" -> WorkColor
        "Personal" -> PersonalColor
        "Health" -> HealthColor
        "Study" -> StudyColor
        "Finance" -> FinanceColor
        "Social" -> SocialColor
        else -> Color.Gray
    }
}

// --- Dynamic Icon Map for Habits ---
fun getHabitIcon(iconName: String): ImageVector {
    return when (iconName) {
        "LocalCafe" -> Icons.Default.LocalCafe
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "MenuBook" -> Icons.Default.MenuBook
        "SelfImprovement" -> Icons.Default.SelfImprovement
        "DirectionsRun" -> Icons.Default.DirectionsRun
        "Pets" -> Icons.Default.Pets
        "Brush" -> Icons.Default.Brush
        else -> Icons.Default.CheckCircle
    }
}

// ==========================================
// 1. Calendar Selector Component
// ==========================================
@Composable
fun HorizontalCalendar(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dates = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5) // Start 5 days ago
        for (i in 0..14) { // Generate 15 days total
            list.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 3)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = PlannerViewModel.getFormattedDisplayDate(selectedDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = {
                    onDateSelected(PlannerViewModel.getCurrentDateString())
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Reset to today",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dates) { dateStr ->
                val isSelected = dateStr == selectedDate
                val displayDay = PlannerViewModel.getFormattedDisplayDayOnly(dateStr)
                val lines = displayDay.split("\n")
                val dayOfWeek = lines.getOrNull(0) ?: ""
                val dayOfMonth = lines.getOrNull(1) ?: ""

                val isToday = dateStr == PlannerViewModel.getCurrentDateString()

                Card(
                    modifier = Modifier
                        .width(56.dp)
                        .height(72.dp)
                        .testTag("calendar_day_$dateStr")
                        .clickable { onDateSelected(dateStr) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else if (isToday) {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = if (isToday && !isSelected) {
                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
                    } else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = dayOfWeek.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = dayOfMonth,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 2. Circular Progress Indicator Component
// ==========================================
@Composable
fun ProgressHeader(
    totalTasks: Int,
    completedTasks: Int,
    totalHabits: Int,
    completedHabits: Int,
    modifier: Modifier = Modifier
) {
    val totalItems = totalTasks + totalHabits
    val completedItems = completedTasks + completedHabits
    val progressFraction = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Progression",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (totalItems == 0) {
                    Text(
                        text = "No activities planned for today. Add a task or timeline event below!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val message = when {
                        progressFraction == 1f -> "Stellar! Everything is completed! 🎉"
                        progressFraction >= 0.7f -> "Almost there! Keep pushing. 💪"
                        progressFraction >= 0.5f -> "Halfway through your goals! 🚀"
                        completedItems > 0 -> "Good start! Keep moving steadily."
                        else -> "Let's capture your daily focus!"
                    }
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tasks: $completedTasks/$totalTasks  •  Habits: $completedHabits/$totalHabits",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "done",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}


// ==========================================
// 3. Tab List: Tasks (Checklist) Screen
// ==========================================
@Composable
fun TasksTabContent(
    tasks: List<PlannerTask>,
    onToggleTask: (PlannerTask) -> Unit,
    onDeleteTask: (PlannerTask) -> Unit,
    onAddTaskClick: () -> Unit,
    onQuickAddTask: (title: String, description: String, priority: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var quickTitle by remember { mutableStateOf("") }
    var quickPriority by remember { mutableStateOf("Medium") }
    var quickCategory by remember { mutableStateOf("Personal") }
    var activeFilter by remember { mutableStateOf("All") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Visual progress bar card for Checklist
        val totalCount = tasks.size
        val completedTasks = tasks.filter { it.isCompleted }
        val completedCount = completedTasks.size
        val activeCount = totalCount - completedCount
        val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
        val percentage = (progressFraction * 100).toInt()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
                .testTag("checklist_progress_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Task Completion Rate",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "$percentage% ($completedCount of $totalCount completed)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .testTag("checklist_linear_progress"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        }

        // Inline Quick Add Task Component
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .testTag("quick_add_task_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quickTitle,
                        onValueChange = { quickTitle = it },
                        placeholder = { Text("Quick add task...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_add_task_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (quickTitle.isNotBlank()) {
                                    onQuickAddTask(quickTitle.trim(), "", quickPriority, quickCategory)
                                    quickTitle = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        trailingIcon = {
                            if (quickTitle.isNotEmpty()) {
                                IconButton(onClick = { quickTitle = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (quickTitle.isNotBlank()) {
                                onQuickAddTask(quickTitle.trim(), "", quickPriority, quickCategory)
                                quickTitle = ""
                                focusManager.clearFocus()
                            }
                        },
                        enabled = quickTitle.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("quick_add_task_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Small selectable quick configuration row for active session inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Selection Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cat:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf("Personal", "Work", "Study").forEach { cat ->
                            val isSelected = quickCategory == cat
                            val catColor = getCategoryColor(cat)
                            SuggestionChip(
                                onClick = { quickCategory = cat },
                                label = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                                colors = if (isSelected) {
                                    SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = catColor.copy(alpha = 0.15f),
                                        labelColor = catColor
                                    )
                                } else {
                                    SuggestionChipDefaults.suggestionChipColors()
                                },
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("quick_add_cat_$cat")
                            )
                        }
                    }

                    // Priority Selection Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pri:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf("High", "Medium", "Low").forEach { p ->
                            val isSelected = quickPriority == p
                            val pColor = when (p) {
                                "High" -> Color(0xFFE53935)
                                "Medium" -> Color(0xFFFBC02D)
                                "Low" -> Color(0xFF43A047)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            SuggestionChip(
                                onClick = { quickPriority = p },
                                label = { Text(p, style = MaterialTheme.typography.bodySmall) },
                                colors = if (isSelected) {
                                    SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = pColor.copy(alpha = 0.15f),
                                        labelColor = pColor
                                    )
                                } else {
                                    SuggestionChipDefaults.suggestionChipColors()
                                },
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("quick_add_pri_$p")
                            )
                        }
                    }
                }
            }
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
            .testTag("task_filter_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            Triple("All", totalCount, Icons.Default.List),
            Triple("Active", activeCount, Icons.Default.RadioButtonUnchecked),
            Triple("Completed", completedCount, Icons.Default.CheckCircle)
        ).forEach { (filterName, count, icon) ->
            val isSelected = activeFilter == filterName
            val containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }
            val contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(containerColor)
                    .then(
                        if (isSelected) Modifier else Modifier.border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            RoundedCornerShape(19.dp)
                        )
                    )
                    .clickable { activeFilter = filterName }
                    .testTag("filter_chip_$filterName"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = filterName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.weight(1f)) {
        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = "Empty checklist",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your checklist is clear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep your day structured and productive by breaking down your goals into actionable items.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddTaskClick,
                    modifier = Modifier.testTag("add_task_empty_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Detailed Task")
                }
            }
        } else {
            val filteredTasks = when (activeFilter) {
                "Active" -> tasks.filter { !it.isCompleted }
                "Completed" -> tasks.filter { it.isCompleted }
                else -> tasks
            }

            if (filteredTasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val emptyIcon = when (activeFilter) {
                        "Active" -> Icons.Default.CheckCircle
                        "Completed" -> Icons.Default.TaskAlt
                        else -> Icons.Default.TaskAlt
                    }
                    val emptyTitle = when (activeFilter) {
                        "Active" -> "No active tasks"
                        "Completed" -> "No completed tasks"
                        else -> "Your checklist is clear"
                    }
                    val emptyDesc = when (activeFilter) {
                        "Active" -> "Fantastic! You've completed all your tasks for today."
                        "Completed" -> "Get started on your tasks and mark them completed here!"
                        else -> "Keep your day structured by adding actionable items."
                    }

                    Icon(
                        imageVector = emptyIcon,
                        contentDescription = "Empty state icon",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = emptyTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = emptyDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val incompleteTasks = filteredTasks.filter { !it.isCompleted }
                val completedTasks = filteredTasks.filter { it.isCompleted }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (incompleteTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "To Do",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(incompleteTasks, key = { it.id }) { task ->
                            TaskItemCard(
                                task = task,
                                onToggle = { onToggleTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                    }

                    if (completedTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                        }
                        items(completedTasks, key = { it.id }) { task ->
                            TaskItemCard(
                                task = task,
                                onToggle = { onToggleTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                    }
                }
            }
        }

            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
                icon = { Icon(Icons.Default.Add, "Add Task Icon") },
                text = { Text("Add Detailed Task") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_task_fab"),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun TaskItemCard(
    task: PlannerTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFE53935)
        "Medium" -> Color(0xFFFBC02D)
        "Low" -> Color(0xFF43A047)
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Tag Strip left margin
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(getCategoryColor(task.category).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = getCategoryColor(task.category)
                        )
                    }
                }
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (task.isCompleted) 0.5f else 0.8f
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Checkbox and Delete icon
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("task_toggle_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle status",
                    tint = if (task.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("task_delete_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


// ==========================================
// 4. Tab List: Timeline (Schedule) Screen
// ==========================================
@Composable
fun ScheduleTabContent(
    events: List<PlannerEvent>,
    onDeleteEvent: (PlannerEvent) -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (events.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Empty timeline",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No scheduled blocks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Block out specific hours of the day to manage meetings, chores, workouts, or classes. Avoid conflicting appointments!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddEventClick,
                    modifier = Modifier.testTag("add_event_empty_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Block a Time Slot")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventItemCard(
                        event = event,
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }

            ExtendedFloatingActionButton(
                onClick = onAddEventClick,
                icon = { Icon(Icons.Default.Add, "Add Event Icon") },
                text = { Text("Schedule Time") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_event_fab"),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun EventItemCard(
    event: PlannerEvent,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(event.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("event_card_${event.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start - End timeline indicator
            Column(
                modifier = Modifier
                    .width(76.dp)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = event.startTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.endTime,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Left Border Strip representing Category Color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(categoryColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Category Tag Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = event.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }
                }
                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("event_delete_${event.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete event",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


// ==========================================
// 5. Tab List: Habits Component Screen
// ==========================================
@Composable
fun HabitsTabContent(
    habits: List<Habit>,
    completions: List<HabitCompletion>,
    onToggleHabitCompletion: (Int, Boolean) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onAddHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (habits.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = "Empty habits",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Build productive routines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Habits are daily systems that compound into massive life transformations over time. Add your custom habits!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddHabitClick,
                    modifier = Modifier.testTag("add_habit_empty_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add a Habit")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Your Daily Routines",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(habits, key = { it.id }) { habit ->
                    val isCompleted = completions.any { it.habitId == habit.id }
                    HabitItemCard(
                        habit = habit,
                        isCompleted = isCompleted,
                        onCompletionToggle = { onToggleHabitCompletion(habit.id, !isCompleted) },
                        onDelete = { onDeleteHabit(habit) }
                    )
                }
            }

            ExtendedFloatingActionButton(
                onClick = onAddHabitClick,
                icon = { Icon(Icons.Default.Add, "Add Habit Icon") },
                text = { Text("Add Habit") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_habit_fab"),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun HabitItemCard(
    habit: Habit,
    isCompleted: Boolean,
    onCompletionToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColor = remember(habit.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(habit.colorHex))
        } catch (e: Exception) {
            Color(0xFF6200EE)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                themeColor.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isCompleted) themeColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon space with circle background
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) themeColor.copy(alpha = 0.25f) else themeColor.copy(
                            alpha = 0.1f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getHabitIcon(habit.iconName),
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(getCategoryColor(habit.category))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = habit.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Checkmark button (Tick checklist)
            IconButton(
                onClick = onCompletionToggle,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("habit_toggle_${habit.id}")
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Complete Habit",
                    tint = if (isCompleted) themeColor else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("habit_delete_${habit.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove Habit Routine",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}


// ==========================================
// 6. Tab List: Reflection & Daily Notes Screen
// ==========================================
@Composable
fun ReflectionTabContent(
    dailyNote: DailyNote?,
    onSaveFocus: (String) -> Unit,
    onSaveReflection: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var focusTemp by remember(dailyNote) { mutableStateOf(dailyNote?.focus ?: "") }
    var reflectionTemp by remember(dailyNote) { mutableStateOf(dailyNote?.reflectionText ?: "") }
    var moodSelected by remember(dailyNote) { mutableStateOf(dailyNote?.moodRating ?: 0) }

    val isFocusSaved = dailyNote?.focus == focusTemp
    val isReflectSaved = (dailyNote?.reflectionText == reflectionTemp && dailyNote?.moodRating == moodSelected)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reflection_content_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub-Sheet: Main Focus Goal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Main Focus Goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What is the single most important objective for you to accomplish today?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = focusTemp,
                        onValueChange = { focusTemp = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("focus_goal_input"),
                        placeholder = { Text("e.g. Finish submitting the Q2 project draft") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onSaveFocus(focusTemp)
                                focusManager.clearFocus()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onSaveFocus(focusTemp)
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_focus_goal_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFocusSaved) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                            contentColor = if (isFocusSaved) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isFocusSaved) Icons.Default.Check else Icons.Default.Save,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isFocusSaved) "Saved" else "Save Focus")
                    }
                }
            }
        }

        // Sub-Sheet: Daily Reflection & Mood Logging
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Reflection & Mood",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reflect on how your day actually went, what you are grateful for, and record your general emotional energy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Rate your energy:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Emojis for mood
                    val moods = listOf(
                        1 to "😢",
                        2 to "😕",
                        3 to "😐",
                        4 to "🙂",
                        5 to "🤩"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        moods.forEach { (rating, emoji) ->
                            val isSelected = moodSelected == rating
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        moodSelected = rating
                                    }
                                    .testTag("mood_emoji_$rating"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 24.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Notes or grateful points:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reflectionTemp,
                        onValueChange = { reflectionTemp = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("reflection_textbox"),
                        placeholder = { Text("What did I learn today? What brought me joy? What can I improve tomorrow?") },
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onSaveReflection(reflectionTemp, moodSelected)
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_reflection_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReflectSaved) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                            contentColor = if (isReflectSaved) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isReflectSaved) Icons.Default.Check else Icons.Default.Save,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isReflectSaved) "Saved" else "Save Log")
                    }
                }
            }
        }
    }
}


// ==========================================
// 7. Dialog: Add Task Form
// ==========================================
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSaveTask: (title: String, description: String, priority: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var category by remember { mutableStateOf("Personal") }

    val priorities = listOf("High", "Medium", "Low")
    val categories = listOf("Work", "Personal", "Health", "Study", "Finance", "Social")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add Daily Task", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_task_dialog_container"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Gym workout session") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Details or specific instructions") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_description_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                // Priority Selection row
                Column {
                    Text(
                        text = "Priority Level:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        priorities.forEach { p ->
                            val isSelected = priority == p
                            val priorityColor = when (p) {
                                "High" -> Color(0xFFE53935)
                                "Medium" -> Color(0xFFFBC02D)
                                "Low" -> Color(0xFF43A047)
                                else -> MaterialTheme.colorScheme.outline
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { priority = p },
                                label = { Text(p) },
                                modifier = Modifier.testTag("priority_chip_$p"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = priorityColor.copy(alpha = 0.25f),
                                    selectedLabelColor = priorityColor
                                )
                            )
                        }
                    }
                }

                // Category selection row
                Column {
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("category_dropdown_trigger"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(category, color = getCategoryColor(category))
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = getCategoryColor(category))
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = FontWeight.Medium,
                                            color = getCategoryColor(cat)
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    },
                                    modifier = Modifier.testTag("category_item_$cat")
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveTask(title, description, priority, category)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("add_task_dialog_save")
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_task_dialog_cancel")
            ) {
                Text("Cancel")
            }
        }
    )
}


// ==========================================
// 8. Dialog: Add Timeline Slot Form
// ==========================================
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSaveEvent: (title: String, description: String, startTime: String, endTime: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var startHour by remember { mutableStateOf("09") }
    var startMin by remember { mutableStateOf("00") }
    var endHour by remember { mutableStateOf("10") }
    var endMin by remember { mutableStateOf("00") }

    var category by remember { mutableStateOf("Work") }

    val hours = (0..23).map { String.format("%02d", it) }
    val minutes = listOf("00", "15", "30", "45")
    val categories = listOf("Work", "Personal", "Health", "Study", "Finance", "Social")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Schedule Time Slot", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_event_dialog_container"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    placeholder = { Text("e.g. Sync meeting with design group") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_event_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details") },
                    placeholder = { Text("Meeting link, resources or checklist") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_event_description_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Start Time dropdowns
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start Hour:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Box {
                            var expanded by remember { mutableStateOf(false) }
                            OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(8.dp)) {
                                Text("$startHour:$startMin")
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                hours.forEach { hr ->
                                    DropdownMenuItem(
                                        text = { Text("$hr : 00") },
                                        onClick = {
                                            startHour = hr
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // End Time dropdowns
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End Hour:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Box {
                            var expanded by remember { mutableStateOf(false) }
                            OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(8.dp)) {
                                Text("$endHour:$endMin")
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                hours.forEach { hr ->
                                    DropdownMenuItem(
                                        text = { Text("$hr : 00") },
                                        onClick = {
                                            endHour = hr
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Category Selection dropdown
                Column {
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("event_category_dropdown_trigger"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(category, color = getCategoryColor(category))
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = getCategoryColor(category))
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = FontWeight.Bold,
                                            color = getCategoryColor(cat)
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val startTimeStr = "$startHour:$startMin"
                        val endTimeStr = "$endHour:$endMin"
                        onSaveEvent(title, description, startTimeStr, endTimeStr, category)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("add_event_dialog_save")
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_event_dialog_cancel")
            ) {
                Text("Cancel")
            }
        }
    )
}


// ==========================================
// 9. Dialog: Add Habit Routine Form
// ==========================================
@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSaveHabit: (title: String, iconName: String, colorHex: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("LocalCafe") }
    var selectedColor by remember { mutableStateOf("#FF2196F3") } // default blue
    var category by remember { mutableStateOf("Health") }

    val iconChoices = listOf(
        "LocalCafe" to "Coffee/Drink",
        "FitnessCenter" to "Gym/Workout",
        "MenuBook" to "Study/Read",
        "SelfImprovement" to "Mindfulness",
        "DirectionsRun" to "Run/Walk",
        "Pets" to "Pet Care",
        "Brush" to "Hobby/Art"
    )

    // A list of friendly color hex representations for routine templates
    val colorChoices = listOf(
        "#FF2196F3" to "Blue",
        "#FF4CAF50" to "Green",
        "#FFFF9800" to "Orange",
        "#FF9C27B0" to "Purple",
        "#FFE91E63" to "Pink",
        "#FFFFEB3B" to "Yellow"
    )

    val categories = listOf("Work", "Personal", "Health", "Study", "Finance", "Social")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add Habit Routine", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_habit_dialog_container"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title *") },
                    placeholder = { Text("e.g. Stretch for 10 minutes") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_habit_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                // Habit Icon choice grid/row
                Column {
                    Text(
                        text = "Choose Icon:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(iconChoices) { (iconKey, label) ->
                            val isSelected = selectedIcon == iconKey
                            val themeColor = try {
                                Color(android.graphics.Color.parseColor(selectedColor))
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }

                            Card(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clickable { selectedIcon = iconKey }
                                    .testTag("icon_choice_$iconKey"),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        themeColor.copy(alpha = 0.2f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    }
                                ),
                                border = if (isSelected) BorderStroke(2.dp, themeColor) else null
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getHabitIcon(iconKey),
                                        contentDescription = label,
                                        tint = if (isSelected) themeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Habit Accent color choices
                Column {
                    Text(
                        text = "Choose Accent Color:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colorChoices.forEach { (colorHex, name) ->
                            val isSelected = selectedColor == colorHex
                            val colorValue = try {
                                Color(android.graphics.Color.parseColor(colorHex))
                            } catch (e: Exception) {
                                Color.Black
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colorValue)
                                    .clickable { selectedColor = colorHex }
                                    .testTag("color_choice_$name"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Category dropdown selection
                Column {
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("habit_category_dropdown_trigger"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(category, color = getCategoryColor(category))
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = getCategoryColor(category))
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = cat,
                                            fontWeight = FontWeight.Bold,
                                            color = getCategoryColor(cat)
                                        )
                                    },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveHabit(title, selectedIcon, selectedColor, category)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("add_habit_dialog_save")
            ) {
                Text("Create Routine")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_habit_dialog_cancel")
            ) {
                Text("Cancel")
            }
        }
    )
}
