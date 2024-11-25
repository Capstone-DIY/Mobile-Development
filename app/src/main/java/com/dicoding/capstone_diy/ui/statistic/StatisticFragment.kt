package com.dicoding.capstone_diy.ui.statistic

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
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
import com.dicoding.capstone_diy.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statisticViewModel =
            ViewModelProvider(this).get(StatisticViewModel::class.java)

        _binding = FragmentStatisticBinding.inflate(inflater, container, false)

        setupChartHeader() // Tambahkan header untuk warna dan nama emosi
        setupBarChart() // Setup grafik

        return binding.root
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
        headerLayout.removeAllViews() // Kosongkan header jika ada

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
                    1f // Distribusi elemen proporsional
                ).apply {
                    setMargins(0, 0, 0, 0) // Tambahkan margin untuk ruang antar elemen
                }
            }

            val colorBox = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(30, 30).apply {
                    setMargins(0, 0, 5, 0) // Ruang antara kotak warna dan teks
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
                    width = 120 // Tetapkan lebar tetap untuk elemen teks
                }
            }

            itemLayout.addView(colorBox)
            itemLayout.addView(textView)
            tableRow.addView(itemLayout)
        }

        headerLayout.addView(tableRow)
    }

    private fun setupBarChart() {
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.chartAxisTextColor, typedValue, true)
        val axisTextColor = typedValue.data
        val visualMaxLevel = 20f // Maksimum sumbu Y
        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val emotions = arrayOf("Anger", "Sadness", "Fear", "Love", "Surprise", "Joy")
        val emotionColors = mapOf(
            "Anger" to ContextCompat.getColor(requireContext(), R.color.red),
            "Sadness" to ContextCompat.getColor(requireContext(), R.color.blue),
            "Fear" to ContextCompat.getColor(requireContext(), R.color.green),
            "Love" to ContextCompat.getColor(requireContext(), R.color.pink),
            "Surprise" to ContextCompat.getColor(requireContext(), R.color.orange),
            "Joy" to ContextCompat.getColor(requireContext(), R.color.yellow)
        )

        val rawData = arrayOf(
            mapOf("Anger" to 10f), // Monday
            mapOf("Sadness" to 11f),  // Tuesday
            mapOf("Fear" to 12f), // Wednesday
            mapOf("Love" to 13f),  // Thursday
            mapOf("Surprise" to 14f), // Friday
            mapOf("Joy" to 15f),  // Saturday
            mapOf("Joy" to 17f)   // Sunday
        )

        val entries = ArrayList<BarEntry>()
        val barColors = ArrayList<Int>()

        // Menambahkan data bar dan menentukan warna berdasarkan emosi tertinggi
        rawData.forEachIndexed { dayIndex, dayData ->
            val highestEmotion = dayData.maxByOrNull { it.value } // Cari emosi tertinggi
            val totalEmotion = dayData.values.sum() // Hitung total emosi

            if (highestEmotion != null) {
                entries.add(BarEntry(dayIndex + 1f, totalEmotion)) // Tambahkan total emosi untuk hari itu
                barColors.add(emotionColors[highestEmotion.key] ?: Color.GRAY) // Tetapkan warna sesuai emosi tertinggi
            } else {
                barColors.add(Color.GRAY) // Jika tidak ada data, gunakan warna default
            }
        }

        val dataSet = BarDataSet(entries, "Emotion Data").apply {
            setColors(barColors)
            valueTextColor = Color.TRANSPARENT // Hilangkan angka di atas bar
            valueTextSize = 10f
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f // Lebar bar

        binding.emotionChart.apply {
            data = barData

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textColor = axisTextColor
                textSize = 10f
                labelRotationAngle = 0f
                axisMinimum = 0.5f
                axisMaximum = days.size + 0.5f
                setCenterAxisLabels(false)
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value.toInt() in 1..days.size) days[value.toInt() - 1] else ""
                    }
                }
            }

            axisLeft.apply {
                granularity = 5f
                textSize = 12f
                textColor = axisTextColor
                axisMinimum = 0f
                axisMaximum = visualMaxLevel
                labelCount = 7
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }

            axisRight.isEnabled = false

            legend.isEnabled = false
            description.isEnabled = false

            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
