package com.asif.kmmvideoconference.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAttributeName
import com.asif.kmmvideoconference.android.viewmodels.MeetingViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MeetingViewModel) {
    val loading by viewModel.loading.collectAsState()
    val meetingId by viewModel.meetingId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loading) {
            CircularProgressIndicator() // Show loading indicator
        } else {
            Button(
                onClick = { viewModel.createMeeting() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Create Meeting")
            }


            meetingId.let {
                Text(text = "Meeting ID: $it", modifier = Modifier.padding(top = 16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("meeting/${meetingId}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Go to Join Meeting")
                }
            }
        }
    }
}

