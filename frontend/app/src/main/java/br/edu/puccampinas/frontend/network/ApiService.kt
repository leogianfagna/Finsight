package br.edu.puccampinas.frontend.network

import retrofit2.Call
import retrofit2.http.GET

data class UserResponse(
    val username: String,
    val password: String
)

interface ApiService {
    @GET("get_all_users/")
    fun getAllUsers(): Call<List<UserResponse>>
}
