package br.edu.puccampinas.frontend.network

import br.edu.puccampinas.frontend.model.AcaoTicker
import br.edu.puccampinas.frontend.model.AddTickerRequest
import br.edu.puccampinas.frontend.model.BalanceResponse
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.FutureBalanceResponse
import br.edu.puccampinas.frontend.model.GraficoResponse
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

    @GET("get_balance/")
    fun getBalanceById(@Query("id") userId: String): Call<BalanceResponse>

    @GET("get_future_balance/")
    fun getFutureBalanceById(@Query("id") userId: String): Call<FutureBalanceResponse>

    @GET("get_account_balance/")
    fun updateBalance(@Query("id") id: String): Call<Void>

    @GET("delete_user_ticker/")
    fun deleteUserTicker(
        @Query("username") username: String,
        @Query("ticker") ticker: String,
        @Query("price") price: Double,
        @Query("quantity") quantity: Double,
        @Query("date") date: String
    ): Call<ResponseMessage>

    @GET("add_user_ticker/")
    fun addUserTicker(
        @Query("username") username: String,
        @Query("ticker") ticker: String,
        @Query("purchase_quantity") purchaseQuantity: Int
    ): Call<ResponseMessage>

    @GET("grafico/")
    fun gerarGrafico(
        @Query("ticker") ticker: String,
        @Query("data_com") dataCom: String
    ): Call<ResponseBody>

    @GET("get_acoes/")
    fun getAcoes(): Call<List<AcaoTicker>>
}
