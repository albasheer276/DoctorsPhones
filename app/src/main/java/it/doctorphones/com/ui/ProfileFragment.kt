package it.doctorphones.com.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentProfileBinding
import it.doctorphones.com.models.User
import it.doctorphones.com.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var mBinding: FragmentProfileBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

    private var mIsFormEmpty = false

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
                        profileEdtPhone.setText(user.phone)
                        profileEdtPassword.setText(if ((user.password) == PASSWORD) "" else user.password)
                        profileEdtUser.setText(user.email?.split("@")?.get(0) ?: "")
                        profileAvatarView.avatarInitials = user.name
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        mBinding.profileBtnSave.setOnClickListener {
            mIsFormEmpty = false
            val edtName = mBinding.profileEdtName
            val edtPhone = mBinding.profileEdtPhone
            val txtPhone = mBinding.profileTxtPhone
            val edtPassword = mBinding.profileEdtPassword
            val txtPassword = mBinding.profileTxtPassword

            checkEditTextEmpty(edtPhone, txtPhone, R.string.required_field)
            checkEditTextEmpty(edtPassword, txtPassword, R.string.required_field)
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

            val phone = "user_${edtPhone.text}@docphones.com"
            val password = edtPassword.text.toString()

            auth.currentUser!!.updateEmail(phone).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser!!.updatePassword(password).addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            val user = User(
                                id = auth.currentUser!!.uid,
                                name = edtName.text.toString(),
                                email = auth.currentUser!!.email,
                                password = password
                            )
                            database.child(USERS_TABLE).child(auth.currentUser!!.uid).setValue(user)
                        }
                    }
                }
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