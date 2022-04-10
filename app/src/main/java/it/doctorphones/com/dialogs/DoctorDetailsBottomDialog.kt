package it.doctorphones.com.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.doctorphones.com.databinding.DoctorDetailsDialogLayoutBinding

class DoctorDetailsBottomDialog : BottomSheetDialogFragment() {

    private lateinit var mBinding: DoctorDetailsDialogLayoutBinding
    private val _tag = "DoctorDetailsBottomDial_DP"

    companion object {
        const val TAG = "DoctorDetailsBottomDialog_DP"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DoctorDetailsDialogLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.doctorDetailsDialogTxtShowDoctorPhone1.setOnClickListener {
            mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone1.visibility = View.GONE
            mBinding.doctorDetailsDialogLayoutVisibleDoctorPhone1.visibility = View.VISIBLE

            mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone2.visibility = View.VISIBLE
            mBinding.doctorDetailsDialogLayoutVisibleDoctorPhone2.visibility = View.GONE
        }

        mBinding.doctorDetailsDialogTxtShowDoctorPhone2.setOnClickListener {
            mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone2.visibility = View.GONE
            mBinding.doctorDetailsDialogLayoutVisibleDoctorPhone2.visibility = View.VISIBLE

            mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone1.visibility = View.VISIBLE
            mBinding.doctorDetailsDialogLayoutVisibleDoctorPhone1.visibility = View.GONE
        }

        mBinding.doctorDetailsDialogImgClose.setOnClickListener {
            dismiss()
        }

        mBinding.doctorDetailsDialogLblReportPhone1.setOnClickListener {
            val dialog = ReportDoctorPhoneDialog.newInstance(requireContext(), parentFragmentManager){ reason, note ->

            }
            dialog.show(parentFragmentManager, ReportDoctorPhoneDialog.TAG)
        }
        mBinding.doctorDetailsDialogLblReportPhone2.setOnClickListener {
            val dialog = ReportDoctorPhoneDialog.newInstance(requireContext(), parentFragmentManager){ reason, note ->

            }
            dialog.show(parentFragmentManager, ReportDoctorPhoneDialog.TAG)
        }
    }
}