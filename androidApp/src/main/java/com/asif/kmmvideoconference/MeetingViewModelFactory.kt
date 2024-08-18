package com.asif.kmmvideoconference

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.asif.kmmvideoconference.android.viewmodels.MeetingViewModel
import com.asif.kmmvideoconference.repository.ChimeRepository

class MeetingViewModelFactory(
    private val repository: ChimeRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeetingViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
