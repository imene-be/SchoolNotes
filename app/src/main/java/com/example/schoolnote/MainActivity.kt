package com.example.schoolnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(applicationContext, SchoolDb::class.java, "school").build()
        val dao = db.dao()

        setContent {
            MaterialTheme {
                val scope = rememberCoroutineScope()
                val students by dao.getAllStudents().collectAsState(initial = emptyList())
                var name by remember { mutableStateOf("") }

                Column(Modifier.padding(16.dp)) {

                    Row {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Nom de l'élève...") }
                        )
                        Button(onClick = {
                            scope.launch {
                                if (name.isNotBlank()) {
                                    dao.insertStudent(Student(name = name))
                                    name = ""
                                }
                            }
                        }) { Text("Ajouter Élève") }
                    }

                    Spacer(Modifier.height(20.dp))

                    LazyColumn {
                        items(students.size) { index ->
                            val grades by dao.getGradesForStudent(students[index].studentId)
                                .collectAsState(initial = emptyList())

                            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(Modifier.padding(8.dp)) {
                                    Text(
                                        "Élève : ${students[index].name}",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text("Notes : ${grades.map { it.value }.joinToString(", ")}")
                                    Button(onClick = {
                                        scope.launch {
                                            dao.insertGrade(
                                                Grade(
                                                    ownerId = students[index].studentId,
                                                    value = (10..20).random()
                                                )
                                            )
                                        }
                                    }) { Text("Donner une note aléatoire") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
