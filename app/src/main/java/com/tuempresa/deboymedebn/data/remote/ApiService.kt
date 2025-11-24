package com.tuempresa.deboymedebn.data.remote

import com.tuempresa.deboymedebn.model.Contact
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("contacts")
    suspend fun getContacts(): Response<List<Contact>>

    @POST("contacts")
    suspend fun createContact(@Body contact: Contact): Response<Map<String, String>>

    @PUT("contacts/{id}")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body contact: Contact
    ): Response<Void>

    @PATCH("contacts/{id}")
    suspend fun patchContact(
        @Path("id") id: String,
        @Body contact: Map<String, Any>
    ): Response<Void>

    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Path("id") id: String
    ): Response<Void>
}
