package it.doctorphones.com.dialogs

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.i18n.phonenumbers.PhoneNumberUtil
import it.doctorphones.com.R
import it.doctorphones.com.databinding.DoctorDetailsDialogLayoutBinding
import it.doctorphones.com.models.Doctor
import it.doctorphones.com.utils.Utils


class DoctorDetailsBottomDialog(private val doctor: Doctor) : BottomSheetDialogFragment() {
    private val _tag = "DoctorDetailsBottomDial_DP"

    private lateinit var mBinding: DoctorDetailsDialogLayoutBinding

    companion object {
        const val TAG = "DoctorDetailsBottomDialog_DP"
    }

    override fun onStart() {
        super.onStart()
        BottomSheetBehavior.from(mBinding.root.parent as View).apply {
            //isFitToContents = false
            state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DoctorDetailsDialogLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.doctorDetailsDialogTxtDoctorName.text = doctor.name
        mBinding.doctorDetailsDialogTxtDoctorSpecialization.text = doctor.specialization
        mBinding.doctorDetailsDialogTxtDoctorAddress.text = "${doctor.province} - ${doctor.city} - ${doctor.street} - ${doctor.building}"
        mBinding.doctorDetailsDialogTxtDoctorPhone1.text =
            Utils.formatNumber(doctor.phone1, PhoneNumberUtil.PhoneNumberFormat.NATIONAL, withSpaces = true)
        mBinding.doctorDetailsDialogImgCopyPhone1.setOnClickListener {
            copyPhoneToClipBoard(Utils.formatNumber(doctor.phone1, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
        }
        mBinding.doctorDetailsDialogImgCallPhone1.setOnClickListener {
            callPhoneNumber(Utils.formatNumber(doctor.phone1, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
        }

        if (doctor.phone2?.isBlank() == true) {
            mBinding.doctorDetailsDialogLayoutDoctorPhone2.visibility = View.GONE
        } else {
            mBinding.doctorDetailsDialogTxtDoctorPhone2.text =
                Utils.formatNumber(doctor.phone2, PhoneNumberUtil.PhoneNumberFormat.NATIONAL, withSpaces = true)
            mBinding.doctorDetailsDialogImgCopyPhone2.setOnClickListener {
                copyPhoneToClipBoard(Utils.formatNumber(doctor.phone2, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
            }
            mBinding.doctorDetailsDialogImgCallPhone2.setOnClickListener {
                callPhoneNumber(Utils.formatNumber(doctor.phone2, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
            }
        }

        mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone1.setOnClickListener {
            mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone1.visibility = View.GONE
            mBinding.doctorDetailsDialogLayoutVisibleDoctorPhone1.visibility = View.VISIBLE
        }

        mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone2.setOnClickListener {
            mBinding.doctorDetailsDialogLayoutHiddenDoctorPhone2.visibility = View.GONE
            mBinding.doctorDetailsDialogLayoutVisibleDoctorPhone2.visibility = View.VISIBLE
        }

        mBinding.doctorDetailsDialogImgClose.setOnClickListener {
            dismiss()
        }

        mBinding.doctorDetailsDialogLblReportPhone1.setOnClickListener {
            val dialog = ReportDoctorPhoneDialog.newInstance(requireContext(), parentFragmentManager) { reason, note ->

            }
            dialog.show(parentFragmentManager, ReportDoctorPhoneDialog.TAG)
        }
        mBinding.doctorDetailsDialogLblReportPhone2.setOnClickListener {
            val dialog = ReportDoctorPhoneDialog.newInstance(requireContext(), parentFragmentManager) { reason, note ->

            }
            dialog.show(parentFragmentManager, ReportDoctorPhoneDialog.TAG)
        }
    }

    private fun callPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(dialIntent)
    }

    private fun copyPhoneToClipBoard(phoneNumber: String) {
        val clipboardManager: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            "doctor phone",
            phoneNumber
        )
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), getString(R.string.phone_number_copied), Toast.LENGTH_SHORT).show()
    }
}