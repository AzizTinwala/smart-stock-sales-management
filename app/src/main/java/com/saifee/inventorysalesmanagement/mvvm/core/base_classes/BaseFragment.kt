package com.saifee.inventorysalesmanagement.mvvm.core.base_classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<V : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> V
) : Fragment() {

    private lateinit var _binding: V
    val binding: V get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }
}