package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.databinding.ActivityCalendarioBinding
import java.text.SimpleDateFormat
import java.util.*

class Calendario : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarioBinding
    private var currentCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuração dos botões de navegação do calendário
        binding.btnPrevMonth.setOnClickListener { changeMonth(-1) }
        binding.btnNextMonth.setOnClickListener { changeMonth(1) }

        updateCalendar()

        binding.comeBack.setOnClickListener {
            comeBack()
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

        val today = Calendar.getInstance()
        val isCurrentMonth = today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
        val actionDay = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) + 1 else -1
        val actionStockName = "GGBR4"

        for (day in 1..maxDays) {
            val dayView = createCalendarTextView(day.toString())

            // Calcula o índice da célula (posição relativa no grid)
            val cellIndex = firstDayOfWeek + day - 1
            val dayOfWeek = cellIndex % 7

            // Deixa domingos em vermelho
            if (dayOfWeek == 0) {
                dayView.setTextColor(resources.getColor(R.color.red, null))
            }

            // Destaca o próximo dia com ação
            if (day == actionDay) {
                dayView.setBackgroundResource(R.drawable.border_cell_highlight_green)
                dayView.setOnClickListener {
                    Toast.makeText(this, "Ação: $actionStockName", Toast.LENGTH_SHORT).show()
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

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}
