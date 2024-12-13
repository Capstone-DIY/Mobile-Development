package com.dicoding.capstone_diy.ui.statistic

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var statisticViewModel: StatisticViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)

        val database = DiaryDatabase.getDatabase(requireContext())
        val diaryDao = database.diaryDao()
        val repository = DiaryRepository(diaryDao)

        val factory = StatisticViewModelFactory(repository, requireContext())
        statisticViewModel = ViewModelProvider(this, factory).get(StatisticViewModel::class.java)

        setupObservers()
        setupChartHeader()

        return binding.root
    }

    private fun setupObservers() {

        statisticViewModel.dailyDominantEmotionStatistics.observe(viewLifecycleOwner) { dailyStatistics ->
            updateDailyBarChart(dailyStatistics)
        }

        statisticViewModel.dominantEmotion.observe(viewLifecycleOwner) { dominantEmotion ->
            binding.overallEmotion.text = "Overall Emotion: $dominantEmotion"
            statisticViewModel.fetchQuote()
        }

        statisticViewModel.quote.observe(viewLifecycleOwner) { quote ->
            binding.quoteText.text = quote // Bind the quote to TextView
        }

        statisticViewModel.loadEmotionStatisticsForLastWeek()
        statisticViewModel.loadDailyDominantEmotionStatistics()
    }


    private fun setupChartHeader() {
        val emotions = arrayOf("Anger", "Sadness", "Fear", "Love", "Surprise", "Joy")
        val emotionColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.pink),
            ContextCompat.getColor(requireContext(), R.color.orange),
            ContextCompat.getColor(requireContext(), R.color.yellow)
        )

        val headerLayout = binding.chartHeader
        headerLayout.removeAllViews()

        val tableRow = TableRow(requireContext()).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_HORIZONTAL
        }

        emotions.forEachIndexed { index, emotion ->
            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(0, 0, 0, 0)
                }
            }

            val colorBox = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(30, 30).apply {
                    setMargins(0, 0, 5, 0)
                }
                setBackgroundColor(emotionColors[index])
            }

            val textView = TextView(requireContext()).apply {
                text = emotion
                textSize = 12f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    width = 120
                }
            }

            itemLayout.addView(colorBox)
            itemLayout.addView(textView)
            tableRow.addView(itemLayout)
        }

        headerLayout.addView(tableRow)
    }

    private fun updateDailyBarChart(dailyStatistics: Map<String, Pair<String, Int>>) {
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.chartAxisTextColor, typedValue, true)
        val axisTextColor = typedValue.data
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val daysOfWeek = mutableListOf<String>()
        for (i in 6 downTo 0) { // Mundur 6 hari
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            daysOfWeek.add(dateFormat.format(calendar.time))
        }

        val dayLabels = daysOfWeek.map {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(dateFormat.parse(it)!!)
        }

        val entries = mutableListOf<BarEntry>()
        val colors = mutableListOf<Int>()

        daysOfWeek.forEachIndexed { index, date ->
            val dominantEmotion = dailyStatistics[date]
            if (dominantEmotion != null) {
                val (emotion, count) = dominantEmotion
                entries.add(BarEntry(index.toFloat(), count.toFloat()))
                val color = when (emotion.lowercase(Locale.getDefault())) {
                    "anger" -> ContextCompat.getColor(requireContext(), R.color.red)
                    "sadness" -> ContextCompat.getColor(requireContext(), R.color.blue)
                    "joy" -> ContextCompat.getColor(requireContext(), R.color.yellow)
                    "fear" -> ContextCompat.getColor(requireContext(), R.color.green)
                    "love" -> ContextCompat.getColor(requireContext(), R.color.pink)
                    "surprise" -> ContextCompat.getColor(requireContext(), R.color.orange)
                    else -> ContextCompat.getColor(requireContext(), R.color.dark_grey) // Default
                }
                colors.add(color)
            } else {
                entries.add(BarEntry(index.toFloat(), 0f))
                colors.add(ContextCompat.getColor(requireContext(), R.color.dark_grey))
            }
        }

        val dataSet = BarDataSet(entries, "Daily Dominant Emotion").apply {
            setColors(colors)
            valueTextSize = 10f
            valueTextColor = axisTextColor
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        binding.emotionChart.apply {
            data = BarData(dataSet)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = axisTextColor
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value.toInt() in dayLabels.indices) dayLabels[value.toInt()] else ""
                    }
                }
            }
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 10f
                textColor = axisTextColor

            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false

            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(true)

            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
