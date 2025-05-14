package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.frontend.model.ResponseMessage
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

        adapter = CarteiraAdapter(listaDeAcoes){acao->showPopup(acao)}
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
                                precoAtual= "R$"+item[1].toString(),
                                quantidade = item[2].toString(),
                                data = item[3].toString(),
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

    private fun showPopup(acao: Acao) {
        val dialogView = layoutInflater.inflate(R.layout.pop_up, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        builder.setCancelable(true)

        val dialog = builder.create()

        dialog.setOnCancelListener {
            dialog.dismiss()
        }

        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClose)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            dialog.dismiss()

            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null)

            if (userId != null) {
                getUserName(userId) { username ->
                    if (username != null) {
                        deleteTicker(username, acao)  // Passando o objeto completo 'acao'
                    }
                }
            }
        }

        dialog.show()
    }


    private fun deleteTicker(username: String, acao: Acao) {
        // Remove o "R$" e converte para Double
        val precoAtualSemSimbolo = acao.precoAtual.replace("R$", "").trim()

        // Converte a quantidade para Double ou Float, caso seja decimal
        val quantidadeDecimal = acao.quantidade.toDouble()

        RetrofitClient.instance.deleteUserTicker(username, acao.ticker, precoAtualSemSimbolo.toDouble(), quantidadeDecimal, acao.data)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                    if (response.isSuccessful) {
                        // Verifica a mensagem de sucesso
                        val responseMessage = response.body()
                        if (responseMessage != null && responseMessage.message == "Ticker removed successfully") {
                            // Atualiza a tela ou remove da lista local
                            listaDeAcoes.remove(acao) // Remove a ação da lista
                            adapter.notifyDataSetChanged() // Notifica o adapter para atualizar a RecyclerView
                        }
                    } else {
                        Log.e("API", "Erro ao deletar: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Log.e("API", "Falha na requisição", t)
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
