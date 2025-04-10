package br.edu.puccampinas.frontend

import Calendario
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityMenuPrincipalBinding
import br.edu.puccampinas.frontend.model.FullNameResponse
import br.edu.puccampinas.frontend.model.UserResponse
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

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            getFullName(userId) { fullName ->
                if (fullName != null) {
                    binding.icLogin.text = fullName
                } else {
                    binding.icLogin.text = "Usuário"
                }
            }
        }

        binding.Oportunidades.setOnClickListener {
            navegarTelaOportunidades()
        }

        binding.Avaliar.setOnClickListener {
            navegarTelaAvaliar()
        }

        binding.Sugestoes.setOnClickListener {
            navegarTelaSugestoes()
        }

        binding.btnGraph.setOnClickListener {
            navegarTelaCalendario()
        }
    }

    private fun getFullName(userId: String, callback: (String?) -> Unit) {
        RetrofitClient.instance.getFullNameById(userId).enqueue(object : Callback<FullNameResponse> {
            override fun onResponse(call: Call<FullNameResponse>, response: Response<FullNameResponse>) {
                if (response.isSuccessful) {
                    val fullName = response.body()?.full_name
                    callback(fullName)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<FullNameResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun navegarTelaOportunidades(){
        val intent = Intent(this, Oportunidades::class.java)
        startActivity(intent)
    }

    private fun navegarTelaAvaliar(){
        val intent = Intent(this, Avaliar::class.java)
        startActivity(intent)
    }

    private fun navegarTelaSugestoes(){
        val intent = Intent(this, Sugestoes::class.java)
        startActivity(intent)
    }

    private fun navegarTelaCalendario(){
        val intent = Intent(this, Calendario::class.java)
        startActivity(intent)
    }

}
