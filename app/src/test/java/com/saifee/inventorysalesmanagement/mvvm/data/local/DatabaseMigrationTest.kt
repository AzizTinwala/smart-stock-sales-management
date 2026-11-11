package com.saifee.inventorysalesmanagement.mvvm.data.local

import com.saifee.inventorysalesmanagement.mvvm.di.DatabaseModule
import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseMigrationTest {

    @Test
    fun `MIGRATION_5_6 starts from 5 and ends at 6`() {
        val migration = DatabaseModule.MIGRATION_5_6
        assertEquals(5, migration.startVersion)
        assertEquals(6, migration.endVersion)
    }
}
