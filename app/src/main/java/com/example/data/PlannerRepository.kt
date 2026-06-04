package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class PlannerRepository(private val plannerDao: PlannerDao) {

    // --- Tasks ---
    fun getTasksForDate(date: String): Flow<List<PlannerTask>> =
        plannerDao.getTasksForDate(date)

    suspend fun insertTask(task: PlannerTask): Long =
        plannerDao.insertTask(task)

    suspend fun updateTask(task: PlannerTask) =
        plannerDao.updateTask(task)

    suspend fun deleteTask(task: PlannerTask) =
        plannerDao.deleteTask(task)

    suspend fun deleteTaskById(id: Int) =
        plannerDao.deleteTaskById(id)


    // --- Events ---
    fun getEventsForDate(date: String): Flow<List<PlannerEvent>> =
        plannerDao.getEventsForDate(date)

    suspend fun insertEvent(event: PlannerEvent): Long =
        plannerDao.insertEvent(event)

    suspend fun updateEvent(event: PlannerEvent) =
        plannerDao.updateEvent(event)

    suspend fun deleteEvent(event: PlannerEvent) =
        plannerDao.deleteEvent(event)

    suspend fun deleteEventById(id: Int) =
        plannerDao.deleteEventById(id)


    // --- Habits ---
    fun getActiveHabits(): Flow<List<Habit>> =
        plannerDao.getActiveHabits()

    suspend fun insertHabit(habit: Habit): Long =
        plannerDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) =
        plannerDao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) =
        plannerDao.deleteHabit(habit)

    suspend fun ensureDefaultHabitsExist() {
        val habits = plannerDao.getActiveHabits().first()
        if (habits.isEmpty()) {
            val defaults = listOf(
                Habit(title = "Drink 8 glasses of water", iconName = "LocalCafe", colorHex = "#FF2196F3", category = "Health"),
                Habit(title = "30 minutes workout", iconName = "FitnessCenter", colorHex = "#FF4CAF50", category = "Health"),
                Habit(title = "Read 10 pages of a book", iconName = "MenuBook", colorHex = "#FFFF9800", category = "Study"),
                Habit(title = "15 minutes mindfulness meditation", iconName = "SelfImprovement", colorHex = "#FF9C27B0", category = "Personal")
            )
            for (habit in defaults) {
                plannerDao.insertHabit(habit)
            }
        }
    }


    // --- Habit Completions ---
    fun getHabitCompletionsForDate(date: String): Flow<List<HabitCompletion>> =
        plannerDao.getHabitCompletionsForDate(date)

    suspend fun toggleHabitCompletion(habitId: Int, date: String, shouldComplete: Boolean) {
        if (shouldComplete) {
            plannerDao.insertHabitCompletion(HabitCompletion(habitId = habitId, date = date))
        } else {
            plannerDao.deleteHabitCompletion(habitId = habitId, date = date)
        }
    }


    // --- Daily Notes ---
    fun getDailyNoteForDate(date: String): Flow<DailyNote?> =
        plannerDao.getDailyNoteForDate(date)

    suspend fun saveDailyNote(note: DailyNote): Long =
        plannerDao.insertDailyNote(note)
}
