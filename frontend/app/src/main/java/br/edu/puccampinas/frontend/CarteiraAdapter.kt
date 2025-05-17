package br.edu.puccampinas.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.frontend.model.Acao

class CarteiraAdapter(private val listaAcoes: List<Acao>, private val onDeleteClick:(Acao)->Unit) :
    RecyclerView.Adapter<CarteiraAdapter.AcaoViewHolder>() {

    class AcaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticker: TextView = itemView.findViewById(R.id.textTicker)
        val precoAtual: TextView = itemView.findViewById(R.id.textPrecoAtual)
        val quantidade: TextView = itemView.findViewById(R.id.textQuantidade)
        val data: TextView = itemView.findViewById(R.id.data)
        val precoFuturo: TextView = itemView.findViewById(R.id.precoFuturo)
        val btnDelete: View = itemView.findViewById(R.id.excluir)
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
        holder.quantidade.text = "Quantidade: ${acao.quantidade}"
        holder.data.text = acao.data
        holder.precoFuturo.text = acao.precoFuturo

        holder.btnDelete.setOnClickListener {
            onDeleteClick(acao)
        }
    }

    override fun getItemCount(): Int = listaAcoes.size
}
