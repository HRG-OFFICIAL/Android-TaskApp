package com.example.data.remote

import android.util.Log
import com.example.data.remote.dto.TaskDto
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface TaskRemoteDataSource {
    suspend fun getTasks(userId: String): List<TaskDto>
    suspend fun getTask(userId: String, taskId: String): TaskDto?
    suspend fun createTask(userId: String, task: TaskDto): String
    suspend fun updateTask(userId: String, taskId: String, task: TaskDto)
    suspend fun deleteTask(userId: String, taskId: String)
    fun observeTasks(userId: String): Flow<List<TaskDto>>
}

@Singleton
class TaskRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TaskRemoteDataSource {

    companion object {
        private const val TAG = "TaskRemoteDataSource"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_TASKS = "tasks"
    }

    override suspend fun getTasks(userId: String): List<TaskDto> {
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TASKS)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(TaskDto::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks", e)
            emptyList()
        }
    }

    override suspend fun getTask(userId: String, taskId: String): TaskDto? {
        return try {
            val doc = firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_TASKS)
                .document(taskId)
                .get()
                .await()

            doc.toObject(TaskDto::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting task", e)
            null
        }
    }

    override suspend fun createTask(userId: String, task: TaskDto): String {
        val doc = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TASKS)
            .add(task)
            .await()
        
        return doc.id
    }

    override suspend fun updateTask(userId: String, taskId: String, task: TaskDto) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TASKS)
            .document(taskId)
            .set(task)
            .await()
    }

    override suspend fun deleteTask(userId: String, taskId: String) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TASKS)
            .document(taskId)
            .delete()
            .await()
    }

    override fun observeTasks(userId: String): Flow<List<TaskDto>> = callbackFlow {
        val listener = firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_TASKS)
            .orderBy("updatedAtEpochMillis", Query.Direction.DESCENDING)
            .addSnapshotListener(MetadataChanges.EXCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing tasks", error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TaskDto::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }
}