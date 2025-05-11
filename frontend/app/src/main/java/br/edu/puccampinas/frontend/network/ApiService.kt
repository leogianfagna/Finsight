package br.edu.puccampinas.frontend.network

import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.RegisterRequest
import br.edu.puccampinas.frontend.model.RegisterResponse
import br.edu.puccampinas.frontend.model.TickerResponse
import br.edu.puccampinas.frontend.model.UserNameResponse
import br.edu.puccampinas.frontend.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("get_all_users/")
    fun getAllUsers(): Call<List<UserResponse>>

    @POST("add_user/")
    fun registerUser(@Body user: RegisterRequest): Call<RegisterResponse>

    @GET("get_full_name/")
    fun getFullNameById(@Query("id") userId: String): Call<FullNameResponse>

    @GET("get_user_tickers/")
    fun getUserTickers(@Query("username") username: String): Call<TickerResponse>

    @GET("get_username/")
    fun getUserNameById(@Query("id") userId: String): Call<UserNameResponse>
}
