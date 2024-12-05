package com.dicoding.capstone_diy.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var diaryAdapter: DiaryAdapter

    // Inisialisasi ViewModel dengan factory
    private val homeViewModel: HomeViewModel by viewModels {
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        val repository = DiaryRepository(diaryDao)
        HomeViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inisialisasi RecyclerView
        initRecyclerView()

        // Set OnClickListener untuk FAB (Floating Action Button) untuk navigasi ke AddDiaryFragment
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addDiaryFragment)
        }

        return binding.root
    }

    private fun initRecyclerView() {
        // Inisialisasi Adapter dengan meneruskan fungsi navigasi ke DetailFragment
        diaryAdapter = DiaryAdapter(
            onItemClick = { diary ->
                val bundle = Bundle().apply {
                    putParcelable("diary", diary)
                }
                findNavController().navigate(R.id.detailsDiaryFragment, bundle)
            },
            onFavoriteClick = { diary ->
                homeViewModel.updateDiary(diary) // Panggil fungsi di ViewModel
            }
        )

        // Mengamati perubahan data diaries
        homeViewModel.diaries.observe(viewLifecycleOwner, Observer { diaries ->
            if (diaries.isNotEmpty()) {
                // Menampilkan RecyclerView
                binding.recyclerView.visibility = View.VISIBLE
                diaryAdapter.submitList(diaries)
                binding.addImage.visibility = View.GONE
                binding.textHome.visibility = View.GONE
            } else {
                // Menampilkan placeholder jika data kosong
                binding.recyclerView.visibility = View.GONE
                binding.addImage.visibility = View.VISIBLE
                binding.textHome.visibility = View.VISIBLE
            }
        })

        // Atur RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = diaryAdapter

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
