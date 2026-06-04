package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlannerViewModel(
    application: Application,
    private val repository: PlannerRepository
) : AndroidViewModel(application) {

    // Helper functions for date format handling
    companion object {
        fun getCurrentDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Calendar.getInstance().time)
        }

        fun getDayOffsetDateString(offset: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, offset)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(calendar.time)
        }

        fun getFormattedDisplayDate(dateStr: String): String {
            val sdfSource = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfDest = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            return try {
                val date = sdfSource.parse(dateStr)
                if (date != null) sdfDest.format(date) else dateStr
            } catch (e: Exception) {
                dateStr
            }
        }

        fun getFormattedDisplayDayOnly(dateStr: String): String {
            val sdfSource = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfDest = SimpleDateFormat("EEE\nd", Locale.getDefault())
            return try {
                val date = sdfSource.parse(dateStr)
                if (date != null) sdfDest.format(date) else dateStr
            } catch (e: Exception) {
                dateStr
            }
        }
    }

    // Selected date state
    private val _selectedDate = MutableStateFlow(getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Trigger update for habits or others if needed
    init {
        viewModelScope.launch {
            repository.ensureDefaultHabitsExist()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDate: StateFlow<List<PlannerTask>> = _selectedDate
        .flatMapLatest { date -> repository.getTasksForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventsForSelectedDate: StateFlow<List<PlannerEvent>> = _selectedDate
        .flatMapLatest { date -> repository.getEventsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeHabits: StateFlow<List<Habit>> = repository.getActiveHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val completionsForSelectedDate: StateFlow<List<HabitCompletion>> = _selectedDate
        .flatMapLatest { date -> repository.getHabitCompletionsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyNoteForSelectedDate: StateFlow<DailyNote?> = _selectedDate
        .flatMapLatest { date -> repository.getDailyNoteForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Select date
    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    // --- Task Mutators ---
    fun addTask(title: String, description: String, priority: String, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = PlannerTask(
                title = title.trim(),
                description = description.trim(),
                date = _selectedDate.value,
                priority = priority,
                category = category,
                isCompleted = false
            )
            repository.insertTask(task)
        }
    }

    fun toggleTaskCompletion(task: PlannerTask) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: PlannerTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }


    // --- Event Mutators ---
    fun addEvent(title: String, description: String, startTime: String, endTime: String, category: String) {
        if (title.isBlank() || startTime.isBlank() || endTime.isBlank()) return
        viewModelScope.launch {
            val event = PlannerEvent(
                title = title.trim(),
                description = description.trim(),
                date = _selectedDate.value,
                startTime = startTime,
                endTime = endTime,
                category = category
            )
            repository.insertEvent(event)
        }
    }

    fun deleteEvent(event: PlannerEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }


    // --- Habits Mutators ---
    fun addHabit(title: String, iconName: String, colorHex: String, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val habit = Habit(
                title = title.trim(),
                iconName = iconName,
                colorHex = colorHex,
                category = category,
                isActive = true
            )
            repository.insertHabit(habit)
        }
    }

    fun toggleHabitCompletion(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, _selectedDate.value, isCompleted)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }


    // --- Notes/Focus Mutators ---
    fun saveDailyNote(focus: String, reflection: String, moodRating: Int) {
        viewModelScope.launch {
            val currentNote = dailyNoteForSelectedDate.value
            val note = DailyNote(
                date = _selectedDate.value,
                focus = focus.trim(),
                reflectionText = reflection.trim(),
                moodRating = moodRating
            )
            repository.saveDailyNote(note)
        }
    }

    fun saveFocusGoal(focus: String) {
        viewModelScope.launch {
            val currentNote = dailyNoteForSelectedDate.value ?: DailyNote(date = _selectedDate.value)
            repository.saveDailyNote(currentNote.copy(focus = focus.trim()))
        }
    }

    fun saveReflection(reflection: String, moodRating: Int) {
        viewModelScope.launch {
            val currentNote = dailyNoteForSelectedDate.value ?: DailyNote(date = _selectedDate.value)
            repository.saveDailyNote(currentNote.copy(
                reflectionText = reflection.trim(),
                moodRating = moodRating
            ) )
        }
    }
}

class PlannerViewModelFactory(
    private val application: Application,
    private val repository: PlannerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlannerViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
