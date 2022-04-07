package it.doctorphones.com.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.i18n.phonenumbers.PhoneNumberUtil
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentAddDoctorPhoneBinding
import it.doctorphones.com.dialogs.AppAlertDialog
import it.doctorphones.com.utils.SearchableSpinnerUtil
import it.doctorphones.com.utils.Utils
import it.doctorphones.com.utils.provinces
import it.doctorphones.com.utils.specializations


class AddDoctorPhoneFragment : Fragment() {

    private lateinit var mBinding: FragmentAddDoctorPhoneBinding
    private lateinit var mSelectedEdtPhone: EditText
    private lateinit var mDialog: AppAlertDialog

    private var resultContactLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                val contactUri = data.data
                val cursor = requireActivity().contentResolver.query(contactUri!!, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val column: Int = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phone = Utils.formatNumber(cursor.getString(column), PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
                    mSelectedEdtPhone.setText(phone)
                }
            }

        }
    }

    private val contactClickListener = View.OnClickListener {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        // Check if permission is granted or not
        ExcuseMe.couldYouGive(this).permissionFor(
            android.Manifest.permission.READ_CONTACTS,
        ) { permission ->
            if (permission.granted.contains(android.Manifest.permission.READ_CONTACTS)) {
                resultContactLauncher.launch(intent)
            } else {
                mDialog.showConfirmCancelMessage("جهات الاتصال", "لابد من اعطاء الصلاحية للتطبيق بقراءة جهات الاتصال"){
                    @Suppress("NAME_SHADOWING") val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    requireContext().startActivity(intent)
                }
            }
        }

        if (it.id == mBinding.addDoctorImgDoctorPhone1.id) {
            mSelectedEdtPhone = mBinding.addDoctorEdtDoctorPhone1
        }
        if (it.id == mBinding.addDoctorImgDoctorPhone2.id) {
            mSelectedEdtPhone = mBinding.addDoctorEdtDoctorPhone2
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddDoctorPhoneBinding.inflate(layoutInflater)
        mDialog = AppAlertDialog(requireContext())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerBinding = mBinding.addDoctorSpinnerLayout
        spinnerBinding.spinnerProvinces.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                provinces,
                "المحافظات",
            ) { _, selectedString ->
                spinnerBinding.spinnerProvincesTxtValue.text = selectedString
            }
        }

        spinnerBinding.spinnerSpecializations.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                specializations,
                "التخصصات",
            ) { _, selectedString ->
                spinnerBinding.spinnerSpecializationsTxtValue.text = selectedString
            }
        }

        mBinding.addDoctorImgDoctorPhone1.setOnClickListener(contactClickListener)
        mBinding.addDoctorImgDoctorPhone2.setOnClickListener(contactClickListener)
    }
}