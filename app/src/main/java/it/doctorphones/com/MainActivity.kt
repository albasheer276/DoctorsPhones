package it.doctorphones.com

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ThemeUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.databinding.ActivityMainBinding
import it.doctorphones.com.utils.DOCTORS_TABLE
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _tag = "MainActivity_DP"
    private lateinit var mBinding: ActivityMainBinding

    @Inject
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val window = this.window
        //window.statusBarColor = this.resources.getColor(R.color.md_theme_light_secondary, theme)

        /*val dialog = RequestDoctorPhoneDialog.newInstance(this, supportFragmentManager){ province, specialize, doctorName ->

        }
        dialog.show(supportFragmentManager, RequestDoctorPhoneDialog.TAG)*/

        /*mBinding.showDoctorDetails.setOnClickListener {
            mBinding.showDoctorDetails.isEnabled = false
            DoctorDetailsBottomDialog().show(supportFragmentManager, DoctorDetailsBottomDialog.TAG)
            Handler(mainLooper).postDelayed({
                mBinding.showDoctorDetails.isEnabled = true
            }, 500)

        }*/

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