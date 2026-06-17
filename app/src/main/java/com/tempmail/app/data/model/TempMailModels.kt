package com.tempmail.app.data.model

data class CreateEmailResponse(
    val uuid: String,
    val email: String,
    val created_at: String,
    val ttl: Int
)

data class Message(
    val id: String,
    val from: String?,
    val to: String?,
    val cc: String?,
    val subject: String?,
    val body_text: String?,
    val body_html: String?,
    val created_at: String?,
    val attachments: List<Attachment>?
)

data class Attachment(
    val id: String,
    val filename: String?,
    val content_type: String?,
    val size: Int?
)

data class ErrorResponse(
    val message: String?,
    val errors: Map<String, List<String>>?
)
