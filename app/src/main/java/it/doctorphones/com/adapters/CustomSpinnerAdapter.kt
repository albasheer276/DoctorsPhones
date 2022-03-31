package it.doctorphones.com.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.doctorphones.com.databinding.SpinnerItemLayoutBinding

class CustomSpinnerAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
    private val onItemSelectListener: (position: Int, selectedString: String) -> Unit
) :
    RecyclerView.Adapter<CustomSpinnerAdapter.MyViewHolder>() {

    private lateinit var mBinding: SpinnerItemLayoutBinding

    class MyViewHolder(private val binding: SpinnerItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val layout: LinearLayout = binding.root

        fun bind(province: String) {
            with(binding) {
                txtItemName.text = province
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mBinding = SpinnerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
        holder.layout.setOnClickListener { onItemSelectListener(position, list[position]) }
    }

    override fun getItemCount(): Int = list.size
}