package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PlannerDatabase
import com.example.data.PlannerRepository
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core room initialization
        val database = PlannerDatabase.getDatabase(applicationContext)
        val repository = PlannerRepository(database.plannerDao())

        setContent {
            MyApplicationTheme {
                // Instantiate our ViewModel safely
                val viewModel: PlannerViewModel by viewModels {
                    PlannerViewModelFactory(application, repository)
                }

                // Main screen view
                PlannerDashboard(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerDashboard(viewModel: PlannerViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val tasks by viewModel.tasksForSelectedDate.collectAsStateWithLifecycle()
    val events by viewModel.eventsForSelectedDate.collectAsStateWithLifecycle()
    val habits by viewModel.activeHabits.collectAsStateWithLifecycle()
    val completions by viewModel.completionsForSelectedDate.collectAsStateWithLifecycle()
    val dailyNote by viewModel.dailyNoteForSelectedDate.collectAsStateWithLifecycle()

    // Screen dialogue overlays state
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }

    // Selected tab: 0=Checklist, 1=Timeline, 2=Habits, 3=Reflect
    var currentTab by remember { mutableIntStateOf(0) }

    // Progression Stats
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val totalHabits = habits.size
    val completedHabits = habits.count { h -> completions.any { it.habitId == h.id } }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                // Simple Header Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DailyFlow",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("app_title")
                    )
                }

                // Interactive horizontal week list calendar picker strip
                HorizontalCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { date -> viewModel.selectDate(date) }
                )

                // Unified Day Progression details widget
                ProgressHeader(
                    totalTasks = totalTasks,
                    completedTasks = completedTasks,
                    totalHabits = totalHabits,
                    completedHabits = completedHabits
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = currentTab,
                    modifier = Modifier.fillMaxWidth().testTag("tab_row")
                ) {
                    Tab(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        modifier = Modifier.testTag("tab_checklist"),
                        icon = {
                            BadgedBox(
                                badge = {
                                    val remaining = totalTasks - completedTasks
                                    if (remaining > 0) {
                                        Badge { Text(remaining.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = "Checklist Tasks")
                            }
                        },
                        text = { Text("Checklist") }
                    )
                    Tab(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        modifier = Modifier.testTag("tab_timeline"),
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (events.isNotEmpty()) {
                                        Badge { Text(events.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = "Timeline Schedule")
                            }
                        },
                        text = { Text("Timeline") }
                    )
                    Tab(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        modifier = Modifier.testTag("tab_habits"),
                        icon = {
                            BadgedBox(
                                badge = {
                                    val remaining = totalHabits - completedHabits
                                    if (remaining > 0) {
                                        Badge { Text(remaining.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Habit Routines")
                            }
                        },
                        text = { Text("Habits") }
                    )
                    Tab(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        modifier = Modifier.testTag("tab_journal"),
                        icon = {
                            Icon(Icons.Default.EditNote, contentDescription = "Reflection Log")
                        },
                        text = { Text("Reflect") }
                    )
                }
            }
        },
        bottomBar = {
            // Handled safe area for standard gesture indicator / bottom nav spacing
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> TasksTabContent(
                    tasks = tasks,
                    onToggleTask = { viewModel.toggleTaskCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onAddTaskClick = { showAddTaskDialog = true },
                    onQuickAddTask = { title, desc, priority, cat ->
                        viewModel.addTask(title, desc, priority, cat)
                    }
                )
                1 -> ScheduleTabContent(
                    events = events,
                    onDeleteEvent = { viewModel.deleteEvent(it) },
                    onAddEventClick = { showAddEventDialog = true }
                )
                2 -> HabitsTabContent(
                    habits = habits,
                    completions = completions,
                    onToggleHabitCompletion = { id, done -> viewModel.toggleHabitCompletion(id, done) },
                    onDeleteHabit = { viewModel.deleteHabit(it) },
                    onAddHabitClick = { showAddHabitDialog = true }
                )
                3 -> ReflectionTabContent(
                    dailyNote = dailyNote,
                    onSaveFocus = { viewModel.saveFocusGoal(it) },
                    onSaveReflection = { ref, mood -> viewModel.saveReflection(ref, mood) }
                )
            }
        }
    }

    // --- Overlay Dialogue Modals ---
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onSaveTask = { title, desc, priority, cat ->
                viewModel.addTask(title, desc, priority, cat)
            }
        )
    }

    if (showAddEventDialog) {
        AddEventDialog(
            onDismiss = { showAddEventDialog = false },
            onSaveEvent = { title, desc, start, end, cat ->
                viewModel.addEvent(title, desc, start, end, cat)
            }
        )
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onSaveHabit = { title, icon, color, cat ->
                viewModel.addHabit(title, icon, color, cat)
            }
        )
    }
}

