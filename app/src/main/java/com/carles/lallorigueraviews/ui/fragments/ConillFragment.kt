package com.carles.lallorigueraviews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carles.lallorigueraviews.MainActivity
import com.carles.lallorigueraviews.Navigator
import com.carles.lallorigueraviews.R
import com.carles.lallorigueraviews.databinding.FragmentConillBinding
import com.carles.lallorigueraviews.ui.common.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConillFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator

    private var _binding: FragmentConillBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.conillOkButton.setDebounceClickListener { navigator.up() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}