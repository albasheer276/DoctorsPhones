package it.doctorphones.com.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.adapters.ViewDoctorPhonesRVAdapter
import it.doctorphones.com.databinding.FragmentViewDoctorPhonesBinding
import it.doctorphones.com.dialogs.AddDoctorPhoneDialog
import it.doctorphones.com.dialogs.DoctorDetailsBottomDialog
import it.doctorphones.com.managers.RtlGridLayoutManager
import it.doctorphones.com.models.Doctor
import it.doctorphones.com.utils.DOCTORS_TABLE
import it.doctorphones.com.utils.SearchableSpinnerUtil
import it.doctorphones.com.utils.provinces
import it.doctorphones.com.utils.specializations
import javax.inject.Inject

@AndroidEntryPoint
class ViewDoctorPhonesFragment : Fragment() {

    private val _tag = "ViewDoctorPhonesFragmen_DP"
    private lateinit var mBinding: FragmentViewDoctorPhonesBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

    private lateinit var mAdapter: ViewDoctorPhonesRVAdapter
    private var doctors = mutableListOf<Doctor>()
    private var filterDoctors = mutableListOf<Doctor>()
    private lateinit var doctorList: List<List<Doctor>>
    private var page = 0

    private val listener = object : RecyclerArrayAdapter.OnMoreListener {
        override fun onMoreShow() {
            Handler(Looper.getMainLooper()).postDelayed({
                if (page <= doctorList.size - 1) {
                    mAdapter.addAll(doctorList[page++])
                } else {
                    mAdapter.stopMore()
                }
            }, 100)
        }

        override fun onMoreClick() {}
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentViewDoctorPhonesBinding.inflate(inflater)
        initRecyclerView()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerBinding = mBinding.viewDoctorPhonesSpinnerLayout
        spinnerBinding.spinnerProvinces.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                provinces,
                "المحافظات",
            ) { index, selectedString ->
                spinnerBinding.spinnerProvincesTxtValue.text = selectedString
                spinnerBinding.spinnerProvincesTxtValue.tag = index
                filterDoctorPhones()
            }
        }

        spinnerBinding.spinnerSpecializations.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                specializations,
                "التخصصات",
            ) { index, selectedString ->
                spinnerBinding.spinnerSpecializationsTxtValue.text = selectedString
                spinnerBinding.spinnerSpecializationsTxtValue.tag = index
                filterDoctorPhones()
            }
        }

        // get all doctors
        mBinding.viewDoctorPhonesRvDoctorPhones.showProgress()
        loadDoctorPhones()

        // swipe refresh to load notifications again
        mBinding.viewDoctorPhonesRvDoctorPhones.setRefreshListener {
            loadDoctorPhones()
        }

        mBinding.viewDoctorPhonesBtnAddDoctor.setOnClickListener {
            val dialog = AddDoctorPhoneDialog.newInstance()
            dialog.show(parentFragmentManager, AddDoctorPhoneDialog.TAG)
        }
    }

    private fun filterDoctorPhones() {
        filterDoctors = doctors
        if (mBinding.viewDoctorPhonesSpinnerLayout.spinnerProvincesTxtValue.tag.toString() != "0") {
            filterDoctors = filterDoctors.filter {
                it.province == mBinding.viewDoctorPhonesSpinnerLayout.spinnerProvincesTxtValue.text.toString()
            } as MutableList<Doctor>
        }
        if (mBinding.viewDoctorPhonesSpinnerLayout.spinnerSpecializationsTxtValue.tag.toString() != "0") {
            filterDoctors = filterDoctors.filter {
                it.specialization == mBinding.viewDoctorPhonesSpinnerLayout.spinnerSpecializationsTxtValue.text.toString()
            } as MutableList<Doctor>
        }
        if (filterDoctors.size > 0) {
            doctorList = filterDoctors.chunked(20)
            page = 0
            mAdapter.setMore(R.layout.load_more_layout, listener)
            mAdapter.clear()
            mAdapter.addAll(doctorList[page++])
        } else {
            mBinding.viewDoctorPhonesRvDoctorPhones.showEmpty()
        }
    }

    private fun loadDoctorPhones() {
        database.child(DOCTORS_TABLE)
            .orderByChild("status")
            .equalTo("public")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onDataChange(snapshot: DataSnapshot) {
                    doctors.clear()
                    for (postSnapshot in snapshot.children) {
                        val doctor = postSnapshot.getValue<Doctor>()
                        if (doctor != null) {
                            doctors.add(doctor)
                        }
                    }
                    filterDoctorPhones()
                }

                override fun onCancelled(error: DatabaseError) {
                    mBinding.viewDoctorPhonesRvDoctorPhones.showEmpty()
                }
            })
    }

    private fun initRecyclerView() {
        mBinding.viewDoctorPhonesRvDoctorPhones.apply {
            // check if the device is a tablet
            setLayoutManager(RtlGridLayoutManager(context, 2))
            mAdapter = ViewDoctorPhonesRVAdapter(context) { doctor ->
                DoctorDetailsBottomDialog(doctor).show(parentFragmentManager, DoctorDetailsBottomDialog.TAG)
            }
            adapter = mAdapter
        }
    }
}