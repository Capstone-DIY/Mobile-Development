package com.dicoding.capstone_diy.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.databinding.FragmentFavoriteHistoryBinding
import com.dicoding.capstone_diy.ui.home.DiaryAdapter
import com.dicoding.capstone_diy.data.DiaryDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteHistoryFragment : Fragment() {

    private var _binding: FragmentFavoriteHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var viewModel: FavoriteHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteHistoryBinding.inflate(inflater, container, false)

        // Inisialisasi repository dan ViewModel
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        val repository = DiaryRepository(diaryDao)
        val factory = FavoriteHistoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoriteHistoryViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<View>(R.id.nav_view).visibility = View.GONE

        // Inisialisasi Adapter
        diaryAdapter = DiaryAdapter(
            onItemClick = { diary ->
                val bundle = Bundle().apply {
                    putParcelable("diary", diary)
                }
                findNavController().navigate(R.id.detailsDiaryFragment, bundle)
            },
            onFavoriteClick = { diary ->
                viewModel.updateDiary(diary)
            }
        )

        // Atur RecyclerView
        binding.rvFavoriteHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = diaryAdapter
        }

        // Observe data favorit dari ViewModel menggunakan Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collectLatest { favorites ->
                if (favorites.isNotEmpty()) {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvFavoriteHistory.visibility = View.VISIBLE
                    diaryAdapter.submitList(favorites)
                } else {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvFavoriteHistory.visibility = View.GONE
                }
            }
        }

        // Tombol Kembali
        binding.btnBackFavorite.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
