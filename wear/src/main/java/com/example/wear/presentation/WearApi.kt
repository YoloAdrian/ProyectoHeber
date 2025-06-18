package com.example.wear.presentation

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WearApi {
    @POST("client/vinculacion")
    suspend fun linkSmartwatch(
        @Body request: LinkSmartwatchRequest
    ): Response<LinkSmartwatchResponse>

    @POST("client/desvincular_smartwatch")
    suspend fun unlinkSmartwatch(
        @Body request: UnlinkSmartwatchRequest
    ): Response<Void> // o Response<Any> por si se retorna JSON el backend
}