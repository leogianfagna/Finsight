package br.edu.puccampinas.frontend.model

data class TickerResponse(
    val username: String,
    val tickers: List<List<Any>>
)
