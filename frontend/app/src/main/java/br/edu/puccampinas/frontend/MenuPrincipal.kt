package br.edu.puccampinas.frontend

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.view.View
import android.widget.LinearLayout
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

        // Ativar item inicial do menu
        ativarMenu("home")

        binding.btnNotifications.setOnClickListener{
            notficacoesAtivadas = !notficacoesAtivadas

            if(notficacoesAtivadas){
                Toast.makeText(this,"Notificações ativadas", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this,"Notificações desativadas",Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSettings.setOnClickListener { navegarTelaConfig() }

        binding.btnWallet.setOnClickListener {
            if (botaoSelecionado != "carteira") {
                ativarMenu("carteira")
                startActivity(Intent(this, Carteira::class.java))
                overridePendingTransition(0, 0) // opcional: remove animação de transição
            }
        }

        binding.btnGraph.setOnClickListener {
            if (botaoSelecionado != "calendario") {
                ativarMenu("calendario")
                navegarTelaCalendario()
            }
        }

        binding.btnHome.setOnClickListener {
            if (botaoSelecionado != "home") {
                ativarMenu("home")
            }
        }

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

    private fun ativarMenu(tela: String) {
        // Resetar visuais
        binding.btnWallet.setBackgroundResource(R.drawable.bg_white_circle)
        binding.btnGraph.setBackgroundResource(R.drawable.bg_white_circle)
        binding.btnHome.setBackgroundResource(R.drawable.bg_white_circle)

        // Oculta todos os textos
        binding.textWallet.visibility = View.GONE
        binding.textCalendario.visibility = View.GONE
        binding.textHome.visibility = View.GONE

        // Resetar pesos
        val layoutHome = binding.btnHome.parent as? LinearLayout
        val layoutWallet = binding.btnWallet.parent as? LinearLayout
        val layoutGraph = binding.btnGraph.parent as? LinearLayout

        layoutHome?.layoutParams = (layoutHome?.layoutParams as? LinearLayout.LayoutParams)?.apply { weight = 1f }
        layoutWallet?.layoutParams = (layoutWallet?.layoutParams as? LinearLayout.LayoutParams)?.apply { weight = 1f }
        layoutGraph?.layoutParams = (layoutGraph?.layoutParams as? LinearLayout.LayoutParams)?.apply { weight = 1f }

        // Ativar botão central
        when (tela) {
            "home" -> {
                binding.btnHome.setBackgroundResource(R.drawable.nav_selected)
                binding.textHome.visibility = View.VISIBLE
                layoutHome?.layoutParams = (layoutHome?.layoutParams as LinearLayout.LayoutParams).apply { weight = 2f }
                layoutWallet?.layoutParams = (layoutWallet?.layoutParams as LinearLayout.LayoutParams).apply { weight = 1f }
                layoutGraph?.layoutParams = (layoutGraph?.layoutParams as LinearLayout.LayoutParams).apply { weight = 1f }
            }
            "carteira" -> {
                binding.btnWallet.setBackgroundResource(R.drawable.nav_selected)
                binding.textWallet.visibility = View.VISIBLE
                layoutWallet?.layoutParams = (layoutWallet?.layoutParams as LinearLayout.LayoutParams).apply { weight = 2f }
                layoutHome?.layoutParams = (layoutHome?.layoutParams as LinearLayout.LayoutParams).apply { weight = 1f }
                layoutGraph?.layoutParams = (layoutGraph?.layoutParams as LinearLayout.LayoutParams).apply { weight = 1f }
            }
            "calendario" -> {
                binding.btnGraph.setBackgroundResource(R.drawable.nav_selected)
                binding.textCalendario.visibility = View.VISIBLE
                layoutGraph?.layoutParams = (layoutGraph?.layoutParams as LinearLayout.LayoutParams).apply { weight = 2f }
                layoutHome?.layoutParams = (layoutHome?.layoutParams as LinearLayout.LayoutParams).apply { weight = 1f }
                layoutWallet?.layoutParams = (layoutWallet?.layoutParams as LinearLayout.LayoutParams).apply { weight = 1f }
            }
        }

        botaoSelecionado = tela
        binding.menuInferior.requestLayout() // força atualização visual
    }


    private fun navegarTelaOportunidades() {
        startActivity(Intent(this, Oportunidades::class.java))
    }

    private fun navegarTelaAvaliar() {
        startActivity(Intent(this, Avaliar::class.java))
    }

    private fun navegarTelaCalendario() {
        startActivity(Intent(this, Calendario::class.java))
        overridePendingTransition(0, 0)
    }
    private fun navegarTelaConfig() {
        val intent = Intent(this, Configuracao::class.java)
        intent.putExtra("NOME_USUARIO", nomeUsuario)
        startActivity(intent)
    }
}
