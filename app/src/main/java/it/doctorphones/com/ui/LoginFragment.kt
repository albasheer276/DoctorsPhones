package it.doctorphones.com.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentLoginBinding
import it.doctorphones.com.models.User
import it.doctorphones.com.dialogs.AppAlertDialog
import it.doctorphones.com.utils.PASSWORD
import it.doctorphones.com.utils.USERS_TABLE
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val _tag = "LoginFragment_DP"

    private lateinit var mBinding: FragmentLoginBinding
    private lateinit var mId: String
    private lateinit var mDialog: AppAlertDialog

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // open main fragment
            openMainScreen()
        }
        // Fetching Android ID
        mId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        mDialog = AppAlertDialog(requireContext())
        mBinding = FragmentLoginBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding) {
            loginBtnLogin.setOnClickListener {
                mDialog.loadingAlert("تسجيل الدخول", "يرجى الانتظار...")
                loginBtnLogin.isEnabled = false
                activity?.let { activity ->
                    val email = "user_$mId@docPhones.com"
                    auth.signInWithEmailAndPassword(email, PASSWORD)
                        .addOnCompleteListener(activity) { signIn ->
                            if (signIn.isSuccessful) {
                                saveUser()
                            } else {
                                auth.createUserWithEmailAndPassword(email, PASSWORD)
                                    .addOnCompleteListener(activity) { task ->
                                        if (task.isSuccessful) {
                                            saveUser()
                                        } else {
                                            Log.w(_tag, "signInAnonymously:failure", task.exception)
                                            Snackbar.make(mBinding.root, "حصل خطأ", Snackbar.LENGTH_LONG).show()
                                        }
                                    }
                            }
                            loginBtnLogin.isEnabled = true
                        }

                }
            }
        }
    }

    private fun FragmentLoginBinding.saveUser() {
        if (auth.currentUser != null) {
            val user = User(
                id = auth.currentUser!!.uid,
                name = loginEdtName.text.toString(),
                email = auth.currentUser!!.email,
                password = PASSWORD
            )
            user.id?.let { userId -> database.child(USERS_TABLE).child(userId).setValue(user) }

            // open main fragment
            openMainScreen()
            mDialog.dismiss()
        }
    }

    private fun openMainScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }
}