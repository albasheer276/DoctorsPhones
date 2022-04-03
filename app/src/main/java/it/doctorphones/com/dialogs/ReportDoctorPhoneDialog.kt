package it.doctorphones.com.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import it.doctorphones.com.R
import it.doctorphones.com.databinding.ReportDoctorPhoneDialogLayoutBinding
import it.doctorphones.com.utils.SearchableSpinnerUtil
import it.doctorphones.com.utils.reportReasons

class ReportDoctorPhoneDialog(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val onSaveClickListener: (reason: String, note: String) -> Unit
) : DialogFragment() {

    private lateinit var mBinding: ReportDoctorPhoneDialogLayoutBinding

    companion object {
        const val TAG = "ReportDialogFragment_DP"
        fun newInstance(
            mContext: Context,
            mFragmentManager: FragmentManager,
            onSaveClickListener: (reason: String, note: String) -> Unit
        ): ReportDoctorPhoneDialog {
            return ReportDoctorPhoneDialog(mContext, mFragmentManager, onSaveClickListener)
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
        mBinding = ReportDoctorPhoneDialogLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.reportDoctorPhoneDialogSpinnerReason.setOnClickListener {
            SearchableSpinnerUtil.setupSpinner(
                mFragmentManager,
                reportReasons,
                mContext.getString(R.string.phone_not_working),
            ) { _, selectedString ->
                mBinding.reportDoctorPhoneDialogSpinnerTxtValue.text = selectedString
            }
        }

        mBinding.reportDoctorPhoneDialogBtnSend.setOnClickListener {
            onSaveClickListener(
                mBinding.reportDoctorPhoneDialogSpinnerTxtValue.text.toString(),
                mBinding.reportDoctorPhoneDialogEdtNote.text.toString()
            )
            dismiss()
        }

        mBinding.reportDoctorPhoneDialogImgClose.setOnClickListener {
            dismiss()
        }
    }
}