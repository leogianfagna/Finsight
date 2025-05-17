package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.frontend.databinding.ActivitySugestoesBinding
import br.edu.puccampinas.frontend.model.Acao
import br.edu.puccampinas.frontend.model.AcaoSugestao
import br.edu.puccampinas.frontend.network.RetrofitClient

private lateinit var binding: ActivitySugestoesBinding

class Sugestoes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SugestoesAdapter
    private val listaDeAcoes = mutableListOf<AcaoSugestao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivitySugestoesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        recyclerView = binding.recyclerViewSugestoes
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SugestoesAdapter(listaDeAcoes)
        recyclerView.adapter = adapter

        binding.comeBack.setOnClickListener {
            comeBack()
        }

        buscarAcoesValorizadas()

    }

    private fun buscarAcoesValorizadas() {
        val call = RetrofitClient.instance.getAcoesValorizadas()

        call.enqueue(object : retrofit2.Callback<List<AcaoSugestao>> {
            override fun onResponse(
                call: retrofit2.Call<List<AcaoSugestao>>,
                response: retrofit2.Response<List<AcaoSugestao>>
            ) {
                if (response.isSuccessful) {
                    val acoes = response.body()
                    if (acoes != null) {
                        listaDeAcoes.clear()
                        listaDeAcoes.addAll(acoes)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<AcaoSugestao>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}
