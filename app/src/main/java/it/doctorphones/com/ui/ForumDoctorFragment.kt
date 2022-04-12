package it.doctorphones.com.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.adapters.ForumRequestsRVAdapter
import it.doctorphones.com.adapters.ViewDoctorPhonesRVAdapter
import it.doctorphones.com.databinding.FragmentForumDoctorBinding
import it.doctorphones.com.dialogs.AppAlertDialog
import it.doctorphones.com.dialogs.DoctorDetailsBottomDialog
import it.doctorphones.com.dialogs.RequestDoctorPhoneDialog
import it.doctorphones.com.models.Doctor
import it.doctorphones.com.models.ForumRequest
import it.doctorphones.com.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ForumDoctorFragment : Fragment() {

    private val _tag = "ForumDoctorFragment_DP"

    private lateinit var mBinding: FragmentForumDoctorBinding
    private lateinit var mDialog: AppAlertDialog

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference


    private lateinit var mAdapter: ForumRequestsRVAdapter
    private var requests = mutableListOf<ForumRequest>()
    private var filterRequests = mutableListOf<ForumRequest>()
    private lateinit var requestsList: List<List<ForumRequest>>
    private var page = 0

    private var scrollingDown = false

    private val listener = object : RecyclerArrayAdapter.OnMoreListener {
        override fun onMoreShow() {
            Handler(Looper.getMainLooper()).postDelayed({
                if (page <= requestsList.size - 1) {
                    mAdapter.addAll(requestsList[page++])
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
        mBinding = FragmentForumDoctorBinding.inflate(inflater)
        mDialog = AppAlertDialog(requireContext())
        initRecyclerView()
        return mBinding.root
    }

    private fun initRecyclerView() {
        mBinding.forumDoctorRvForums.apply {
            // check if the device is a tablet
            setLayoutManager(LinearLayoutManager(context))
            mAdapter = ForumRequestsRVAdapter(context) { request ->
                Log.d(_tag, "initRecyclerView: ${request.doctorName}")
            }
            adapter = mAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (scrollingDown && dy >= 0) {
                        scrollingDown = !scrollingDown
                        mBinding.forumDoctorBtnAddForumRequest.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.fab_close
                            )
                        )
                    } else if (!scrollingDown && dy < 0) {
                        scrollingDown = !scrollingDown
                        mBinding.forumDoctorBtnAddForumRequest.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.fab_open
                            )
                        )
                    }
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerBinding = mBinding.forumDoctorSpinnerLayout
        spinnerBinding.spinnerProvinces.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                provinces,
                "المحافظات",
            ) { index, selectedString ->
                spinnerBinding.spinnerProvincesTxtValue.text = selectedString
                spinnerBinding.spinnerProvincesTxtValue.tag = index
                filterForumRequest()
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
                filterForumRequest()
            }
        }

        // get all doctors
        mBinding.forumDoctorRvForums.showProgress()
        loadForumRequests()

        // swipe refresh to load notifications again
        mBinding.forumDoctorRvForums.setRefreshListener {
            loadForumRequests()
        }

        mBinding.forumDoctorBtnAddForumRequest.setOnClickListener {
            val dialog = RequestDoctorPhoneDialog.newInstance(requireContext(), parentFragmentManager) { province, specialize, doctorName ->
                val key = database.child(FORUMS_TABLE).push().key
                if (key == null) {
                    Log.w(_tag, "Couldn't get push key for $DOCTORS_TABLE")
                    return@newInstance
                }
                val request = ForumRequest(
                    id = key,
                    userId = auth.currentUser!!.uid,
                    createdDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    province = province,
                    specialization = specialize,
                    doctorName = doctorName
                )
                mDialog.loadingAlert("حفظ الطلب", getString(R.string.please_wait))
                database.child(FORUMS_TABLE).child(key).setValue(request)
                    .addOnSuccessListener {
                        mDialog.showSuccessMessage("حفظ الطلب", getString(R.string.success_message))
                        loadForumRequests()
                    }.addOnFailureListener {
                        mDialog.showErrorMessage("حفظ الطلب", getString(R.string.error_occurred))
                    }
            }
            dialog.show(parentFragmentManager, RequestDoctorPhoneDialog.TAG)
        }
    }

    private fun loadForumRequests() {
        database.child(FORUMS_TABLE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    requests.clear()
                    for (postSnapshot in snapshot.children) {
                        val request = postSnapshot.getValue<ForumRequest>()
                        if (request != null) {
                            requests.add(request)
                        }
                    }
                    filterForumRequest()
                }

                override fun onCancelled(error: DatabaseError) {
                    mBinding.forumDoctorRvForums.showEmpty()
                }
            })
    }

    private fun filterForumRequest() {
        filterRequests = requests
        if (mBinding.forumDoctorSpinnerLayout.spinnerProvincesTxtValue.tag.toString() != "0") {
            filterRequests = filterRequests.filter {
                it.province == mBinding.forumDoctorSpinnerLayout.spinnerProvincesTxtValue.text.toString()
            } as MutableList<ForumRequest>
        }
        if (mBinding.forumDoctorSpinnerLayout.spinnerSpecializationsTxtValue.tag.toString() != "0") {
            filterRequests = filterRequests.filter {
                it.specialization == mBinding.forumDoctorSpinnerLayout.spinnerSpecializationsTxtValue.text.toString()
            } as MutableList<ForumRequest>
        }
        if (filterRequests.size > 0) {
            requestsList = filterRequests.chunked(20)
            page = 0
            mAdapter.setMore(R.layout.load_more_layout, listener)
            mAdapter.clear()
            mAdapter.addAll(requestsList[page++])
        } else {
            mBinding.forumDoctorRvForums.showEmpty()
        }
    }
}