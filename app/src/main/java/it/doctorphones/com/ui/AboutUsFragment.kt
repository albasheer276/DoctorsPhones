package it.doctorphones.com.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentAboutUsBinding
import it.doctorphones.com.utils.FORUMS_TABLE
import it.doctorphones.com.utils.USER_ID


class AboutUsFragment : Fragment() {

    private lateinit var mBinding: FragmentAboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutUsBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set a custom toolbar
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(mBinding.aboutUsToolbar)
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