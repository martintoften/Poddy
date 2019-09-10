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

    @Synchronized
    fun getTopTask() = workQueue.firstOrNull()

    @Synchronized
    fun addTask(task: V) {
        workQueue.add(task)
    }

    @Synchronized
    fun removeTask(id: String) {
        workQueue.removeAll { it.id == id }
    }

    @Synchronized
    fun isEmpty() = workQueue.isEmpty()

    @Synchronized
    fun size() = workQueue.size
}
