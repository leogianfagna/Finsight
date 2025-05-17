package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityMenuPrincipalBinding
import br.edu.puccampinas.frontend.model.AcaoTicker
import br.edu.puccampinas.frontend.model.BalanceResponse
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.FutureBalanceResponse
import br.edu.puccampinas.frontend.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class MenuPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding

    private var notficacoesAtivadas = false
    private var nomeUsuario: String? = null
    private val mapaAcoesPorData = mutableMapOf<String, MutableList<String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNotifications.setOnClickListener{
            notficacoesAtivadas = !notficacoesAtivadas

            if(notficacoesAtivadas){
                Toast.makeText(this,"Notificações ativadas", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this,"Notificações desativadas",Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSettings.setOnClickListener { navegarTelaConfig() }

        // Nome do usuário
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            updateUserBalance(userId) {
                getBalance(userId) { balance ->
                    val balance = balance ?: "R$00,00"
                    binding.Saldo.text = "R$"+balance
                }
            }
            updateUserFutureBalance(userId) {
                getFutureBalance(userId) { future_balance ->
                    val future_balance = future_balance ?: "R$00,00"
                    binding.SaldoFuturo.text = "R$" + future_balance
                }
            }
            getFullName(userId) { fullName ->
                val nome = fullName ?: "Usuário"
                binding.icUser.text = nome
                nomeUsuario = nome
            }
        }

        carregarAcoesDoBackend()

        // Ações dos botões principais
        binding.Sugestoes.setOnClickListener { navegarTelaSugestoes() }
        binding.Avaliar.setOnClickListener { navegarTelaAvaliar() }
        binding.btnGraph.setOnClickListener { navegarTelaCalendario() }
        binding.btnWallet.setOnClickListener { navegarTelaCarteira() }
    }

    private fun updateUserBalance(id: String, callback: () -> Unit) {
        RetrofitClient.instance.updateBalance(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback()
                } else {
                    Toast.makeText(this@MenuPrincipal, "Erro ao atualizar saldo", Toast.LENGTH_SHORT).show()
                    callback()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MenuPrincipal, "Erro de conexão ao atualizar saldo", Toast.LENGTH_SHORT).show()
                callback()
            }
        })
    }

    private fun updateUserFutureBalance(id: String, callback: () -> Unit) {
        RetrofitClient.instance.updateFutureBalance(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback()
                } else {
                    Toast.makeText(this@MenuPrincipal, "Erro ao atualizar saldo futuro", Toast.LENGTH_SHORT).show()
                    callback()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MenuPrincipal, "Erro de conexão ao atualizar saldo", Toast.LENGTH_SHORT).show()
                callback()
            }
        })
    }


    private fun getFullName(userId: String, callback: (String?) -> Unit) {
        RetrofitClient.instance.getFullNameById(userId).enqueue(object : Callback<FullNameResponse> {
            override fun onResponse(call: Call<FullNameResponse>, response: Response<FullNameResponse>) {
                val fullName = response.body()?.full_name
                callback(fullName)
            }

            override fun onFailure(call: Call<FullNameResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun getBalance(userId: String, callback: (String?) -> Unit) {
        RetrofitClient.instance.getBalanceById(userId).enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                val balance = response.body()?.balance
                callback(balance)
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun getFutureBalance(userId: String, callback: (String?) -> Unit) {
        RetrofitClient.instance.getFutureBalanceById(userId).enqueue(object : Callback<FutureBalanceResponse> {
            override fun onResponse(call: Call<FutureBalanceResponse>, response: Response<FutureBalanceResponse>) {
                val future_balance = response.body()?.future_balance
                callback(future_balance)
            }

            override fun onFailure(call: Call<FutureBalanceResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun carregarAcoesDoBackend() {
        RetrofitClient.instance.getAcoes().enqueue(object : Callback<List<AcaoTicker>> {
            override fun onResponse(call: Call<List<AcaoTicker>>, response: Response<List<AcaoTicker>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaAcoes = response.body()!!
                    val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                    for (acao in listaAcoes) {
                        val dataFormatada = try {
                            formatoEntrada.parse(acao.data_com)?.let {
                                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it)
                            }
                        } catch (e: Exception) {
                            null
                        }

                        dataFormatada?.let {
                            if (!mapaAcoesPorData.containsKey(it)) {
                                mapaAcoesPorData[it] = mutableListOf()
                            }
                            mapaAcoesPorData[it]!!.add(acao.ticker)
                        }
                    }
                    Log.d("Calendario", "mapaAcoesPorData keys: ${mapaAcoesPorData.keys}")

                    exibirDataMaisRecente()

                } else {
                    Toast.makeText(this@MenuPrincipal, "Erro ao carregar ações", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AcaoTicker>>, t: Throwable) {
                Toast.makeText(this@MenuPrincipal, "Falha de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun exibirDataMaisRecente() {
        val formatterEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatterSaida = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

        val diasOrdenados = mapaAcoesPorData.toSortedMap(compareBy { it })

        val dataMaisRecente = diasOrdenados.keys.firstOrNull()

        if (dataMaisRecente != null) {
            val dataFormatada = try {
                formatterEntrada.parse(dataMaisRecente)?.let { date ->
                    formatterSaida.format(date)
                } ?: dataMaisRecente
            } catch (e: Exception) {
                "Erro ao formatar"
            }

            val tickers = diasOrdenados[dataMaisRecente]?.joinToString(", ") ?: "Sem ações"

            binding.NomeEmpesa.text = "$dataFormatada: $tickers"
        } else {
            binding.NomeEmpesa.text = "Sem dados"
        }
    }

    private fun navegarTelaSugestoes() {
        startActivity(Intent(this, Sugestoes::class.java))
    }

    private fun navegarTelaAvaliar() {
        startActivity(Intent(this, AvaliarAcoes::class.java))
    }

    private fun navegarTelaCalendario() {
        startActivity(Intent(this, Calendario::class.java))
    }

    private fun navegarTelaCarteira() {
        startActivity(Intent(this, Carteira::class.java))
    }

    private fun navegarTelaConfig() {
        val intent = Intent(this, Configuracao::class.java)
        intent.putExtra("NOME_USUARIO", nomeUsuario)
        startActivity(intent)
    }
}
