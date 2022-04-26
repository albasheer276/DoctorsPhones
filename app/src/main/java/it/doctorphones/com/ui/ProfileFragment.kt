package it.doctorphones.com.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import coil.transform.CircleCropTransformation
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.avatarview.coil.loadImage
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentProfileBinding
import it.doctorphones.com.models.User
import it.doctorphones.com.utils.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val _tag = "ProfileFragment_DP"

    private lateinit var mBinding: FragmentProfileBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

    @Inject
    lateinit var storage: StorageReference

    private var mIsFormEmpty = false
    private val mSharedPref by lazy { AppSharedPref(requireContext(), LOGIN_SHARED_DATA) }
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set a custom toolbar
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(mBinding.profileToolbar)

        // get username and profile
        database.child(USERS_TABLE).child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                if (user != null) {
                    with(mBinding) {
                        profileEdtName.setText(user.name)
                        profileEdtPhone.setText(Utils.formatNumber(user.phone, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
                        profileEdtPassword.setText(if ((user.password) == PASSWORD) "" else user.password)
                        profileEdtUser.setText(user.email?.split("@")?.get(0) ?: "")
                        profileAvatarView.avatarInitials = user.name

                        // Use Uri object instead of File to avoid storage permissions
                        if(user.profile != null){
                            profileAvatarView.avatarInitials = null
                            profileAvatarView.loadImage(
                                data = user.profile
                            ) {
                                crossfade(true)
                                crossfade(300)
                                transformations(CircleCropTransformation())
                                lifecycle(requireActivity())
                            }
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        mBinding.profileBtnSave.setOnClickListener {
            mIsFormEmpty = false
            val edtName = mBinding.profileEdtName
            val txtName = mBinding.profileTxtName
            val edtPhone = mBinding.profileEdtPhone
            val txtPhone = mBinding.profileTxtPhone
            val edtUser = mBinding.profileEdtUser
            val txtUser = mBinding.profileTxtUser
            val edtPassword = mBinding.profileEdtPassword
            val txtPassword = mBinding.profileTxtPassword

            checkEditTextEmpty(edtName, txtName, R.string.required_field)
            checkEditTextEmpty(edtPhone, txtPhone, R.string.required_field)
            //checkEditTextEmpty(edtUser, txtUser, R.string.required_field)
            checkEditTextEmpty(edtPassword, txtPassword, R.string.required_field)
            Log.d(_tag, "onViewCreated: step 0")
            if (mIsFormEmpty) {
                return@setOnClickListener
            }

            //check validation (phone Format, Correct Names, etc...)
            if (!Utils.isValidPhoneNumber(edtPhone.text.toString())) {
                txtPhone.text = getString(R.string.phone_invalid)
                txtPhone.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                txtPhone.visibility = View.GONE
            }

            if (edtPassword.text.length < 8) {
                txtPassword.text = getString(R.string.password_weak)
                txtPassword.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                txtPassword.visibility = View.GONE
            }

            val phone = "user_${Utils.formatNumber(edtPhone.text.toString(), PhoneNumberUtil.PhoneNumberFormat.NATIONAL)}@docphones.com"
            val password = edtPassword.text.toString()

            if (imageUri != null) {
                Log.d(_tag, "onViewCreated: $imageUri")
                val ref: StorageReference = storage.child("users/" + UUID.randomUUID().toString())
                ref.putFile(imageUri!!).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val profile = task.result.toString()
                        database.child(USERS_TABLE).child(auth.currentUser!!.uid).child("profile").setValue(profile)
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }

            auth.currentUser!!.updateEmail(phone).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(_tag, "onViewCreated: step 1")
                    auth.currentUser!!.updatePassword(password).addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            Log.d(_tag, "onViewCreated: step 2")
                            val user = User(
                                id = auth.currentUser!!.uid,
                                name = edtName.text.toString(),
                                email = auth.currentUser!!.email,
                                phone = Utils.formatNumber(edtPhone.text.toString(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
                                password = password
                            )
                            database.child(USERS_TABLE).child(auth.currentUser!!.uid).setValue(user)

                            // update shared preference
                            mSharedPref.saveData(USER_NAME, user.name.toString())
                            mSharedPref.saveData(USER_PROFILE, user.profile.toString())
                        } else {
                            Log.d(_tag, "onViewCreated: fail 2")
                        }
                    }
                } else {
                    Log.d(_tag, "onViewCreated: ${task.result}")
                }
            }
        }

        mBinding.profileLblEditImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                imageUri = data?.data!!
                try {
                    // Use Uri object instead of File to avoid storage permissions
                    mBinding.profileAvatarView.avatarInitials = null
                    mBinding.profileAvatarView.loadImage(
                        data = imageUri
                    ) {
                        crossfade(true)
                        crossfade(300)
                        transformations(CircleCropTransformation())
                        lifecycle(viewLifecycleOwner)
                    }
                } catch (e: Exception) {
                    Log.d(_tag, "onActivityResult: $e")
                }

            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }

            else -> {
                false
            }
        }
    }
}