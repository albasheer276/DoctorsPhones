package it.doctorphones.com.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.installations.Utils
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import it.doctorphones.com.databinding.DoctorPhoneItemLayoutBinding
import it.doctorphones.com.models.Doctor

class ViewDoctorPhonesRVAdapter(mContext: Context, private val clickListener: (Doctor) -> Unit) : RecyclerArrayAdapter<Doctor>(mContext) {

    override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = DoctorPhoneItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(mBinding = binding, clickListener = clickListener)
    }

    class MyViewHolder(private val mBinding: DoctorPhoneItemLayoutBinding, private val clickListener: (Doctor) -> Unit) :
        BaseViewHolder<Doctor>(mBinding.root) {

        @SuppressLint("SetTextI18n")
        override fun setData(doctor: Doctor) {
            with(mBinding) {
                docPhoneTxtDoctorName.text = doctor.name
                docPhoneTxtDoctorSpecialize.text = doctor.specialization
                docPhoneTxtDoctorAddress.text = "${doctor.province} - ${doctor.city} - ${doctor.street} - ${doctor.building}"
                root.setOnClickListener {
                    clickListener(doctor)
                }
            }
        }
    }
}