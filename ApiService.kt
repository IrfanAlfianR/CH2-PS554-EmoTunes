package com.Capstone.musicPlayer.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("{emosi}") // Sesuaikan dengan endpoint API yang sesuai
    suspend fun getSong(): Response
    @POST("uploadSong")
    fun uploadSong(@Body song: Song): Call<Song>
}