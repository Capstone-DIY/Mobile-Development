package com.dicoding.capstone_diy.ui.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.capstone_diy.databinding.FragmentStatisticBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

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
        val root: View = binding.root

        // Setup for LineChart
        setupLineChart()

        return root
    }

    private fun setupLineChart() {
        val entries = ArrayList<Entry>()
        // Example data
        entries.add(Entry(1f, 1f))
        entries.add(Entry(2f, 3f))
        entries.add(Entry(3f, 2f))
        entries.add(Entry(4f, 5f))
        entries.add(Entry(5f, 4f))

        val dataSet = LineDataSet(entries, "Emotions")
        val lineData = LineData(dataSet)

        binding.emotionChart.data = lineData
        binding.emotionChart.invalidate() // Refresh chart
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
