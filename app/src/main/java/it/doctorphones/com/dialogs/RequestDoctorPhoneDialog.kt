package it.doctorphones.com.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import it.doctorphones.com.R
import it.doctorphones.com.databinding.RequestForumDialogLayoutBinding
import it.doctorphones.com.utils.SearchableSpinnerUtil
import it.doctorphones.com.utils.provinces
import it.doctorphones.com.utils.specializations

class RequestDoctorPhoneDialog(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val onSaveClickListener: (province: String, specialize: String, doctorName: String) -> Unit
) : DialogFragment() {

    private lateinit var mBinding: RequestForumDialogLayoutBinding

    companion object {
        const val TAG = "RequestDoctorPhoneDialog_DP"
        fun newInstance(
            mContext: Context,
            mFragmentManager: FragmentManager,
            onSaveClickListener: (province: String, specialize: String, doctorName: String) -> Unit
        ): RequestDoctorPhoneDialog {
            return RequestDoctorPhoneDialog(mContext, mFragmentManager, onSaveClickListener)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = RequestForumDialogLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerBinding = mBinding.requestDoctorPhoneDialogSpinnerLayout
        spinnerBinding.spinnerProvinces.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                mFragmentManager,
                provinces,
                "المحافظات",
            ) { position, selectedString ->
                spinnerBinding.spinnerProvincesTxtValue.text = selectedString
                spinnerBinding.spinnerProvincesTxtValue.tag = position.toString()
            }
        }

        spinnerBinding.spinnerSpecializations.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                mFragmentManager,
                specializations,
                "التخصصات",
            ) { position, selectedString ->
                spinnerBinding.spinnerSpecializationsTxtValue.text = selectedString
                spinnerBinding.spinnerSpecializationsTxtValue.tag = position.toString()
            }
        }

        mBinding.requestDoctorPhoneDialogBtnRequest.setOnClickListener {
            var province = spinnerBinding.spinnerProvincesTxtValue.text.toString()
            var specialize = spinnerBinding.spinnerSpecializationsTxtValue.text.toString()
            if (spinnerBinding.spinnerProvincesTxtValue.tag.toString() == "0") {
                province = "0"
            }
            if (spinnerBinding.spinnerSpecializationsTxtValue.tag.toString() == "0") {
                specialize = "0"
            }
            if (mBinding.requestDoctorPhoneDialogEdtDoctorName.text.isBlank()) {
                mBinding.requestDoctorPhoneDialogTxtDoctorName.text = getText(R.string.required_field)
                mBinding.requestDoctorPhoneDialogTxtDoctorName.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                mBinding.requestDoctorPhoneDialogTxtDoctorName.visibility = View.GONE
            }
            onSaveClickListener(
                province,
                specialize,
                mBinding.requestDoctorPhoneDialogEdtDoctorName.text.toString()
            )
            dismiss()
        }

        mBinding.requestDoctorPhoneDialogImgClose.setOnClickListener {
            dismiss()
        }
    }
}