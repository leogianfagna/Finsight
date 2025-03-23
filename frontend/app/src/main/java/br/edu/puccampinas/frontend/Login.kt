package br.edu.puccampinas.frontend

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import br.edu.puccampinas.frontend.databinding.ActivityLoginBinding
import br.edu.puccampinas.frontend.network.RetrofitClient
import br.edu.puccampinas.frontend.network.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    // Variável de binding para vincular os elementos de layout ao código
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // Inicializa o binding com o layout
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {
            navegarTelaCadastro()
        }

        binding.btnEntrar.setOnClickListener {
            val email = binding.emailLogin.text.toString() // Obtém o texto do campo de email
            val senha = binding.passwordLogin.text.toString() // Obtém o texto do campo de senha

            when{
                email.isEmpty() -> {
                    binding.emailLogin.error = "Preencha o E-mail!"
                }
                senha.isEmpty() -> {
                    binding.passwordLogin.error = "Digite sua senha!"
                }
                !email.contains("@gmail.com") -> {
                    val snackbar = Snackbar.make(it, "E-mail inválido!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                senha.length <= 5 -> {
                    val snackbar = Snackbar.make(it, "A senha precisa ter no mínimo 6 caracteres!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                else -> {
                    login(it)
                }
            }
        }
    }

    private fun login(view: View) {
        val progressBar = binding.progessBar
        progressBar.visibility = View.VISIBLE
        binding.btnEntrar.isEnabled = false

        // Fazendo a requisição para buscar os usuários
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<List<UserResponse>> {
            override fun onResponse(call: Call<List<UserResponse>>, response: Response<List<UserResponse>>) {
                progressBar.visibility = View.GONE
                binding.btnEntrar.isEnabled = true

                if (response.isSuccessful) {
                    val userList = response.body()
                    val email = binding.emailLogin.text.toString()
                    val senha = binding.passwordLogin.text.toString()

                    val userExists = userList?.any { it.username == email && it.password == senha } ?: false

                    if (userExists) {
                        val snackbar = Snackbar.make(view, "Login efetuado com sucesso!", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                        navegarTelaPrincipal()
                    } else {
                        val snackbar = Snackbar.make(view, "E-mail ou senha incorretos!", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
                } else {
                    val snackbar = Snackbar.make(view, "Erro ao conectar ao servidor!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }

            override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                progressBar.visibility = View.GONE
                binding.btnEntrar.isEnabled = true
                val snackbar = Snackbar.make(view, "Erro: ${t.message}", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })
    }

    private fun navegarTelaPrincipal(){
        val intent = Intent(this, Create_account::class.java)
        startActivity(intent)
    }

    private fun navegarTelaCadastro(){
        val intent = Intent(this, Create_account::class.java)
        startActivity(intent)
    }
}
