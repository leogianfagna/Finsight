package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

        // Define ação para o vetor "voltar"
        binding.comeBack.setOnClickListener {
            comeBack()
        }

        // Define ação para o botão "Criar conta"
        binding.btnCadastrarConta.setOnClickListener {
            register()
        }

        binding.cadastrarCpf.addTextChangedListener(CpfTextWatcher(binding.cadastrarCpf))
    }

    // Função para registrar o usuário após validar os dados
    // Validação do CPF corretamente
    private fun register() {
        val name = binding.nomeCompleto.text.toString()
        val email = binding.emailCadastrar.text.toString()
        val cpf = binding.cadastrarCpf.text.toString() // Obtém o CPF formatado
        val password = binding.passwordLogin.text.toString()
        val confirmPassword = binding.passwordConfirm.text.toString()

        // Remove qualquer formatação do CPF para validar corretamente
        val cpfNumerico = cpf.replace(Regex("[^\\d]"), "")

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

    // Validação correta do CPF
    private fun validarCPF(cpf: String): Boolean {
        val cpfNumerico = cpf.replace(Regex("[^\\d]"), "")

        if (cpfNumerico.length != 11 || cpfNumerico.all { it == cpfNumerico[0] }) return false

        val numeros = cpfNumerico.map { it.toString().toInt() }

        val peso1 = (10 downTo 2).toList()
        val digito1 = (numeros.take(9).zip(peso1).sumOf { it.first * it.second } * 10) % 11
        val primeiroDigitoVerificador = if (digito1 == 10) 0 else digito1

        val peso2 = (11 downTo 2).toList()
        val digito2 = (numeros.take(10).zip(peso2).sumOf { it.first * it.second } * 10) % 11
        val segundoDigitoVerificador = if (digito2 == 10) 0 else digito2

        return numeros[9] == primeiroDigitoVerificador && numeros[10] == segundoDigitoVerificador
    }

    // Formatação automática do CPF sem duplicação
    inner class CpfTextWatcher(private val editText: EditText) : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isUpdating || s.isNullOrEmpty()) return

            isUpdating = true

            // Remove tudo que não for número
            val cleanString = s.toString().replace(Regex("[^\\d]"), "")

            // Garante que tenha no máximo 11 caracteres numéricos
            val limitedString = cleanString.take(11)

            // Aplica a formatação XXX.XXX.XXX-XX
            val formattedCPF = StringBuilder()
            for (i in limitedString.indices) {
                formattedCPF.append(limitedString[i])
                if (i == 2 || i == 5) formattedCPF.append('.') // Adiciona "." nos lugares certos
                if (i == 8) formattedCPF.append('-') // Adiciona "-" no lugar certo
            }

            // Atualiza o EditText corretamente
            editText.removeTextChangedListener(this)
            editText.setText(formattedCPF)
            editText.setSelection(formattedCPF.length) // Move o cursor para o final
            editText.addTextChangedListener(this)

            isUpdating = false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
