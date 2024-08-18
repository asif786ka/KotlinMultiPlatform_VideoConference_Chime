package com.asif.kmmvideoconference.models

import kotlinx.serialization.Serializable


@Serializable
data class JoinMeetingResponse(
    val joinInfo: JoinInfo
)

@Serializable
data class JoinInfo(
    val meetingResponse: MeetingResponse,
    val attendeeResponse: AttendeeResponse
)

@Serializable
data class MeetingResponse(
    val meeting: Meeting
)

@Serializable
data class Meeting(
    val ExternalMeetingId: String,
    val MediaPlacement: MediaPlacement,
    val MediaRegion: String,
    val MeetingId: String,
    val MeetingFeatures: MeetingFeatures?
)

@Serializable
data class AttendeeResponse(
    val attendee: Attendee
)
@Serializable
data class JoinMeetingRequest(
    val meetingId: String,
    val attendeeName: String
)


@Serializable
data class Attendee(
    val AttendeeId: String,
    val ExternalUserId: String
)

@Serializable
data class MediaPlacement(
    val AudioHostUrl: String,
    val ScreenDataUrl: String,
    val ScreenSharingUrl: String,
    val SignalingUrl: String,
    val TurnControlUrl: String
)

@Serializable
data class MeetingFeatures(
    val video: VideoFeatures?,
    val content: ContentFeatures?
)

@Serializable
data class VideoFeatures(
    val MaxResolution: VideoResolution
)

@Serializable
data class ContentFeatures(
    val MaxResolution: VideoResolution
)

@Serializable
data class VideoResolution(
    val Width: Int,
    val Height: Int
)
