package it.doctorphones.com.dialogs

import android.R
import android.hardware.SensorManager.getOrientation
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.doctorphones.com.adapters.CustomSpinnerAdapter
import it.doctorphones.com.databinding.SpinnerDialogLayoutBinding


class SpinnerDialogFragment(
    private val list: ArrayList<String>,
    private val title: String,
    private val onItemSelectListener: (position: Int, selectedString: String) -> Unit,
    private val hideSearch: Boolean
) : DialogFragment(), TextWatcher {
    private lateinit var mBinding: SpinnerDialogLayoutBinding
    private lateinit var mSpinnerAdapter: CustomSpinnerAdapter

    companion object {
        const val TAG = "SpinnerDialogFragment_DP"
        fun newInstance(
            list: ArrayList<String>, title: String,
            onItemSelectListener: (position: Int, selectedString: String) -> Unit,
            hideSearch: Boolean = false
        ): SpinnerDialogFragment {
            return SpinnerDialogFragment(list, title, onItemSelectListener, hideSearch)
        }
    }

    /**
     * On start.
     * set the width and height of the dialog
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = SpinnerDialogLayoutBinding.inflate(inflater)
        mBinding.spinnerDialogTxtTitle.text = title
        initRecyclerView()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filter(null)
        if (hideSearch) {
            mBinding.spinnerDialogEdtSearch.visibility = View.GONE
        }
        mBinding.spinnerDialogImgClose.setOnClickListener {
            dismiss()
        }
        mBinding.spinnerDialogEdtSearch.addTextChangedListener(this)
    }

    private fun initRecyclerView() {
        mBinding.spinnerDialogRvItems.apply {
            mSpinnerAdapter = CustomSpinnerAdapter(context, list) { position, selectedString ->
                onItemSelectListener(position, selectedString)
                dismiss()
            }
            adapter = mSpinnerAdapter
            layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(query: Editable?) {
        filter(query.toString())
    }

    private fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            mSpinnerAdapter.setList(list)
        } else {
            mSpinnerAdapter.setList(list.filter {
                it.contains(query, true) ?: false
            } as ArrayList<String>)
        }
    }
}