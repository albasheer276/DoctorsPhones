package it.doctorphones.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import it.doctorphones.com.databinding.ActivityMainBinding
import it.doctorphones.com.databinding.SpinnersLayoutBinding
import it.doctorphones.com.utils.SearchableSpinnerUtil
import it.doctorphones.com.utils.provinces

class MainActivity : AppCompatActivity() {
    private val _log = "MainActivity_DP"
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val spinnerBinding = mBinding.spinnerLayout
        spinnerBinding.spinnerTxtProvince.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(this, provinces, "المحافظات"){ position: Int, selectedString: String ->
                Log.d(_log, "onCreate: $position $selectedString")
            }
        }
    }
}