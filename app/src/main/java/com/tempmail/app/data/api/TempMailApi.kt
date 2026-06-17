package com.tempmail.app.data.api

import com.tempmail.app.data.model.CreateEmailResponse
import com.tempmail.app.data.model.Message
import retrofit2.Response
import retrofit2.http.*

interface TempMailApi {

    @POST("api/v3/email/new")
    suspend fun createEmail(): Response<CreateEmailResponse>

    @GET("api/v3/email/{email}/messages")
    suspend fun getMessages(
        @Path("email", encoded = true) email: String
    ): Response<List<Message>>
}
