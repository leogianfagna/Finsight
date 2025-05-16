package br.edu.puccampinas.frontend

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdicionarAcao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_acao)

        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)
        val spinnerAcoes = findViewById<Spinner>(R.id.spinnerAcoes)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        // Mostra o spinner(dropdown) ao clicar no botão "Adicionar"
        btnAdicionar.setOnClickListener {
            spinnerAcoes.visibility = View.VISIBLE
        }

        // Nessa parte, pode puxar a rota e apagar isso aqui embaixo que está comentado, é so um exemplo
        // Simular lista de ações temporária
        //val acoesExemplo = listOf("Selecione uma ação", "PETR4", "VALE3", "ITUB3", "BBSE3")
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, acoesExemplo)
        //spinnerAcoes.adapter = adapter

        // Exibe o botão "Confirmar" apenas após uma ação válida ser escolhida
        spinnerAcoes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                btnConfirmar.visibility = if (position != 0) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnConfirmar.setOnClickListener {
            val acaoEscolhida = spinnerAcoes.selectedItem.toString()
            Toast.makeText(this, "Ação escolhida: $acaoEscolhida", Toast.LENGTH_SHORT).show()
        }
    }
}
