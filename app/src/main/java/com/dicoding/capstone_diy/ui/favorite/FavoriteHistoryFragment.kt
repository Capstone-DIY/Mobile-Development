package com.dicoding.capstone_diy.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.capstone_diy.databinding.FragmentFavoriteHistoryBinding

class FavoriteHistoryFragment : Fragment() {

    private var _binding: FragmentFavoriteHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        val adapter = FavoriteHistoryAdapter()
        binding.rvFavoriteHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteHistory.adapter = adapter

        // Observe data dari ViewModel
        viewModel.favoriteList.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isNotEmpty()) {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvFavoriteHistory.visibility = View.VISIBLE
                adapter.submitList(favorites)
            } else {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvFavoriteHistory.visibility = View.GONE
            }
        }

        // Tombol kembali
        binding.btnBackFavorite.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
