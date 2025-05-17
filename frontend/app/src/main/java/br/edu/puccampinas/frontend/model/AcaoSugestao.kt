package br.edu.puccampinas.frontend.model

data class AcaoSugestao(
    val ticker: String,
    val data_com: String,
    val ultimo_preco: String,
    val preco_atual: String
)
