package com.example.safisha.data.local.entity.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.safisha.data.local.SafishaDatabase
import com.example.safisha.data.local.dao.SafishaDao
import com.example.safisha.data.local.entity.IssueReportEntity
import com.example.safisha.data.local.entity.UserEntity
import com.example.safisha.domain.model.IssueCategory
import com.example.safisha.domain.model.ReportStatus
import com.example.safisha.domain.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SafishaDaoTest {

    private lateinit var database: SafishaDatabase
    private lateinit var dao: SafishaDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            SafishaDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.safishaDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertAndRetrieveUser() = runBlocking {
        val user = UserEntity(
            fullName = "Test Resident",
            email = "resident@test.com",
            passwordHash = "test_hash",
            role = UserRole.RESIDENT
        )

        val userId = dao.insertUser(user)

        val retrievedUser = dao.getUserByEmail("resident@test.com")

        assertTrue(userId > 0)
        assertNotNull(retrievedUser)
        assertEquals("Test Resident", retrievedUser?.fullName)
        assertEquals(UserRole.RESIDENT, retrievedUser?.role)
    }

    @Test
    fun insertAndRetrieveReport() = runBlocking {
        val userId = dao.insertUser(
            UserEntity(
                fullName = "Test Resident",
                email = "resident@test.com",
                passwordHash = "test_hash",
                role = UserRole.RESIDENT
            )
        )

        val report = IssueReportEntity(
            categoryId = IssueCategory.ROAD_DAMAGE,
            location = "Nairobi CBD",
            description = "Large pothole on the road",
            reporterName = "Test Resident",
            reporterId = userId.toInt()
        )

        val reportId = dao.insertReport(report)
        val reports = dao.getAllReports().first()

        assertTrue(reportId > 0)
        assertEquals(1, reports.size)
        assertEquals(IssueCategory.ROAD_DAMAGE, reports[0].categoryId)
        assertEquals(ReportStatus.PENDING, reports[0].status)
    }

    @Test
    fun retrieveReportsByUser() = runBlocking {
        val firstUserId = dao.insertUser(
            UserEntity(
                fullName = "Resident One",
                email = "one@test.com",
                passwordHash = "hash1",
                role = UserRole.RESIDENT
            )
        )

        val secondUserId = dao.insertUser(
            UserEntity(
                fullName = "Resident Two",
                email = "two@test.com",
                passwordHash = "hash2",
                role = UserRole.RESIDENT
            )
        )

        dao.insertReport(
            IssueReportEntity(
                categoryId = IssueCategory.ROAD_DAMAGE,
                location = "CBD",
                description = "Pothole",
                reporterName = "Resident One",
                reporterId = firstUserId.toInt()
            )
        )

        dao.insertReport(
            IssueReportEntity(
                categoryId = IssueCategory.WASTE,
                location = "Westlands",
                description = "Overflowing bin",
                reporterName = "Resident Two",
                reporterId = secondUserId.toInt()
            )
        )

        val reports = dao.getReportsByUser(firstUserId.toInt()).first()

        assertEquals(1, reports.size)
        assertEquals("Resident One", reports[0].reporterName)
    }

    @Test
    fun updateReportStatus() = runBlocking {
        val userId = dao.insertUser(
            UserEntity(
                fullName = "Test Resident",
                email = "resident@test.com",
                passwordHash = "hash",
                role = UserRole.RESIDENT
            )
        )

        val reportId = dao.insertReport(
            IssueReportEntity(
                categoryId = IssueCategory.WASTE,
                location = "Kilimani",
                description = "Uncollected waste",
                reporterName = "Test Resident",
                reporterId = userId.toInt()
            )
        )

        val updatedRows = dao.updateReportStatus(
            reportId.toInt(),
            ReportStatus.RESOLVED
        )

        val reports = dao.getAllReports().first()

        assertEquals(1, updatedRows)
        assertEquals(ReportStatus.RESOLVED, reports[0].status)
    }

    @Test
    fun deleteReport() = runBlocking {
        val userId = dao.insertUser(
            UserEntity(
                fullName = "Test Resident",
                email = "resident@test.com",
                passwordHash = "hash",
                role = UserRole.RESIDENT
            )
        )

        val report = IssueReportEntity(
            categoryId = IssueCategory.DRAINAGE,
            location = "Parklands",
            description = "Blocked drainage",
            reporterName = "Test Resident",
            reporterId = userId.toInt()
        )

        val reportId = dao.insertReport(report)

        dao.deleteReportById(reportId.toInt())

        val reports = dao.getAllReports().first()

        assertTrue(reports.isEmpty())
    }
}
