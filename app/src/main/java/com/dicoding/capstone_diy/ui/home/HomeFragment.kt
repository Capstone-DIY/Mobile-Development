package com.dicoding.capstone_diy.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    private val homeViewModel: HomeViewModel by viewModels {
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        val repository = DiaryRepository(diaryDao)
        HomeViewModelFactory(repository, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initRecyclerView()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addDiaryFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.fetchDiariesFromApi()
        homeViewModel.isTokenExpired.observe(viewLifecycleOwner, Observer { isExpired ->
            if (isExpired) {
                Log.e("HomeFragment", "Token expired, navigating to login")
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        })

        homeViewModel.apiStatus.observe(viewLifecycleOwner, Observer { status ->
            Log.d("HomeFragment", "API Status: $status")
        })
    }

    private fun initRecyclerView() {
        diaryAdapter = DiaryAdapter(
            onItemClick = { diary ->
                val bundle = Bundle().apply {
                    putParcelable("diary", diary)
                }
                findNavController().navigate(R.id.detailsDiaryFragment, bundle)
            },
            onFavoriteClick = { diary ->
                homeViewModel.updateDiary(diary)
            }
        )

        homeViewModel.diaries.observe(viewLifecycleOwner, Observer { diaries ->
            if (diaries.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                diaryAdapter.submitList(diaries)
                binding.addImage.visibility = View.GONE
                binding.textHome.visibility = View.GONE
                Log.d("HomeFragment", "Diaries loaded successfully")
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.addImage.visibility = View.VISIBLE
                binding.textHome.visibility = View.VISIBLE
                Log.d("HomeFragment", "No diaries available")
            }
        })

        homeViewModel.apiStatus.observe(viewLifecycleOwner, Observer { status ->
            Log.d("HomeFragment", "API Status: $status")
        })

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
