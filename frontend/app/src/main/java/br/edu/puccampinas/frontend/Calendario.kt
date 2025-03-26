import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.frontend.R
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
    }

    private fun changeMonth(offset: Int) {
        currentCalendar.add(Calendar.MONTH, offset)
        updateCalendar()
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        binding.txtMonth.text = dateFormat.format(currentCalendar.time)

        binding.calendarGrid.removeAllViews()

        // Dias da semana
        val daysOfWeek = listOf("D", "S", "T", "Q", "Q", "S", "S")
        for (day in daysOfWeek) {
            val textView = createCalendarTextView(day, true)
            binding.calendarGrid.addView(textView)
        }

        // Ajuste para o primeiro dia do mês
        val tempCalendar = currentCalendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1

        for (i in 0 until firstDayOfWeek) {
            val emptyView = TextView(this)
            emptyView.layoutParams = GridLayout.LayoutParams().apply {
                width = 120
                height = 120
            }
            binding.calendarGrid.addView(emptyView)
        }

        // Preenchendo os dias do mês
        val maxDays = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDays) {
            val dayView = createCalendarTextView(day.toString(), false)
            binding.calendarGrid.addView(dayView)
        }
    }

    private fun createCalendarTextView(text: String, isHeader: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = if (isHeader) 14f else 16f
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.drawable.border_cell)
        }
    }
}