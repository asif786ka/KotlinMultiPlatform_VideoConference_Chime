package com.asif.kmmvideoconference.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.asif.kmmvideoconference.android.viewmodels.MeetingViewModel
import com.asif.kmmvideoconference.models.Attendee

@Composable
fun MeetingScreen(
    viewModel: MeetingViewModel,
    initialMeetingId: String?,
    navController: NavController // Navigation controller passed here
) {
    // Collect states from the ViewModel
    val isVideoEnabled = viewModel.isVideoEnabled.collectAsState().value
    val isAudioEnabled = viewModel.isAudioEnabled.collectAsState().value
    val attendees = viewModel.attendees.collectAsState().value
    val loading = viewModel.loading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val meetingId = remember { mutableStateOf(initialMeetingId) }
    val attendeeId = remember { mutableStateOf("") }
    val meetingJoined = remember { mutableStateOf(false) } // Track if the meeting was successfully joined

    // Layout for the meeting screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (!meetingJoined.value) {
            // Show error message if any
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Meeting ID and Attendee ID input fields
            meetingId.value?.let {
                TextField(
                    value = it,
                    onValueChange = { meetingId.value = it },
                    label = { Text("Enter Meeting ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = attendeeId.value,
                onValueChange = { attendeeId.value = it },
                label = { Text("Enter Attendee ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Join Meeting Button
            Button(
                onClick = {
                    meetingId.value?.let { viewModel.joinMeeting(it, attendeeId.value) }
                    meetingJoined.value = true // Update the state to indicate the meeting is joined
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Join Meeting")
            }
        } else {
            // Title for the video grid
            Text(
                text = "Video Grid",
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                Button(onClick = {
                    viewModel.endCall()
                    navController.navigate("home") { // Navigate back to the home screen
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }) {
                    Text("End Call")
                }
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
                val videoTileState = viewModel.getVideoTileStateForAttendee(attendees[index].AttendeeId)
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
                    Text(text = "Attendee ${attendees[index].AttendeeId}", color = Color.White, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
