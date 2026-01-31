package com.indelo.goods.data.ai

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class AnthropicMessage(
    val role: String,
    val content: String
)

data class AnthropicRequest(
    val model: String = "claude-sonnet-4-20250514",
    val max_tokens: Int = 1024,
    val messages: List<AnthropicMessage>
)

data class ContentBlock(
    val type: String,
    val text: String
)

data class AnthropicResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ContentBlock>,
    val model: String,
    val stop_reason: String?,
    val usage: Usage?
)

data class Usage(
    val input_tokens: Int,
    val output_tokens: Int
)

interface AnthropicApi {
    @POST("v1/messages")
    suspend fun createMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Header("content-type") contentType: String = "application/json",
        @Body request: AnthropicRequest
    ): Response<AnthropicResponse>
}
