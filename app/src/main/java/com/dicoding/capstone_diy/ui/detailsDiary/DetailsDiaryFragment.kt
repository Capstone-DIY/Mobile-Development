package com.dicoding.capstone_diy.ui.detailsDiary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.databinding.FragmentDetailsDiaryBinding
import com.dicoding.capstone_diy.ui.details.DetailsDiaryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsDiaryFragment : Fragment() {

    private var _binding: FragmentDetailsDiaryBinding? = null
    private val binding get() = _binding!!

    private val detailsDiaryViewModel: DetailsDiaryViewModel by viewModels {
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        val repository = DiaryRepository(diaryDao)
        DetailsDiaryViewModelFactory(repository, requireContext())
    }


    private var diary: Diary? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsDiaryBinding.inflate(inflater, container, false)

        diary = arguments?.let {
            BundleCompat.getParcelable(it, "diary", Diary::class.java)
        }

        diary?.let { updateUI(it) }

        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDelete.setOnClickListener {
            diary?.let { diary ->
                detailsDiaryViewModel.deleteDiary(diary)
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigateUp()
                }, 2000)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<View>(R.id.nav_view).visibility = View.GONE
    }

    private fun updateUI(diary: Diary) {
        binding.dateText.text = formatTimestamp(diary.date)
        binding.titleText.text = diary.title
        binding.descriptionText.text = diary.description
        binding.tvLabelText.text = diary.emotion
        binding.responseText.text = diary.response

        updateFavoriteIcon(diary.favorited)

        binding.heartIcon.setOnClickListener {
            val updatedDiary = this.diary?.copy(favorited = !(this.diary?.favorited ?: false))
            if (updatedDiary != null) {
                detailsDiaryViewModel.updateDiary(updatedDiary)
                this.diary = updatedDiary
                updateFavoriteIcon(updatedDiary.favorited)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorited: Boolean) {
        val favoriteIcon = if (isFavorited) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        binding.heartIcon.setImageResource(favoriteIcon)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
