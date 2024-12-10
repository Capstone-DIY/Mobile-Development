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
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository
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
    private lateinit var statisticViewModel: StatisticViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel dengan Factory
        val database = DiaryDatabase.getDatabase(requireContext())
        val diaryDao = database.diaryDao()
        val repository = DiaryRepository(diaryDao)

        val factory = StatisticViewModelFactory(repository)
        statisticViewModel = ViewModelProvider(this, factory).get(StatisticViewModel::class.java)

        setupObservers()
        setupChartHeader()

        return binding.root
    }

    private fun setupObservers() {
        // Observasi data statistik emosi
        statisticViewModel.emotionStatistics.observe(viewLifecycleOwner) { statistics ->
            updateBarChart(statistics)
        }

        // Observasi emosi dominan
        statisticViewModel.dominantEmotion.observe(viewLifecycleOwner) { dominantEmotion ->
            binding.overallEmotion.text = "Dominant Emotion: $dominantEmotion"
        }

        // Memuat data statistik
        statisticViewModel.loadEmotionStatisticsForLastWeek()
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
//        val typedValue = TypedValue()
//        val theme = requireContext().theme
//        theme.resolveAttribute(R.attr.chartAxisTextColor, typedValue, true)
//        val axisTextColor = typedValue.data
//        val visualMaxLevel = 20f // Tetap maksimum 20 pada sumbu Y
//        val visualMinLevel = 5f  // Tetap minimum 5 pada sumbu Y
//        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
//        val emotionColors = mapOf(
//            "Anger" to ContextCompat.getColor(requireContext(), R.color.red),
//            "Sadness" to ContextCompat.getColor(requireContext(), R.color.blue),
//            "Fear" to ContextCompat.getColor(requireContext(), R.color.green),
//            "Love" to ContextCompat.getColor(requireContext(), R.color.pink),
//            "Surprise" to ContextCompat.getColor(requireContext(), R.color.orange),
//            "Joy" to ContextCompat.getColor(requireContext(), R.color.yellow)
//        )
//
//        val rawData = arrayOf(
//            mapOf("Anger" to 10f), // Monday
//            mapOf("Sadness" to 11f),  // Tuesday
//            mapOf("Fear" to 12f), // Wednesday
//            mapOf("Love" to 13f),  // Thursday
//            mapOf("Surprise" to 14f), // Friday
//            mapOf("Joy" to 15f),  // Saturday
//            mapOf("Joy" to 17f)   // Sunday
//        )
//
//        val entries = ArrayList<BarEntry>()
//        val barColors = ArrayList<Int>()
//
//        // Menambahkan data bar dan menentukan warna berdasarkan emosi tertinggi
//        rawData.forEachIndexed { dayIndex, dayData ->
//            val highestEmotion = dayData.maxByOrNull { it.value } // Cari emosi tertinggi
//            val totalEmotion = dayData.values.sum() // Hitung total emosi
//
//            if (highestEmotion != null) {
//                entries.add(BarEntry(dayIndex.toFloat(), totalEmotion)) // Tambahkan total emosi untuk hari itu
//                barColors.add(emotionColors[highestEmotion.key] ?: Color.GRAY) // Tetapkan warna sesuai emosi tertinggi
//            } else {
//                barColors.add(Color.GRAY) // Jika tidak ada data, gunakan warna default
//            }
//        }
//
//        val dataSet = BarDataSet(entries, "Emotion Data").apply {
//            setColors(barColors)
//            valueTextColor = Color.TRANSPARENT // Hilangkan angka di atas bar
//            valueTextSize = 10f
//        }
//
//        val barData = BarData(dataSet)
//        barData.barWidth = 0.8f // Lebar bar
//
//        binding.emotionChart.apply {
//            data = barData
//
//            xAxis.apply {
//                position = XAxis.XAxisPosition.BOTTOM
//                granularity = 1f
//                textColor = axisTextColor
//                textSize = 10f
//                labelRotationAngle = 0f
//                axisMinimum = -0.5f // Mengatur agar sesuai dengan indeks hari
//                axisMaximum = days.size - 0.5f
//                setCenterAxisLabels(false)
//                setDrawGridLines(false)
//                valueFormatter = object : ValueFormatter() {
//                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                        return if (value.toInt() in days.indices) days[value.toInt()] else ""
//                    }
//                }
//            }
//
//            axisLeft.apply {
//                granularity = 1f
//                textSize = 12f
//                textColor = axisTextColor
//                axisMinimum = visualMinLevel // Tetap minimum 5
//                axisMaximum = visualMaxLevel // Tetap maksimum 20
//                labelCount = 4
//                valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return value.toInt().toString()
//                    }
//                }
//            }
//
//            axisRight.isEnabled = false
//            legend.isEnabled = false
//            description.isEnabled = false
//
//            animateY(1000) // Tambahkan animasi
//            invalidate()
//        }
    }

    private fun updateBarChart(statistics: Map<String, Int>) {
        // Data harian dan skala sumbu Y
        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val visualMinLevel = 5f  // Minimum pada sumbu Y
        val visualMaxLevel = 20f // Maksimum pada sumbu Y

        // Mengonversi data statistik menjadi entri untuk grafik
        val entries = statistics.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        // Menentukan warna berdasarkan emosi
        val colors = statistics.keys.map { emotion ->
            when (emotion) {
                "Anger" -> ContextCompat.getColor(requireContext(), R.color.red)
                "Sadness" -> ContextCompat.getColor(requireContext(), R.color.blue)
                "Joy" -> ContextCompat.getColor(requireContext(), R.color.yellow)
                "Fear" -> ContextCompat.getColor(requireContext(), R.color.green)
                "Love" -> ContextCompat.getColor(requireContext(), R.color.pink)
                "Surprise" -> ContextCompat.getColor(requireContext(), R.color.orange)
                else -> ContextCompat.getColor(requireContext(), R.color.dark_grey)
            }
        }

        // Menyiapkan dataset untuk grafik batang
        val dataSet = BarDataSet(entries, "Emotion Data").apply {
            setColors(colors)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
            valueTextSize = 10f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.8f
        }

        // Mengatur properti grafik batang
        binding.emotionChart.apply {
            data = barData

            // Sumbu X (hari)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textColor = ContextCompat.getColor(requireContext(), R.color.white)
                textSize = 10f
                axisMinimum = -0.5f // Menyesuaikan agar label sesuai dengan bar
                axisMaximum = days.size - 0.5f
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value.toInt() in days.indices) days[value.toInt()] else ""
                    }
                }
            }

            // Sumbu Y (angka 5-20)
            axisLeft.apply {
                granularity = 1f
                textColor = ContextCompat.getColor(requireContext(), R.color.white)
                textSize = 10f
                axisMinimum = visualMinLevel
                axisMaximum = visualMaxLevel
                labelCount = 4
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }

            // Nonaktifkan sumbu Y kanan
            axisRight.isEnabled = false

            // Properti tambahan
            legend.isEnabled = false
            description.isEnabled = false

            // Animasi
            animateY(1000)
            invalidate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
