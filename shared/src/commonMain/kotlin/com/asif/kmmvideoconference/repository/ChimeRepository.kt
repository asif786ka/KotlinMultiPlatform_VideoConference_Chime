package com.asif.kmmvideoconference.repository

import co.touchlab.kermit.Logger
import com.asif.kmmvideoconference.models.JoinMeetingRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChimeRepository(private val httpClient: HttpClient) {

    private val logger = Logger.withTag("ChimeVideoRepository")

    // Function to create a meeting by making a POST request to the backend
    // Function to create a meeting by making a POST request to the backend
    suspend fun createMeeting(): String? = withContext(Dispatchers.Default) {
        try {
            logger.d { "Attempting to create a meeting" }
            val response = httpClient.post("https://chimebackend-c74fc39330b2.herokuapp.com/createMeeting")
            val rawResponse = response.bodyAsText()
            logger.d { "Raw response: $rawResponse" }

            if (rawResponse.isBlank()) {
                logger.e { "Received an empty response from the API" }
                null
            } else {
                val jsonResponse = Json.parseToJsonElement(rawResponse).jsonObject
                val meetingId = jsonResponse["Meeting"]?.jsonObject?.get("MeetingId")?.jsonPrimitive?.content
                logger.d { "Parsed Meeting ID: $meetingId" }
                meetingId
            }
        } catch (e: Exception) {
            logger.e(e) { "Failed to create a meeting" }
            null
        }
    }

    // Function to join a meeting by making a POST request to the backend
    @OptIn(InternalAPI::class)
    suspend fun joinMeeting(meetingId: String, attendeeName: String): String? = withContext(Dispatchers.Default) {
        try {
            logger.d { "Attempting to join meeting: $meetingId with attendee: $attendeeName" }

            // Create the request object and serialize it to JSON
            val requestBody = JoinMeetingRequest(meetingId, attendeeName)
            val jsonBody = Json.encodeToString(requestBody)

            // Perform the POST request with the serialized JSON string
            val response = httpClient.post("https://chimebackend-c74fc39330b2.herokuapp.com/joinMeeting") {
                contentType(ContentType.Application.Json)  // Manually set the content type
                setBody(jsonBody)  // Set the body as the serialized JSON string
            }

            // Handle the response
            val rawResponse = response.bodyAsText()
            logger.d { "Raw response: $rawResponse" }

            if (rawResponse.isBlank()) {
                logger.e { "Received an empty response from the API" }
                null
            } else {
                val jsonResponse = Json.parseToJsonElement(rawResponse).jsonObject
                val attendeeId = jsonResponse["Attendee"]?.jsonObject?.get("AttendeeId")?.jsonPrimitive?.content
                logger.d { "Parsed Attendee ID: $attendeeId" }
                attendeeId
            }
        } catch (e: Exception) {
            logger.e(e) { "Failed to join meeting" }
            null
        }
    }
}
