package com.asif.kmmvideoconference.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAttributeName
import com.asif.kmmvideoconference.android.viewmodels.MeetingViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MeetingViewModel) {
    // Collect the state of attendees from the ViewModel
    val meetingId = EventAttributeName.attendeeId

    // Layout for the home screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button to create a new meeting
        Button(
            onClick = { viewModel.createMeeting() },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Create Meeting")
        }

        // Display the meeting ID if available
        meetingId.let {
            Text(text = "Meeting ID: $it", modifier = Modifier.padding(top = 16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Button to navigate to the MeetingScreen
            Button(
                onClick = { navController.navigate("meeting") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Go to Meeting")
            }
        }

        // Display any error messages from the ViewModel
        viewModel.error.collectAsState().value?.let { error ->
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}
