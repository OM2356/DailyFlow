package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "planner_tasks")
data class PlannerTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val date: String, // format: "yyyy-MM-dd"
    val priority: String = "Medium", // High, Medium, Low
    val category: String = "Personal", // Work, Personal, Health, Study, Finance, Social
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "planner_events")
data class PlannerEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val date: String, // format: "yyyy-MM-dd"
    val startTime: String, // format: "HH:mm"
    val endTime: String, // format: "HH:mm"
    val category: String = "Personal" // Work, Personal, Health, Study, Finance, Social
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val iconName: String = "Check", // "Check", "DirectionsRun", "LocalCafe", "MenuBook", "FitnessCenter", "Pets", "Brush", "SelfImprovement"
    val colorHex: String = "#FF6200EE", // custom styling color hex representation
    val category: String = "Personal",
    val isActive: Boolean = true
)

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String, // format: "yyyy-MM-dd"
    val isCompleted: Boolean = true
)

@Entity(tableName = "daily_notes")
data class DailyNote(
    @PrimaryKey val date: String, // format: "yyyy-MM-dd"
    val focus: String = "",
    val reflectionText: String = "",
    val moodRating: Int = 0 // 1-5 rating, 0 means not set
)
