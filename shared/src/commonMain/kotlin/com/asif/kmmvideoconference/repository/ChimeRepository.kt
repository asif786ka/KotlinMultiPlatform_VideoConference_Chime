package com.asif.kmmvideoconference.repository

import com.asif.kmmvideoconference.models.AttendeeResponse
import com.asif.kmmvideoconference.models.MeetingResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ChimeRepository(private val httpClient: HttpClient) {

    // Function to create a meeting by making a POST request to the backend
    suspend fun createMeeting(): HttpResponse = withContext(Dispatchers.Default) {
        httpClient.post("https://chimebackend-c74fc39330b2.herokuapp.com/createMeeting")
    }

    // Function to join a meeting by making a POST request to the backend
    @OptIn(InternalAPI::class)
    suspend fun joinMeeting(meetingId: String, attendeeName: String): HttpResponse = withContext(Dispatchers.Default) {
        httpClient.post("https://chimebackend-c74fc39330b2.herokuapp.com/joinMeeting") {
            body = mapOf("meetingId" to meetingId, "attendeeName" to attendeeName)
        }
    }
}

