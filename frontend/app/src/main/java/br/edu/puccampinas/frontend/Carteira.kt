package br.edu.puccampinas.frontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Carteira : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CarteiraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carteira)

        recyclerView = findViewById(R.id.recyclerViewCarteira)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val listaDeAcoes = mutableListOf<Acao>()
        adapter = CarteiraAdapter(listaDeAcoes)
        recyclerView.adapter = adapter
        //Isso vai ter que ser mudado
    }
}


