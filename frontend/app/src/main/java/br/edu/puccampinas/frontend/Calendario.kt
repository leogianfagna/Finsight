package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityCalendarioBinding
import br.edu.puccampinas.frontend.model.AcaoTicker
import br.edu.puccampinas.frontend.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class Calendario : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarioBinding
    private var currentCalendar: Calendar = Calendar.getInstance()
    private val mapaAcoesPorData = mutableMapOf<String, MutableList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuração dos botões de navegação do calendário
        binding.btnPrevMonth.setOnClickListener { changeMonth(-1) }
        binding.btnNextMonth.setOnClickListener { changeMonth(1) }

        carregarAcoesDoBackend()

        binding.comeBack.setOnClickListener {
            comeBack()
        }

        binding.btnHome.setOnClickListener {
            navegarMenuPrincipal()
        }

        binding.btnWallet.setOnClickListener {
            navegarTelaCarteira()
        }
    }

    private fun changeMonth(offset: Int) {
        currentCalendar.add(Calendar.MONTH, offset)
        updateCalendar()
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale("pt", "BR"))
        binding.txtMonth.text = dateFormat.format(currentCalendar.time)

        binding.calendarGrid.removeAllViews()

        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDays = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Adiciona espaços vazios antes do primeiro dia do mês
        for (i in 0 until firstDayOfWeek) {
            val emptyView = TextView(this)
            emptyView.layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 120
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            binding.calendarGrid.addView(emptyView)
        }

        for (day in 1..maxDays) {
            val dayView = createCalendarTextView(day.toString())

            // Calcula o índice da célula (posição relativa no grid)
            val cellIndex = firstDayOfWeek + day - 1
            val dayOfWeek = cellIndex % 7

            // Deixa domingos em vermelho
            if (dayOfWeek == 0) {
                dayView.setTextColor(resources.getColor(R.color.red, null))
            }

            // Verifica se esse dia tem ações
            val dataAtual = Calendar.getInstance().apply {
                time = currentCalendar.time
                set(Calendar.DAY_OF_MONTH, day)
            }

            val dataFormatada = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dataAtual.time)
            Log.d("Calendario", "Verificando data no calendário: $dataFormatada")

            if (mapaAcoesPorData.containsKey(dataFormatada)) {
                val acoes = mapaAcoesPorData[dataFormatada]!!
                dayView.setBackgroundResource(R.drawable.border_cell_highlight_green)
                dayView.setOnClickListener {
                    Log.d("Calendario", "Dia $dataFormatada com ações: ${mapaAcoesPorData[dataFormatada]}")
                    Toast.makeText(this, "Ações: ${mapaAcoesPorData[dataFormatada]}", Toast.LENGTH_SHORT).show()
                }
            }

            binding.calendarGrid.addView(dayView)
        }
    }

    private fun createCalendarTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 15f
            gravity = Gravity.TOP or Gravity.START
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 120
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(6, 6, 6, 6)
            }
            setPadding(10, 6, 6, 6)
            setBackgroundResource(R.drawable.border_cell_white)
            setTextColor(resources.getColor(R.color.white, null))
        }
    }

    private fun carregarAcoesDoBackend() {
        RetrofitClient.instance.getAcoes().enqueue(object : Callback<List<AcaoTicker>> {
            override fun onResponse(call: Call<List<AcaoTicker>>, response: Response<List<AcaoTicker>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaAcoes = response.body()!!
                    val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                    for (acao in listaAcoes) {
                        val dataFormatada = try {
                            formatoEntrada.parse(acao.data_com)?.let {
                                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it)
                            }
                        } catch (e: Exception) {
                            null
                        }

                        dataFormatada?.let {
                            if (!mapaAcoesPorData.containsKey(it)) {
                                mapaAcoesPorData[it] = mutableListOf()
                            }
                            mapaAcoesPorData[it]!!.add(acao.ticker)
                        }
                    }
                    Log.d("Calendario", "mapaAcoesPorData keys: ${mapaAcoesPorData.keys}")

                    updateCalendar()
                    popularHistorico()

                } else {
                    Toast.makeText(this@Calendario, "Erro ao carregar ações", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AcaoTicker>>, t: Throwable) {
                Toast.makeText(this@Calendario, "Falha de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun popularHistorico() {
        val container = findViewById<LinearLayout>(R.id.historico_container)
        container.removeAllViews()

        val formatterEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatterSaida = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

        // Ordenar as datas decrescentes
        val diasOrdenados = mapaAcoesPorData.toSortedMap(compareBy { it })

        for ((data, tickers) in diasOrdenados) {
            val dataFormatada = try {
                formatterEntrada.parse(data)?.let { formatterSaida.format(it) } ?: data
            } catch (e: Exception) {
                data
            }

            val historicoView = layoutInflater.inflate(R.layout.item_historico, container, false)
            historicoView.findViewById<TextView>(R.id.txt_data).text = dataFormatada
            historicoView.findViewById<TextView>(R.id.txt_tickers).text = tickers.joinToString(", ")

            container.addView(historicoView)
        }
    }

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

    private fun navegarMenuPrincipal() {
        startActivity(Intent(this, MenuPrincipal::class.java))
    }

    private fun navegarTelaCarteira() {
        startActivity(Intent(this, Carteira::class.java))
    }
}
