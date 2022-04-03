package it.doctorphones.com

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import it.doctorphones.com.dialogs.DoctorDetailsBottomDialog
import it.doctorphones.com.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val _tag = "MainActivity_DP"
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.showDoctorDetails.setOnClickListener {
            mBinding.showDoctorDetails.isEnabled = false
            DoctorDetailsBottomDialog().show(supportFragmentManager, DoctorDetailsBottomDialog.TAG)
            Handler(mainLooper).postDelayed({
                mBinding.showDoctorDetails.isEnabled = true
            }, 500)

        }

        /*val spinnerBinding = mBinding.addPhone.spinnerLayout
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
        }*/
    }
}