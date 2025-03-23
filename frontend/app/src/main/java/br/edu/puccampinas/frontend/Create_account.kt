package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityCreateAccountBinding
import br.edu.puccampinas.frontend.model.RegisterRequest
import br.edu.puccampinas.frontend.model.RegisterResponse
import br.edu.puccampinas.frontend.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Create_account : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding para acessar os elementos da interface
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adiciona um listener para formatar o CPF conforme o usuário digita
        setupCPFListener()

        // Define ação para o vetor "voltar"
        binding.comeBack.setOnClickListener {
            comeBack()
        }

        // Define ação para o botão "Criar conta"
        binding.btnCadastrarConta.setOnClickListener {
            register()
        }
    }

    // Função para registrar o usuário após validar os dados
    private fun register() {
        val name = binding.nomeCompleto.text.toString()
        val email = binding.emailCadastrar.text.toString()
        val cpf = binding.cadastrarCpf.text.toString() // Obtém o CPF formatado
        val password = binding.passwordLogin.text.toString()
        val confirmPassword = binding.passwordCreateAccount.text.toString()

        // Remove a formatação do CPF para validação
        val cpfNumerico = cpf.replace(".", "").replace("-", "")

        // Validação do nome completo
        if (name.length < 5) {
            showToast("O nome completo deve ter pelo menos 5 caracteres!")
            return
        }

        // Validação do e-mail (deve terminar com @gmail.com)
        if (!email.endsWith("@gmail.com")) {
            showToast("O e-mail deve ser do domínio @gmail.com!")
            return
        }

        // Validação do CPF: verifica se tem 11 números e se é válido
        if (cpfNumerico.length != 11 || !validarCPF(cpfNumerico)) {
            showToast("CPF inválido!")
            return
        }

        // Validação da senha (mínimo 6 caracteres)
        if (password.length < 6 || confirmPassword.length < 6) {
            showToast("A senha deve ter pelo menos 6 caracteres!")
            return
        }

        // Verifica se as senhas coincidem
        if (password != confirmPassword) {
            showToast("As senhas não coincidem!")
            return
        }

        // Verifica se todos os campos estão preenchidos
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            showToast("Preencha todos os campos!")
            return
        }

        // Chama a API para registrar o usuário
        val userRequest = RegisterRequest(name, email, password, cpf)
        val progressBar = binding.progessBar
        progressBar.visibility = View.VISIBLE
        binding.btnCadastrarConta.isEnabled = false

        RetrofitClient.instance.registerUser(userRequest).enqueue(object :
            Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                progressBar.visibility = View.GONE
                binding.btnCadastrarConta.isEnabled = true

                if (response.isSuccessful) {
                    showToast(response.body()?.message ?: "Usuário registrado com sucesso!")
                    backToLogin()
                } else {
                    showToast("Erro ao registrar usuário!")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                binding.btnCadastrarConta.isEnabled = true
                showToast("Erro: ${t.message}")
            }
        })
    }

    // Exibe mensagens curtas na tela
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Adiciona um TextWatcher para formatar o CPF automaticamente
    private fun setupCPFListener() {
        val cpfEditText = binding.cadastrarCpf

        // Define o limite de caracteres do EditText para 14 (incluindo "." e "-")
        cpfEditText.filters = arrayOf(InputFilter.LengthFilter(14))

        cpfEditText.addTextChangedListener(object : TextWatcher {
            var isUpdating = false  // Evita chamadas recursivas desnecessárias

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating || s == null) return

                isUpdating = true

                // Remove pontos e traços para manter apenas os números
                val cleanString = s.filter { it.isDigit() }

                // Limita a string a 11 números
                val limitedString = if (cleanString.length > 11) cleanString.take(11) else cleanString

                // Aplica a formatação padrão do CPF (XXX.XXX.XXX-XX)
                val formattedCPF = when (limitedString.length) {
                    in 1..3 -> limitedString
                    in 4..6 -> "${limitedString.substring(0, 3)}.${limitedString.substring(3)}"
                    in 7..9 -> "${limitedString.substring(0, 3)}.${limitedString.substring(3, 6)}.${limitedString.substring(6)}"
                    in 10..11 -> "${limitedString.substring(0, 3)}.${limitedString.substring(3, 6)}.${limitedString.substring(6, 9)}-${limitedString.substring(9)}"
                    else -> limitedString
                }

                // Define o texto formatado no EditText e move o cursor para o final
                cpfEditText.setText(formattedCPF)
                cpfEditText.setSelection(formattedCPF.length)

                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 14) { // 14 caracteres é o tamanho correto do CPF formatado
                    val cpfSemFormatacao = s.toString().replace(".", "").replace("-", "")

                    // Se o CPF for inválido, exibe um erro
                    if (!validarCPF(cpfSemFormatacao)) {
                        cpfEditText.error = "CPF inválido!"
                        showToast("CPF inválido!")
                    }
                }
            }
        })
    }

    // Função que valida se um CPF é válido de acordo com o algoritmo oficial
    private fun validarCPF(cpf: String): Boolean {
        // Remove qualquer caractere não numérico
        val cpfNumerico = cpf.filter { it.isDigit() }

        // CPF deve ter 11 números e não pode ser uma sequência repetida (ex: 11111111111)
        if (cpfNumerico.length != 11 || cpfNumerico.all { it == cpfNumerico[0] }) return false

        // Converte os números do CPF para uma lista de inteiros
        val numeros = cpfNumerico.map { it.toString().toInt() }

        // Cálculo do primeiro dígito verificador
        val peso1 = (10 downTo 2).toList()
        val digito1 = (numeros.take(9).zip(peso1).sumOf { it.first * it.second } * 10) % 11
        val primeiroDigitoVerificador = if (digito1 == 10) 0 else digito1

        // Cálculo do segundo dígito verificador
        val peso2 = (11 downTo 2).toList()
        val digito2 = (numeros.take(10).zip(peso2).sumOf { it.first * it.second } * 10) % 11
        val segundoDigitoVerificador = if (digito2 == 10) 0 else digito2

        // Retorna verdadeiro se os dígitos calculados forem iguais aos informados
        return numeros[9] == primeiroDigitoVerificador && numeros[10] == segundoDigitoVerificador
    }

    // Função para retornar à tela de início, caso precione o vetor "voltar"
    private fun comeBack() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
    // Função para retornar à tela de início, quando verificar se todos os campos estão válidos
    private fun backToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
