package br.edu.puccampinas.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarteiraAdapter(private val listaAcoes: List<Acao>) :
    RecyclerView.Adapter<CarteiraAdapter.AcaoViewHolder>() {

    class AcaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticker: TextView = itemView.findViewById(R.id.textTicker)
        val precoAtual: TextView = itemView.findViewById(R.id.textPrecoAtual)
        val precoMedio: TextView = itemView.findViewById(R.id.textPrecoMedio)
        val quantidade: TextView = itemView.findViewById(R.id.textQuantidade)
        val ganho: TextView = itemView.findViewById(R.id.textGanho)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_acao, parent, false)
        return AcaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcaoViewHolder, position: Int) {
        val acao = listaAcoes[position]
        holder.ticker.text = acao.ticker
        holder.precoAtual.text = acao.precoAtual
        holder.precoMedio.text = "Preço médio: ${acao.precoMedio}"
        holder.quantidade.text = "Quantidade: ${acao.quantidade}"
        holder.ganho.text = acao.ganho
    }

    override fun getItemCount(): Int = listaAcoes.size
}
