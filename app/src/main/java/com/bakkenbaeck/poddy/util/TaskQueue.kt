package com.bakkenbaeck.poddy.util

interface Task {
    val id: String
}

data class DownloadTask(
    override val id: String,
    val name: String,
    val url: String
) : Task

class SafeQueue<V : Task> {
    private val workQueue = mutableListOf<V>()

    fun getTopTask() = workQueue.firstOrNull()

    fun addTask(task: V) {
        workQueue.add(task)
    }

    fun removeTask(id: String) {
        workQueue.removeAll { it.id == id }
    }

    fun isEmpty() = workQueue.isEmpty()

    fun size() = workQueue.size
}
