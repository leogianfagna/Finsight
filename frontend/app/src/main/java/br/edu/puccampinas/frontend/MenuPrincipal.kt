package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityMenuPrincipalBinding
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ativar item inicial do menu
        ativarMenu("home")

        binding.btnWallet.setOnClickListener {
            ativarMenu("carteira")
            Toast.makeText(this, "Abrir Carteira (ainda não implementado)", Toast.LENGTH_SHORT).show()
        }

        binding.btnGraph.setOnClickListener {
            ativarMenu("calendario")
            navegarTelaCalendario()
        }

        // Clique no botão home
        binding.iconBg.setOnClickListener {
            ativarMenu("home")
        }

        // Clique no botão de gráfico (atalho pro calendário)
        binding.btnGraph.setOnClickListener {
            ativarMenu("calendario")
            navegarTelaCalendario()
        }

        // Nome do usuário
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            getFullName(userId) { fullName ->
                val nome = fullName ?: "Usuário"
                binding.icLogin.text = nome
            }
        }

        // Ações dos botões principais
        binding.Oportunidades.setOnClickListener {
            navegarTelaOportunidades()
        }

        binding.Avaliar.setOnClickListener {
            navegarTelaAvaliar()
        }

        binding.Sugestoes.setOnClickListener {
            navegarTelaSugestoes()
        }
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

    private fun ativarMenu(tela: String) {
        // Esconder todos os textos
        binding.textWallet.visibility = View.GONE
        binding.textCalendario.visibility = View.GONE
        binding.textHome.visibility = View.GONE

        // Resetar fundo dos botões (se quiser mudar visualmente também)
        binding.btnWallet.setBackgroundResource(R.drawable.bg_white_circle)
        binding.btnGraph.setBackgroundResource(R.drawable.bg_white_circle)
        binding.iconBg.setBackgroundResource(R.drawable.bg_white_circle)

        when (tela) {
            "home" -> {
                binding.textHome.visibility = View.VISIBLE
                binding.iconBg.setBackgroundResource(R.drawable.nav_selected)
            }

            "carteira" -> {
                binding.textWallet.visibility = View.VISIBLE
                binding.btnWallet.setBackgroundResource(R.drawable.nav_selected)
            }

            "calendario" -> {
                binding.textCalendario.visibility = View.VISIBLE
                binding.btnGraph.setBackgroundResource(R.drawable.nav_selected)
            }
        }
    }

    private fun navegarTelaOportunidades() {
        startActivity(Intent(this, Oportunidades::class.java))
    }

    private fun navegarTelaAvaliar() {
        startActivity(Intent(this, Avaliar::class.java))
    }

    private fun navegarTelaSugestoes() {
        startActivity(Intent(this, Sugestoes::class.java))
    }

    private fun navegarTelaCalendario() {
        startActivity(Intent(this, Calendario::class.java))
        overridePendingTransition(0, 0)
    }
}
