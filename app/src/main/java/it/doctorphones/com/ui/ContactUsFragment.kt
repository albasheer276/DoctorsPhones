package it.doctorphones.com.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {

    private lateinit var mBinding: FragmentContactUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactUsBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set a custom toolbar
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(mBinding.contactUsToolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }

            else -> {
                false
            }
        }
    }
}