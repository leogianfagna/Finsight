package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityMenuPrincipalBinding
import br.edu.puccampinas.frontend.model.BalanceResponse
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.FutureBalanceResponse
import br.edu.puccampinas.frontend.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding

    // Controle do estado do menu
    private var botaoSelecionado: String = "home"
    private var notficacoesAtivadas = false
    private var nomeUsuario: String? = null

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

                getFutureBalance(userId) { future_balance ->
                    val future_balance = future_balance ?: "R$00,00"
                    binding.SaldoFuturo.text = "R$"+future_balance
                }
            }

            getFullName(userId) { fullName ->
                val nome = fullName ?: "Usuário"
                binding.icUser.text = nome
                nomeUsuario = nome
            }
        }

        // Ações dos botões principais
        binding.Oportunidades.setOnClickListener { navegarTelaOportunidades() }
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


    private fun navegarTelaOportunidades() {
        startActivity(Intent(this, Oportunidades::class.java))
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
