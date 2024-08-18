package com.asif.kmmvideoconference.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAttributeName
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.asif.kmmvideoconference.android.viewmodels.MeetingViewModel
import com.asif.kmmvideoconference.models.Attendee

@Composable
fun MeetingScreen(viewModel: MeetingViewModel) {
    // Collect states from the ViewModel
    val isVideoEnabled = viewModel.isVideoEnabled.collectAsState().value
    val isAudioEnabled = viewModel.isAudioEnabled.collectAsState().value
    val attendees = viewModel.attendees.collectAsState().value

    // Layout for the meeting screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Title for the video grid
        Text(
            text = "Video Grid",
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Display the video grid for attendees
        VideoGrid(attendees, viewModel)

        Spacer(modifier = Modifier.weight(1f))

        // Controls for video, audio, and ending the call
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Toggle video on/off
            Button(onClick = { viewModel.toggleVideo() }) {
                Text(if (isVideoEnabled) "Disable Video" else "Enable Video")
            }
            // Toggle audio on/off
            Button(onClick = { viewModel.toggleAudio() }) {
                Text(if (isAudioEnabled) "Mute" else "Unmute")
            }
            // End the call
            Button(onClick = { viewModel.endCall() }) {
                Text("End Call")
            }
        }
    }
}

@Composable
fun VideoGrid(attendees: List<Attendee>, viewModel: MeetingViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Create a grid with two columns
        contentPadding = PaddingValues(8.dp) // Padding for the grid
    ) {
        // Iterate over attendees to display their video tiles
        items(attendees.size) { index ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(Color.Gray)
                    .padding(8.dp)
            ) {
                // Get the VideoTileState for the current attendee
                val videoTileState = viewModel.getVideoTileStateForAttendee(EventAttributeName.attendeeId.toString())
                if (videoTileState != null) {
                    // Attach the video tile to render the video stream
                    AndroidView(
                        factory = { context ->
                            DefaultVideoRenderView(context).apply {
                                viewModel.bindVideoTile(this, videoTileState)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Display a placeholder text if video is not available
                    Text(text = "Attendee ${EventAttributeName.attendeeId}")
                }
            }
        }
    }
}

