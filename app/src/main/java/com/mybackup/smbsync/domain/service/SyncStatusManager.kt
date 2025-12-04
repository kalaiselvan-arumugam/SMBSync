package com.mybackup.smbsync.domain.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStatusManager @Inject constructor() {
    private val _runningTasks = MutableStateFlow<Set<Long>>(emptySet())
    val runningTasks = _runningTasks.asStateFlow()

    fun setTaskRunning(configId: Long, isRunning: Boolean) {
        val current = _runningTasks.value.toMutableSet()
        if (isRunning) {
            current.add(configId)
        } else {
            current.remove(configId)
        }
        _runningTasks.value = current
    }
}
