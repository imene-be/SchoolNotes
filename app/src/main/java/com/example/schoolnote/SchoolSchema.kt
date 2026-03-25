package com.example.schoolnote

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Student(
    @PrimaryKey(autoGenerate = true) val studentId: Int = 0,
    val name: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Grade(
    @PrimaryKey(autoGenerate = true) val gradeId: Int = 0,
    val ownerId: Int,
    val value: Int
)

@Dao
interface SchoolDao {
    @Insert suspend fun insertStudent(student: Student)
    @Insert suspend fun insertGrade(grade: Grade)

    @Query("SELECT * FROM Student")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM Grade WHERE ownerId = :sId")
    fun getGradesForStudent(sId: Int): Flow<List<Grade>>
}

@Database(entities = [Student::class, Grade::class], version = 1)
abstract class SchoolDb : RoomDatabase() {
    abstract fun dao(): SchoolDao
}
