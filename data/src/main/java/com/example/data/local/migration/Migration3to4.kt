package com.example.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns to tasks table
        database.execSQL("ALTER TABLE tasks ADD COLUMN categoryId INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE tasks ADD COLUMN isImportant INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE tasks ADD COLUMN progress REAL NOT NULL DEFAULT 0.0")
        database.execSQL("ALTER TABLE tasks ADD COLUMN colorIndex INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE tasks ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
    }
}