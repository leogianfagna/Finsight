package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.frontend.databinding.ActivityConfiguracaoBinding
import br.edu.puccampinas.frontend.databinding.ActivityMenuPrincipalBinding


class Configuracao : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfiguracaoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nomeUsuario = intent.getStringExtra("NOME_USUARIO")
        binding.icUser.text = nomeUsuario ?: "Usuário"

        binding.comeBack.setOnClickListener {
            comeBack()
        }

        binding.Sair.setOnClickListener {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

}
