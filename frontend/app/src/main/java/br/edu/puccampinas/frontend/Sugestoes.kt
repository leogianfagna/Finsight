package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.frontend.databinding.ActivitySugestoesBinding

private lateinit var binding: ActivitySugestoesBinding

class Sugestoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivitySugestoesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.comeBack.setOnClickListener {
            comeBack()
        }
    }

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}
