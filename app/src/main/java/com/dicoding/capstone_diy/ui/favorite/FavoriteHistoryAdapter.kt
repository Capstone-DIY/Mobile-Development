package com.dicoding.capstone_diy.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.capstone_diy.databinding.ItemFavoriteHistoryBinding

class FavoriteHistoryAdapter :
    ListAdapter<FavoriteItem, FavoriteHistoryAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteItem>() {
            override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoriteViewHolder(private val binding: ItemFavoriteHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoriteItem) {
            binding.tvDate.text = favorite.date
            binding.tvTitle.text = favorite.title
            binding.tvDescription.text = favorite.description
        }
    }
}
