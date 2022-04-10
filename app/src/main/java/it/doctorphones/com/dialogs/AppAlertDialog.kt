package it.doctorphones.com.dialogs

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import it.doctorphones.com.R
import it.doctorphones.com.databinding.AppAlertDialogBinding

/**
 * App alert dialog.
 *
 * this class will create an alert dialog for the whole app
 *
 * @constructor Create [AppAlertDialog]
 *
 * @param context
 */
class AppAlertDialog(context: Context) : AlertDialog(context) {

    private var builder: AlertDialog? = null
    private val binding: AppAlertDialogBinding = AppAlertDialogBinding.inflate(this.layoutInflater)

    /**
     * the init procedure will create the Alert Dialog object
     * and set its main properties like the root view and the animation
     */
    init {
        builder = Builder(context).create()
        builder?.setView(binding.root)
        builder?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        builder?.setCancelable(false)
    }

    /**
     * Loading alert.
     * show the message in a SnackBar
     *
     * @param title Title
     * @param content Content
     */
    fun loadingAlert(title: String, content: String) {
        dismiss()
        with(binding) {
            alertDialogTxtAlertTitle.text = title
            alertDialogTxtAlertContent.text = content
            alertDialogProgressBar.visibility = View.VISIBLE
            setCancelable(false)
        }
        builder?.show()
    }

    /**
     * Show normal message.
     *
     * @param title Title
     * @param content Content
     */
    fun showNormalMessage(title: String, content: String?) {
        dismiss()
        with(binding) {
            alertDialogTxtAlertTitle.text = title
            alertDialogTxtAlertContent.text = content
            alertDialogBtnPositive.visibility = View.VISIBLE
            alertDialogBtnPositive.setOnClickListener { dismiss() }
        }
        builder?.show()
    }

    /**
     * Show confirm cancel message.
     *
     * @param title Title
     * @param content Content
     * @param confirmListener Confirm listener
     */
    fun showConfirmCancelMessage(
        title: String,
        content: String?,
        confirmListener: View.OnClickListener
    ) {
        dismiss()
        with(binding) {
            alertDialogTxtAlertTitle.text = title
            alertDialogTxtAlertContent.text = content
            alertDialogBtnPositive.visibility = View.VISIBLE
            alertDialogBtnPositive.text = context.getText(R.string.confirm)
            alertDialogBtnPositive.setOnClickListener(confirmListener)
            alertDialogBtnNegative.visibility = View.VISIBLE
            alertDialogBtnNegative.text = context.getText(R.string.cancel)
            alertDialogBtnNegative.setOnClickListener { dismiss() }
        }
        if(builder != null) {
            builder!!.show()
        }
    }

    /**
     * Dismiss.
     * this method will be called before any of other build dialogs, to ensure that the app show only on alert dialog
     */
    override fun dismiss() {
        if (builder != null && builder!!.isShowing) {
            with(binding) {
                alertDialogBtnPositive.visibility = View.GONE
                alertDialogBtnNegative.visibility = View.GONE
                alertDialogProgressBar.visibility = View.GONE
                alertDialogBtnNegative.text = context.getText(R.string.cancel)
            }
            builder?.dismiss()
        }
    }

    /**
     * Show error message.
     * this method show the message as a SnackBar
     *
     * @param title Title
     * @param content Content
     */
    fun showErrorMessage(title: String, content: String?) {
        dismiss()
        with(binding) {
            alertDialogTxtAlertTitle.text = title
            alertDialogTxtAlertContent.text = content
            alertDialogBtnNegative.visibility = View.VISIBLE
            alertDialogBtnNegative.text = context.getText(R.string.close)
            alertDialogBtnNegative.setOnClickListener { dismiss() }
        }
        if(builder != null) {
            builder!!.show()
        }
    }

    /**
     * Show success message in a SnackBar
     *
     * @param title Title
     * @param content Content
     */
    fun showSuccessMessage(title: String, content: String?) {
        dismiss()
        with(binding) {
            alertDialogTxtAlertTitle.text = title
            alertDialogTxtAlertContent.text = content
            alertDialogBtnPositive.visibility = View.VISIBLE
            alertDialogBtnPositive.setOnClickListener { dismiss() }
        }
        if(builder != null) {
            builder!!.show()
        }
    }

    /**
     * Show warning message in a SnackBar
     *
     * @param title Title
     * @param content Content
     */
    fun showWarningMessage(title: String, content: String?) {
        dismiss()
    }
}