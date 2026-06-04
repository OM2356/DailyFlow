package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {

    // --- Tasks ---
    @Query("SELECT * FROM planner_tasks WHERE date = :date ORDER BY priority DESC, id ASC")
    fun getTasksForDate(date: String): Flow<List<PlannerTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: PlannerTask): Long

    @Update
    suspend fun updateTask(task: PlannerTask)

    @Delete
    suspend fun deleteTask(task: PlannerTask)

    @Query("DELETE FROM planner_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)


    // --- Events (Schedule) ---
    @Query("SELECT * FROM planner_events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsForDate(date: String): Flow<List<PlannerEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: PlannerEvent): Long

    @Update
    suspend fun updateEvent(event: PlannerEvent)

    @Delete
    suspend fun deleteEvent(event: PlannerEvent)

    @Query("DELETE FROM planner_events WHERE id = :id")
    suspend fun deleteEventById(id: Int)


    // --- Habits ---
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY id ASC")
    fun getActiveHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)


    // --- Habit Completions ---
    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getHabitCompletionsForDate(date: String): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitCompletion(habitId: Int, date: String)


    // --- Daily Notes ---
    @Query("SELECT * FROM daily_notes WHERE date = :date LIMIT 1")
    fun getDailyNoteForDate(date: String): Flow<DailyNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyNote(note: DailyNote): Long
}
