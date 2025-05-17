package br.edu.puccampinas.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.frontend.model.AcaoSugestao

class SugestoesAdapter(private val listaAcoes: List<AcaoSugestao>) :
    RecyclerView.Adapter<SugestoesAdapter.AcaoSugestaoViewHolder>() {

    class AcaoSugestaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticker: TextView = itemView.findViewById(R.id.ticker)
        val preco_atual: TextView = itemView.findViewById(R.id.preco_atual)
        val data_com: TextView = itemView.findViewById(R.id.data_com)
        val ultimo_preco: TextView = itemView.findViewById(R.id.ultimo_preco)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoSugestaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sugestao, parent, false)
        return AcaoSugestaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcaoSugestaoViewHolder, position: Int) {
        val acao = listaAcoes[position]
        holder.ticker.text = acao.ticker
        holder.preco_atual.text = "R$${acao.preco_atual}"
        holder.data_com.text = "Data COM: ${acao.data_com}"
        holder.ultimo_preco.text = "R$${acao.ultimo_preco}"
    }

    override fun getItemCount(): Int = listaAcoes.size
}
