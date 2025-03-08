package com.example.finalproject.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoViewModel(private val videoDao: VideoDao) : ViewModel() {
    val videos: StateFlow<List<Video>> = videoDao.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            val existingUsers = videoDao.getAll().firstOrNull()
            if (existingUsers.isNullOrEmpty()) {
                videoDao.insertAll(Video(title = "Tetris Theme", link = "QT3X-qyIh8s"))
            }
        }
    }

    fun addVideo(video: Video) {
        viewModelScope.launch {
            videoDao.insertAll(video)
        }
    }
}

class VideoViewModelFactory(private val videoDao: VideoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoViewModel(videoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}