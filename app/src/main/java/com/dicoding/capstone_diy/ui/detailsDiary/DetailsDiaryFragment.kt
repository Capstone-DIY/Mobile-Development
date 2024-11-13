package com.dicoding.capstone_diy.ui.detailsDiary

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.capstone_diy.R

class DetailsDiaryFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsDiaryFragment()
    }

    private val viewModel: DetailsDiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_details_diary, container, false)
    }
}