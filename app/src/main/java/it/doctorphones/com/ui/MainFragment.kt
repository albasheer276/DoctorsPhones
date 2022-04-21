package it.doctorphones.com.ui

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentMainBinding
import it.doctorphones.com.dialogs.AppAlertDialog
import it.doctorphones.com.models.User
import it.doctorphones.com.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var mDialog: AppAlertDialog
    private lateinit var mNavController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference


    private val mSharedPref by lazy { AppSharedPref(requireContext(), LOGIN_SHARED_DATA) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // handle the back pressed action, to close the app, and do not open the splash screen again
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                    return
                }
                requireActivity().finish()
            }
        })
    }

    companion object {
        private lateinit var mBinding: FragmentMainBinding
        fun isDrawerOpen() =
            mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)

        fun closeDrawer() {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(layoutInflater)
        mDialog = AppAlertDialog(requireContext())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mDrawer = mBinding.drawerLayout
        mBinding.contentMain.contentMainImgOpenDrawer.setOnClickListener {
            mDrawer.openDrawer(GravityCompat.END)
        }

        val activity = (activity as AppCompatActivity)
        // set the support action to the toolbar view that added to the fragment
        activity.setSupportActionBar(mBinding.contentMain.contentMainToolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // add the main_menu to the toolbar
        //mBinding.contentMain.toolbar.inflateMenu(menu.main_menu)

        // get the navController from the fragment container view
        mNavController = activity.findNavController(R.id.contentMain_navHostFragment)
        // add the required fragments in the fragment container
        // the fragments IDs are the same as the menu items IDs
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menuForum,
                R.id.menuViewDoctorPhones
            )
        )
        // setup the action bar for the nav controller
        activity.setupActionBarWithNavController(mNavController, appBarConfiguration)
        // make the bottom navigation works with the nav controller
        mBinding.contentMain.contentMainBottomNavView.setupWithNavController(mNavController)

        // get username and profile
        database.child(USERS_TABLE).child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                if (user != null) {

                    mSharedPref.saveData(USER_ID, user.id!!)
                    mSharedPref.saveData(USER_NAME, user.name!!)
                    mSharedPref.saveData(USER_PROFILE, user.profile!!)

                    mBinding.drawerTopLayout.drawerTxtUserName.text = user.name
                    mBinding.drawerTopLayout.drawerAvatarView.avatarInitials = user.name
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        with(mBinding) {
            drawerLogout.setOnClickListener {
                mDialog.showConfirmCancelMessage("تسجيل الخروج", "هل تريد تسجيل الخروج من التطبيق؟") {
                    mSharedPref.clearAllData()
                    auth.signOut()
                    findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                    mDialog.dismiss()
                }
            }

            drawerAboutUs.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, AboutUsFragment())
                    .addToBackStack(null)
                    .commit()
                closeDrawer()
            }

            drawerContactUs.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, ContactUsFragment())
                    .addToBackStack(null)
                    .commit()
                closeDrawer()
            }

            drawerPersonalAccount.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, ProfileFragment())
                    .addToBackStack(null)
                    .commit()
                closeDrawer()
            }
        }
    }


}