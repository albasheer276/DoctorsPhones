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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // handle the back pressed action, to close the app, and do not open the splash screen again
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mDrawer = mBinding.drawerLayout
        mBinding.contentMain.contentMainImgOpenDrawer.setOnClickListener {
            mDrawer.openDrawer(GravityCompat.END)
        }

        val activity = (activity as AppCompatActivity)
        // set the support action to the toolbar view that added to the fragemnt
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
                R.id.menuViewDoctorPhones,
                R.id.menuAddDoctor
            )
        )
        // setup the action bar for the nav controller
        activity.setupActionBarWithNavController(mNavController, appBarConfiguration)
        // make the bottom navigation works with the nav controller
        mBinding.contentMain.contentMainBottomNavView.setupWithNavController(mNavController)
    }


}