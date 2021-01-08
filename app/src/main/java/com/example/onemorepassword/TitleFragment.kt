package com.example.onemorepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.onemorepassword.databinding.TitleFragmentBinding

class TitleFragment: Fragment() {

    private lateinit var binding: TitleFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<TitleFragmentBinding>(inflater,
        R.layout.title_fragment, container, false)


        val myImage = binding.ompTitleImageImageView
        myImage.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_titleFragment_to_oneMorePasswordFragment)
        }

        return binding.root
    }



}