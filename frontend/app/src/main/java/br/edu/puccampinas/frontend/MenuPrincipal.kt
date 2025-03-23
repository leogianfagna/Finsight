package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityMenuPrincipalBinding

class MenuPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Oportunidades.setOnClickListener {
            navegarTelaOportunidades()
        }

        binding.Avaliar.setOnClickListener {
            navegarTelaAvaliar()
        }
    }

    private fun navegarTelaOportunidades(){
        val intent = Intent(this, Oportunidades::class.java)
        startActivity(intent)
    }

    private fun navegarTelaAvaliar(){
        val intent = Intent(this, Avaliar::class.java)
        startActivity(intent)
    }

}
