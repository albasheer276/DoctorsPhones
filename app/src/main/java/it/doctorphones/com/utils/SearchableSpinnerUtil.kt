package it.doctorphones.com.utils

import androidx.fragment.app.FragmentManager
import it.doctorphones.com.dialogs.SpinnerDialogFragment

object SearchableSpinnerUtil {

    private val _log = "SearchableSpinnerUtil_DP"

    fun setupSearchableSpinner(
        fragmentManager: FragmentManager,
        items: ArrayList<String>,
        title: String,
        onItemSelectListener: (position: Int, selectedString: String) -> Unit
    ) {
        val dialog = SpinnerDialogFragment.newInstance(items, title, onItemSelectListener)
        dialog.show(fragmentManager, SpinnerDialogFragment.TAG)
    }
}