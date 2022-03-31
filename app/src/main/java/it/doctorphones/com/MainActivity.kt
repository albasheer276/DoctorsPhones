package it.doctorphones.com

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.doctorphones.com.databinding.ActivityMainBinding
import it.doctorphones.com.utils.SearchableSpinnerUtil
import it.doctorphones.com.utils.provinces
import it.doctorphones.com.utils.specializations

class MainActivity : AppCompatActivity() {
    private val _tag = "MainActivity_DP"
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val spinnerBinding = mBinding.addPhone.spinnerLayout
        spinnerBinding.spinnerProvinces.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                supportFragmentManager,
                provinces,
                "المحافظات",
                ) { position, selectedString ->
                spinnerBinding.spinnerProvincesTxtValue.text = selectedString
            }
        }

        spinnerBinding.spinnerSpecializations.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                supportFragmentManager,
                specializations,
                "التخصصات",
            ) { position, selectedString ->
                spinnerBinding.spinnerSpecializationsTxtValue.text = selectedString
            }
        }
    }
}