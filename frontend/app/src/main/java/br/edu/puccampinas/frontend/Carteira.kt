package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.TickerResponse
import br.edu.puccampinas.frontend.model.UserNameResponse
import br.edu.puccampinas.frontend.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Carteira : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CarteiraAdapter
    private val listaDeAcoes = mutableListOf<Acao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carteira)

        recyclerView = findViewById(R.id.recyclerViewCarteira)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CarteiraAdapter(listaDeAcoes)
        recyclerView.adapter = adapter

        val comeBackTextView = findViewById<TextView>(R.id.come_back)
        comeBackTextView.setOnClickListener { comeBack() }

        val graphButton = findViewById<ImageButton>(R.id.btn_graph)
        graphButton.setOnClickListener { navegarTelaCalendario() }

        val menuButton = findViewById<ImageView>(R.id.btn_home)
        menuButton.setOnClickListener { navegarMenuPrincipal() }

        // Nome do usuário
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            getUserName(userId) { username ->
                if (username != null) {
                    fetchAcoesDoUsuario(username)
                }
            }
        }
    }

    private fun getUserName(userId: String, callback: (String?) -> Unit) {
        RetrofitClient.instance.getUserNameById(userId).enqueue(object : Callback<UserNameResponse> {
            override fun onResponse(call: Call<UserNameResponse>, response: Response<UserNameResponse>) {
                val username = response.body()?.username
                callback(username)
            }

            override fun onFailure(call: Call<UserNameResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun fetchAcoesDoUsuario(username: String) {
        val retrofit = RetrofitClient.instance
        val call = retrofit.getUserTickers(username)

        call.enqueue(object : Callback<TickerResponse> {
            override fun onResponse(call: Call<TickerResponse>, response: Response<TickerResponse>) {
                if (response.isSuccessful) {
                    response.body()?.tickers?.forEach { item ->
                        if (item.size >= 4) {
                            val acao = Acao(
                                ticker = item[0].toString(),
                                precoMedio = item[1].toString(),
                                quantidade = item[2].toString(),
                                ganho = item[3].toString(),
                                precoAtual = "R$ XX,XX"
                            )
                            listaDeAcoes.add(acao)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    // Tratar resposta não bem-sucedida
                    Log.e("API", "Erro: ${response.code()} - ${response.message()}")
                }
            }


            override fun onFailure(call: Call<TickerResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

    private fun navegarTelaCalendario() {
        startActivity(Intent(this, Calendario::class.java))
    }

    private fun navegarMenuPrincipal() {
        startActivity(Intent(this, MenuPrincipal::class.java))
    }
}
