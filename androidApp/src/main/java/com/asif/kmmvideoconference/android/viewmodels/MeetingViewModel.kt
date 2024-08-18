package com.asif.kmmvideoconference.android.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoFacade
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState
import com.amazonaws.services.chime.sdk.meetings.session.*
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import com.asif.kmmvideoconference.models.Attendee
import com.asif.kmmvideoconference.models.JoinMeetingResponse
import com.asif.kmmvideoconference.models.MeetingFeatures
import com.asif.kmmvideoconference.repository.ChimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MeetingViewModel(private val repository: ChimeRepository, private val context: Context) : ViewModel(),
    VideoTileObserver {

    // StateFlow for attendees
    private val _attendees = MutableStateFlow<List<Attendee>>(emptyList())
    val attendees: StateFlow<List<Attendee>> = _attendees

    // StateFlow for meeting
    private val _meetingId = MutableStateFlow<String?>(null)
    val meetingId: StateFlow<String?> = _meetingId

    // StateFlow for selected attendee
    private val _selectedAttendee = MutableStateFlow<Attendee?>(null)
    val selectedAttendee: StateFlow<Attendee?> = _selectedAttendee

    // StateFlow for loading state
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // StateFlow for video enable/disable state
    private val _isVideoEnabled = MutableStateFlow(true)
    val isVideoEnabled: StateFlow<Boolean> = _isVideoEnabled

    // StateFlow for audio enable/disable state
    private val _isAudioEnabled = MutableStateFlow(true)
    val isAudioEnabled: StateFlow<Boolean> = _isAudioEnabled

    // StateFlow for errors
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Reference to the AudioVideoFacade from the Chime SDK
    private var audioVideoFacade: AudioVideoFacade? = null

    // Store video tiles
    private val videoTileStates = mutableMapOf<Int, VideoTileState>()

    // Function to create a meeting
    fun createMeeting() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null // Reset error state
            try {
                val meetingId = repository.createMeeting()
                if (meetingId != null) {
                    _meetingId.value = meetingId // Add local attendee as an example
                } else {
                    _error.value = "Failed to create meeting"
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Function to join an existing meeting
    fun joinMeeting(meetingId: String, attendeeName: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null // Reset error state
            try {
                val attendeeId = repository.joinMeeting(meetingId, attendeeName)
                if (attendeeId != null) {
                    _attendees.value += Attendee(attendeeId, "false")
                    initializeMeetingSession(meetingId) // Initialize the meeting session after successfully joining
                } else {
                    _error.value = "Failed to join meeting"
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Function to initialize the meeting session and set the AudioVideoFacade
    private suspend fun initializeMeetingSession(meetingId: String) {
        try {
            // Create the meeting session using the response and set the AudioVideoFacade
            val meetingSession = createMeetingSession(meetingId)
            audioVideoFacade = meetingSession?.audioVideo
            audioVideoFacade?.addVideoTileObserver(this)
        } catch (e: Exception) {
            _error.value = "Failed to initialize meeting session: ${e.message}"
        }
    }

    // VideoTileObserver methods
    override fun onVideoTileAdded(tileState: VideoTileState) {
        videoTileStates[tileState.tileId] = tileState
    }

    override fun onVideoTileRemoved(tileState: VideoTileState) {
        videoTileStates.remove(tileState.tileId)
    }

    override fun onVideoTilePaused(tileState: VideoTileState) {
        videoTileStates[tileState.tileId] = tileState
    }

    override fun onVideoTileResumed(tileState: VideoTileState) {
        videoTileStates[tileState.tileId] = tileState
    }

    override fun onVideoTileSizeChanged(tileState: VideoTileState) {
        // Implement handling for video tile size change
    }

    // Function to bind a video tile to a DefaultVideoRenderView
    fun bindVideoTile(view: DefaultVideoRenderView, videoTileState: VideoTileState) {
        try {
            audioVideoFacade?.bindVideoView(view, videoTileState.tileId)
        } catch (e: Exception) {
            _error.value = "Failed to bind video tile: ${e.message}"
        }
    }

    // Function to unbind a video tile from a DefaultVideoRenderView
    fun unbindVideoTile(videoTileState: VideoTileState) {
        try {
            audioVideoFacade?.unbindVideoView(videoTileState.tileId)
        } catch (e: Exception) {
            _error.value = "Failed to unbind video tile: ${e.message}"
        }
    }

    // Function to toggle video on/off
    fun toggleVideo() {
        try {
            if (_isVideoEnabled.value) {
                audioVideoFacade?.stopLocalVideo()
            } else {
                audioVideoFacade?.startLocalVideo()
            }
            _isVideoEnabled.value = !_isVideoEnabled.value
        } catch (e: Exception) {
            _error.value = "Failed to toggle video: ${e.message}"
        }
    }

    // Function to toggle audio on/off
    fun toggleAudio() {
        try {
            if (_isAudioEnabled.value) {
                audioVideoFacade?.realtimeLocalMute()
            } else {
                audioVideoFacade?.realtimeLocalUnmute()
            }
            _isAudioEnabled.value = !_isAudioEnabled.value
        } catch (e: Exception) {
            _error.value = "Failed to toggle audio: ${e.message}"
        }
    }

    // Function to end the call
    fun endCall() {
        try {
            audioVideoFacade?.stop()
        } catch (e: Exception) {
            _error.value = "Failed to end call: ${e.message}"
        }
    }

    private suspend fun createMeetingSession(meetingId: String): MeetingSession? {
        return try {
            // Normally, you would obtain these details from the backend response
            // Here we assume the meeting details and attendee details are already available

            val meeting = Meeting(
                ExternalMeetingId = "externalMeetingId",  // You need to replace this with actual external meeting ID
                MediaPlacement = MediaPlacement(
                    AudioFallbackUrl = "audioFallbackUrl", // Replace with actual URL
                    AudioHostUrl = "audioHostUrl", // Replace with actual URL
                    SignalingUrl = "signalingUrl", // Replace with actual URL
                    TurnControlUrl = "turnControlUrl" // Replace with actual URL
                ),
                MediaRegion = "us-east-1", // Replace with actual media region
                MeetingId = meetingId,
                MeetingFeatures = MeetingFeatures()// Pass the meeting ID received from the backend
            )

            val attendee = com.amazonaws.services.chime.sdk.meetings.session.Attendee(
                AttendeeId = "attendeeId", // Replace with actual attendee ID
                ExternalUserId = "externalUserId", // Replace with actual external user ID
                JoinToken  = "joinToken" // Replace with actual join token
            )

            // Create the MeetingSessionConfiguration
            val meetingSessionConfiguration = MeetingSessionConfiguration(
                CreateMeetingResponse(meeting),
                CreateAttendeeResponse(attendee),
                ::urlRewriter
            )

            // Create the DefaultMeetingSession
            DefaultMeetingSession(meetingSessionConfiguration, ConsoleLogger(LogLevel.DEBUG), context)
        } catch (exception: Exception) {
            _error.value = "Failed to create meeting session: ${exception.message}"
            null
        }
    }

    private fun urlRewriter(url: String): String {
        // Implement URL rewriting if necessary
        return url
    }

    // Helper function to get VideoTileState for an attendee
    fun getVideoTileStateForAttendee(attendeeId: String): VideoTileState? {
        return videoTileStates.values.find {
            it.attendeeId == attendeeId
        }
    }
}

