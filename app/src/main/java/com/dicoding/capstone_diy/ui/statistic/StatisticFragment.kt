package com.dicoding.capstone_diy.ui.statistic

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstone_diy.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
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

        // Setup CandleStickChart
        setupCandleStickChart()

        return binding.root
    }

    private fun setupCandleStickChart() {
        // 1. Data untuk CandleStickChart
        val entries = ArrayList<CandleEntry>().apply {
            add(CandleEntry(1f, 3f, 0f, 0f, 3f)) // Senin: Range dari bawah (0) ke Fear (3)
            add(CandleEntry(2f, 4f, 0f, 0f, 4f)) // Selasa: Range dari bawah (0) ke Happy (4)
            add(CandleEntry(3f, 1f, 0f, 0f, 1f)) // Rabu: Range dari bawah (0) ke Angry (1)
            add(CandleEntry(4f, 3f, 0f, 0f, 3f)) // Kamis: Range dari bawah (0) ke Fear (3)
            add(CandleEntry(5f, 2f, 0f, 0f, 2f)) // Jumat: Range dari bawah (0) ke Love (2)
            add(CandleEntry(6f, 4f, 0f, 0f, 4f)) // Sabtu: Range dari bawah (0) ke Happy (4)
            add(CandleEntry(7f, 2f, 0f, 0f, 2f)) // Minggu: Range dari bawah (0) ke Love (2)
        }

        // 2. Konfigurasi dataset
        val dataSet = CandleDataSet(entries, "Your Emotions").apply {
            shadowColor = Color.GRAY
            shadowWidth = 0.7f
            decreasingColor = Color.RED // Warna candle menurun
            decreasingPaintStyle = android.graphics.Paint.Style.FILL
            increasingColor = Color.GREEN // Warna candle meningkat
            increasingPaintStyle = android.graphics.Paint.Style.FILL
            neutralColor = Color.YELLOW
            setDrawValues(true) // Tampilkan angka di atas candle
        }

        // Tambahkan ValueFormatter untuk dataset
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Menampilkan nilai sebagai bilangan bulat
            }
        }

        val candleData = CandleData(dataSet)

        // 3. Konfigurasi Chart
        binding.emotionChart.apply {
            data = candleData

            // Atur sumbu X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f // Pastikan jarak antar label tetap konsisten
                textColor = Color.BLACK
                textSize = 9f
                labelRotationAngle = 0f // Teks tetap horizontal
                axisMinimum = 0.5f // Geser sedikit agar grid sejajar
                axisMaximum = 7.5f // Pastikan ruang cukup untuk label terakhir
                setCenterAxisLabels(false) // Nonaktifkan pemusatan label
                setDrawGridLines(false) // Opsional: Sembunyikan grid vertikal
                valueFormatter = object : ValueFormatter() {
                    private val days = arrayOf(
                        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
                    )
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value.toInt() in 1..7) days[value.toInt() - 1] else ""
                    }
                }
                gridLineWidth = 1f
            }

            // Atur padding untuk teks
            setExtraOffsets(20f, 10f, 20f, 80f)

            // Atur sumbu Y
            axisLeft.apply {
                granularity = 1f
                textColor = Color.BLACK
                textSize = 9f
                axisMinimum = 0f // Mulai dari 0
                axisMaximum = 5f // Maksimum untuk kategori emosi
                labelCount = 6 // Jumlah label grid
                gridLineWidth = 1f // Ketebalan garis grid
                valueFormatter = object : ValueFormatter() {
                    private val emotions = arrayOf("Angry", "Sadness", "Fear", "Love", "Happy")
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return if (value.toInt() in 1..5) emotions[value.toInt() - 1] else ""
                    }
                }
            }

            // Hilangkan sumbu kanan
            axisRight.isEnabled = false

            // Konfigurasi legend (judul grafik)
            legend.apply {
                isEnabled = true
                textSize = 12f
                textColor = Color.BLACK
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            // Hilangkan deskripsi bawaan
            description.isEnabled = false

            // Refresh chart untuk menampilkan data
            invalidate()
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
