package br.edu.puccampinas.frontend

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.model.AcaoTicker
import br.edu.puccampinas.frontend.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AvaliarAcoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliar_acoes)

        val btnAdicionar = findViewById<Button>(R.id.btnAvaliar)
        val spinnerAcoes = findViewById<Spinner>(R.id.spinnerAcoes)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val comeBack = findViewById<TextView>(R.id.come_back)

        comeBack.setOnClickListener {
            comeBack()
        }

        btnAdicionar.setOnClickListener {
            RetrofitClient.instance.getAcoes().enqueue(object : Callback<List<AcaoTicker>> {
                override fun onResponse(call: Call<List<AcaoTicker>>, response: Response<List<AcaoTicker>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val acoes = response.body()!!
                        val listaComSelecione = mutableListOf("Selecione uma ação")
                        val acoesMap = mutableMapOf<String, String>()  // ticker -> data_com

                        for (acao in acoes) {
                            listaComSelecione.add(acao.ticker)
                            acoesMap[acao.ticker] = acao.data_com
                        }

                        val adapter = ArrayAdapter(this@AvaliarAcoes, android.R.layout.simple_spinner_dropdown_item, listaComSelecione)
                        spinnerAcoes.adapter = adapter
                        spinnerAcoes.visibility = View.VISIBLE

                        btnConfirmar.setOnClickListener {
                            val acaoEscolhida = spinnerAcoes.selectedItem.toString()
                            val dataCom = acoesMap[acaoEscolhida] ?: ""

                            if (dataCom.isNotEmpty()) {
                                val imageViewGrafico = findViewById<ImageView>(R.id.imageViewGrafico)

                                RetrofitClient.instance.gerarGrafico(acaoEscolhida, dataCom)
                                    .enqueue(object : Callback<ResponseBody> {
                                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                            if (response.isSuccessful && response.body() != null) {
                                                val inputStream = response.body()!!.byteStream()
                                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                                imageViewGrafico.visibility = View.VISIBLE
                                                imageViewGrafico.setImageBitmap(bitmap)
                                            } else {
                                                Toast.makeText(this@AvaliarAcoes, "Erro ao gerar gráfico", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                            Toast.makeText(this@AvaliarAcoes, "Falha: ${t.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            } else {
                                Toast.makeText(this@AvaliarAcoes, "Data COM não encontrada", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(this@AvaliarAcoes, "Erro ao carregar ações", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<AcaoTicker>>, t: Throwable) {
                    Toast.makeText(this@AvaliarAcoes, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

            spinnerAcoes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    btnConfirmar.visibility = if (position != 0) View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            btnConfirmar.setOnClickListener {
                val acaoEscolhida = spinnerAcoes.selectedItem.toString()
                val imageViewGrafico = findViewById<ImageView>(R.id.imageViewGrafico)

                val dataCom = "2025-06-01"

                RetrofitClient.instance.gerarGrafico(acaoEscolhida, dataCom)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val inputStream = response.body()!!.byteStream()
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                imageViewGrafico.visibility = View.VISIBLE
                                imageViewGrafico.setImageBitmap(bitmap)
                            } else {
                                Toast.makeText(
                                    this@AvaliarAcoes,
                                    "Erro ao gerar gráfico",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(
                                this@AvaliarAcoes,
                                "Falha: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

            }
        }
    }

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}
