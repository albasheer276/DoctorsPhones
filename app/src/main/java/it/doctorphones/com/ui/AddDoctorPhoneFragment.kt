package it.doctorphones.com.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.SyncStateContract.Helpers.update
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.adapters.DoctorNameAutoCompleteAdapter
import it.doctorphones.com.databinding.FragmentAddDoctorPhoneBinding
import it.doctorphones.com.dialogs.AppAlertDialog
import it.doctorphones.com.models.Doctor
import it.doctorphones.com.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddDoctorPhoneFragment : Fragment() {
    private val _tag = "AddDoctorPhoneFragment_DP"

    private lateinit var mBinding: FragmentAddDoctorPhoneBinding
    private lateinit var mSelectedEdtText: EditText
    private lateinit var mDialog: AppAlertDialog

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

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
                    mSelectedEdtText.setText(phone)
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
                mDialog.showConfirmCancelMessage("جهات الاتصال", "لابد من اعطاء الصلاحية للتطبيق بقراءة جهات الاتصال") {
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
            mSelectedEdtText = mBinding.addDoctorEdtDoctorPhone1
        }
        if (it.id == mBinding.addDoctorImgDoctorPhone2.id) {
            mSelectedEdtText = mBinding.addDoctorEdtDoctorPhone2
        }
    }

    private var mIsFormEmpty = false
    private val doctors = ArrayList<Doctor>()
    private var mSelectedDoctor: Doctor? = null

    private lateinit var edtDoctorName: AutoCompleteTextView
    private lateinit var txtDoctorName: TextView
    private lateinit var edtDoctorPhone1: EditText
    private lateinit var txtDoctorPhone1: TextView
    private lateinit var imgDoctorPhone1: ImageView
    private lateinit var edtDoctorPhone2: EditText
    private lateinit var txtDoctorPhone2: TextView
    private lateinit var imgDoctorPhone2: ImageView
    private lateinit var layoutDoctorProvince: LinearLayout
    private lateinit var edtDoctorProvince: TextView
    private lateinit var txtDoctorProvince: TextView
    private lateinit var layoutDoctorSpecialize: LinearLayout
    private lateinit var edtDoctorSpecialize: TextView
    private lateinit var txtDoctorSpecialize: TextView
    private lateinit var edtDoctorCity: EditText
    private lateinit var txtDoctorCity: TextView
    private lateinit var edtDoctorStreet: EditText
    private lateinit var txtDoctorStreet: TextView
    private lateinit var edtDoctorBuilding: EditText
    private lateinit var txtDoctorBuilding: TextView
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
        // get all values
        edtDoctorName = mBinding.addDoctorEdtDoctorName
        txtDoctorName = mBinding.addDoctorTxtDoctorName
        edtDoctorPhone1 = mBinding.addDoctorEdtDoctorPhone1
        txtDoctorPhone1 = mBinding.addDoctorTxtDoctorPhone1
        imgDoctorPhone1 = mBinding.addDoctorImgDoctorPhone1
        edtDoctorPhone2 = mBinding.addDoctorEdtDoctorPhone2
        txtDoctorPhone2 = mBinding.addDoctorTxtDoctorPhone2
        imgDoctorPhone2 = mBinding.addDoctorImgDoctorPhone2
        layoutDoctorProvince = spinnerBinding.spinnerProvinces
        edtDoctorProvince = spinnerBinding.spinnerProvincesTxtValue
        txtDoctorProvince = spinnerBinding.spinnerProvincesAlertValue
        layoutDoctorSpecialize = spinnerBinding.spinnerSpecializations
        edtDoctorSpecialize = spinnerBinding.spinnerSpecializationsTxtValue
        txtDoctorSpecialize = spinnerBinding.spinnerSpecializationsAlertValue
        edtDoctorCity = mBinding.addDoctorEdtDoctorCity
        txtDoctorCity = mBinding.addDoctorTxtDoctorCity
        edtDoctorStreet = mBinding.addDoctorEdtDoctorStreet
        txtDoctorStreet = mBinding.addDoctorTxtDoctorStreet
        edtDoctorBuilding = mBinding.addDoctorEdtDoctorBuilding
        txtDoctorBuilding = mBinding.addDoctorTxtDoctorBuilding

        spinnerBinding.spinnerProvinces.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                provinces,
                "المحافظات",
            ) { index, selectedString ->
                edtDoctorProvince.text = selectedString
                edtDoctorProvince.tag = index
                txtDoctorProvince.visibility = View.GONE
            }
        }

        spinnerBinding.spinnerSpecializations.setOnClickListener {
            SearchableSpinnerUtil.setupSearchableSpinner(
                parentFragmentManager,
                specializations,
                "التخصصات",
            ) { index, selectedString ->
                edtDoctorSpecialize.text = selectedString
                edtDoctorSpecialize.tag = index
                txtDoctorSpecialize.visibility = View.GONE
            }
        }

        imgDoctorPhone1.setOnClickListener(contactClickListener)
        imgDoctorPhone2.setOnClickListener(contactClickListener)

        database.child(DOCTORS_TABLE).addValueEventListener(object : ValueEventListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDataChange(snapshot: DataSnapshot) {
                doctors.clear()
                for (postSnapshot in snapshot.children) {
                    val doctor = postSnapshot.getValue<Doctor>()
                    if (doctor != null) {
                        doctors.add(doctor)
                    }
                }

                // set autocomplete
                val adapter = DoctorNameAutoCompleteAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1, doctors,
                    mBinding
                )
                edtDoctorName.setAdapter(adapter)
                edtDoctorName.threshold = 1
                edtDoctorName.setOnItemClickListener { _, _, i, _ ->
                    mSelectedDoctor = adapter.getItem(i)
                    edtDoctorName.setText(mSelectedDoctor?.name)
                    edtDoctorPhone1.setText(Utils.formatNumber(mSelectedDoctor?.phone1, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
                    edtDoctorPhone2.setText(Utils.formatNumber(mSelectedDoctor?.phone2, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
                    edtDoctorCity.setText(mSelectedDoctor?.city)
                    edtDoctorStreet.setText(mSelectedDoctor?.street)
                    edtDoctorBuilding.setText(mSelectedDoctor?.building)
                    edtDoctorProvince.text = mSelectedDoctor?.province
                    edtDoctorSpecialize.text = mSelectedDoctor?.specialization
                    edtDoctorProvince.tag = provinces.indexOf(mSelectedDoctor?.province)
                    edtDoctorSpecialize.tag = specializations.indexOf(mSelectedDoctor?.specialization)

                    edtDoctorName.isEnabled = false
                    edtDoctorName.background = resources.getDrawable(R.drawable.rounded_bordered_disabled_bg, requireActivity().theme)

                    /*edtDoctorPhone1.isEnabled = false
                    imgDoctorPhone1.isEnabled = false
                    edtDoctorPhone1.background = resources.getDrawable(R.drawable.rounded_bordered_disabled_bg, requireActivity().theme)

                    if (mSelectedDoctor?.phone2?.length!! > 0) {
                        edtDoctorPhone2.isEnabled = false
                        imgDoctorPhone2.isEnabled = false
                        edtDoctorPhone2.background = resources.getDrawable(R.drawable.rounded_bordered_disabled_bg, requireActivity().theme)
                    }*/

                    mBinding.addDoctorBtnSave.text = getString(R.string.update)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        mBinding.addDoctorBtnSave.setOnClickListener {
            // check if empty
            mIsFormEmpty = false
            checkEditTextEmpty(edtDoctorName, txtDoctorName, R.string.required_field)
            checkEditTextEmpty(edtDoctorPhone1, txtDoctorPhone1, R.string.required_field)
            checkSpinnerEmpty(edtDoctorProvince, txtDoctorProvince, R.string.doctor_province_required)
            checkSpinnerEmpty(edtDoctorSpecialize, txtDoctorSpecialize, R.string.doctor_specialization_required)
            checkEditTextEmpty(edtDoctorCity, txtDoctorCity, R.string.required_field)
            checkEditTextEmpty(edtDoctorStreet, txtDoctorStreet, R.string.required_field)
            checkEditTextEmpty(edtDoctorBuilding, txtDoctorBuilding, R.string.required_field)
            if (mIsFormEmpty) {
                return@setOnClickListener
            }

            //check validation (phone Format, Correct Names, etc...)
            if(!Utils.isValidPhoneNumber(edtDoctorPhone1.text.toString())){
                txtDoctorPhone1.text = getString(R.string.phone_invalid)
                txtDoctorPhone1.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                txtDoctorPhone1.visibility = View.GONE
            }
            if(!Utils.isValidPhoneNumber(edtDoctorPhone2.text.toString())){
                txtDoctorPhone2.text = getString(R.string.phone_invalid)
                txtDoctorPhone2.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                txtDoctorPhone2.visibility = View.GONE
            }

            //TODO(check if phone numbers are already stored)

            //TODO(update the doctor details in the firebase if the doctor name is already founded in the firebase)

            //TODO(store the new doctor details)
            val key = database.child(DOCTORS_TABLE).push().key
            if (key == null) {
                Log.w(_tag, "Couldn't get push key for $DOCTORS_TABLE")
                return@setOnClickListener
            }
            val doctor = Doctor(
                id = key,
                name = edtDoctorName.text.toString(),
                phone1 = Utils.formatNumber(edtDoctorPhone1.text.toString(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
                phone2 = Utils.formatNumber(edtDoctorPhone2.text.toString(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
                province = edtDoctorProvince.text.toString(),
                specialization = edtDoctorSpecialize.text.toString(),
                province_specialization = "${edtDoctorProvince.text}_${edtDoctorSpecialize.text}",
                city = edtDoctorCity.text.toString(),
                street = edtDoctorStreet.text.toString(),
                building = edtDoctorBuilding.text.toString(),
                phone1ByUserId = auth.currentUser!!.uid,
                phone2ByUserId = if (edtDoctorPhone2.text.isNotBlank()) auth.currentUser!!.uid else "",
                createdByUserId = auth.currentUser!!.uid,
                createdDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )
            mDialog.loadingAlert("حفظ معلومات الطبيب", getString(R.string.please_wait))
            database.child(DOCTORS_TABLE).child(key).setValue(doctor).addOnSuccessListener {
                mDialog.showSuccessMessage("حفظ معلومات الطبيب", getString(R.string.success_message))
                // clear All Form Fields
                clearAllFormData()
            }.addOnFailureListener {
                mDialog.showErrorMessage("حفظ معلومات الطبيب", getString(R.string.error_occurred))
            }
        }

        mBinding.addDoctorBtnClearForm.setOnClickListener {
            clearAllFormData()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun clearAllFormData() {
        edtDoctorName.setText("")
        edtDoctorPhone1.setText("")
        edtDoctorPhone2.setText("")
        edtDoctorCity.setText("")
        edtDoctorStreet.setText("")
        edtDoctorBuilding.setText("")
        edtDoctorProvince.text = "المحافظة"
        edtDoctorSpecialize.text = "الاختصاص"
        edtDoctorProvince.tag = "-1"
        edtDoctorSpecialize.tag = "-1"

        edtDoctorName.isEnabled = true
        edtDoctorPhone1.isEnabled = true
        imgDoctorPhone1.isEnabled = true

        mSelectedDoctor = null
        edtDoctorName.background = resources.getDrawable(R.drawable.rounded_bordered_bg, requireActivity().theme)
        /*edtDoctorPhone1.background = resources.getDrawable(R.drawable.rounded_bordered_bg, requireActivity().theme)
        edtDoctorPhone2.background = resources.getDrawable(R.drawable.rounded_bordered_bg, requireActivity().theme)*/

        mBinding.addDoctorBtnSave.text = getString(R.string.add)
    }

    private fun checkSpinnerEmpty(edt: TextView, txt: TextView, errorMessageRes: Int): Boolean {
        return if (edt.tag.equals("-1")) {
            txt.visibility = View.VISIBLE
            txt.text = resources.getText(errorMessageRes)
            if (!mIsFormEmpty) mIsFormEmpty = true
            true
        } else {
            txt.visibility = View.GONE
            false
        }
    }

    private fun checkEditTextEmpty(edt: EditText, txt: TextView, errorMessageRes: Int): Boolean {
        edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                txt.visibility = View.GONE
            }
        })

        return if (edt.text.isBlank()) {
            txt.visibility = View.VISIBLE
            txt.text = resources.getText(errorMessageRes)
            if (!mIsFormEmpty) mIsFormEmpty = true
            true
        } else {
            txt.visibility = View.GONE
            false
        }

    }
}