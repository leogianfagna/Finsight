package br.edu.puccampinas.frontend

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
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
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
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

        // Adiciona os dias do mês
        for (day in 1..maxDays) {
            val dayView = createCalendarTextView(day.toString())
            binding.calendarGrid.addView(dayView)
        }
    }

    private fun createCalendarTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            gravity = Gravity.START or Gravity.TOP // canto superior esquerdo
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 120
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            setPadding(12, 8, 8, 8) // espaço do número pro canto
            setBackgroundResource(R.drawable.border_cell)
        }
    }

    private fun comeBack() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}
