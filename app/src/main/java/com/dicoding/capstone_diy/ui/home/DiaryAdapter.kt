package com.dicoding.capstone_diy.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.databinding.CardDiaryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryAdapter(
                   private val onItemClick: (Diary) -> Unit,
                   private val onFavoriteClick: (Diary) -> Unit
) :
    ListAdapter<Diary, DiaryAdapter.DiaryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = CardDiaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DiaryViewHolder(private val binding: CardDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary) {
            Log.d("DiaryAdapter", "Emotion: ${diary.emotion} Diary title: ${diary.title}, Date: ${diary.date}, Description: ${diary.description}")
            binding.tvLabelText.text = diary.emotion
            binding.dateText.text = formatTimestamp(diary.date)
            binding.titleText.text = diary.title
            binding.descriptionText.text = diary.description

            val favoriteIcon = if (diary.favorited) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
            binding.heartIcon.setImageResource(favoriteIcon)

            binding.heartIcon.setOnClickListener {
                val updatedDiary = diary.copy(favorited = !diary.favorited)
                onFavoriteClick(updatedDiary)
            }

            binding.root.setOnClickListener {
                onItemClick(diary)
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }
        }
    }
}
