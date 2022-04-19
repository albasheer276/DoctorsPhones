package it.doctorphones.com.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {

    private lateinit var mBinding: FragmentAboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutUsBinding.inflate(inflater)
        return mBinding.root
    }
}