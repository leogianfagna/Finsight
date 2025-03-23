package br.edu.puccampinas.frontend.model

data class RegisterRequest(
    val full_name: String,
    val username: String,
    val password: String,
    val cpf: String
)
