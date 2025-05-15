package br.edu.puccampinas.frontend.network

import br.edu.puccampinas.frontend.model.AddTickerRequest
import br.edu.puccampinas.frontend.model.BalanceResponse
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.FutureBalanceResponse
import br.edu.puccampinas.frontend.model.RegisterRequest
import br.edu.puccampinas.frontend.model.RegisterResponse
import br.edu.puccampinas.frontend.model.ResponseMessage
import br.edu.puccampinas.frontend.model.TickerResponse
import br.edu.puccampinas.frontend.model.UserNameResponse
import br.edu.puccampinas.frontend.model.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("api/get_all_users/")
    fun getAllUsers(): Call<List<UserResponse>>

    @POST("api/add_user/")
    fun registerUser(@Body user: RegisterRequest): Call<RegisterResponse>

    @GET("api/get_full_name/")
    fun getFullNameById(@Query("id") userId: String): Call<FullNameResponse>

    @GET("api/get_user_tickers/")
    fun getUserTickers(@Query("username") username: String): Call<TickerResponse>

    @GET("api/get_username/")
    fun getUserNameById(@Query("id") userId: String): Call<UserNameResponse>

    @GET("api/get_balance/")
    fun getBalanceById(@Query("id") userId: String): Call<BalanceResponse>

    @GET("api/get_future_balance/")
    fun getFutureBalanceById(@Query("id") userId: String): Call<FutureBalanceResponse>

    @GET("api/get_account_balance/")
    fun updateBalance(@Query("id") id: String): Call<Void>

    @GET("api/delete_user_ticker/")
    fun deleteUserTicker(
        @Query("username") username: String,
        @Query("ticker") ticker: String,
        @Query("price") price: Double,
        @Query("quantity") quantity: Double,
        @Query("date") date: String
    ): Call<ResponseMessage>

    @POST("add_user_ticker/")
    fun addUserTicker(@Body request: AddTickerRequest): Call<ResponseMessage>

}
