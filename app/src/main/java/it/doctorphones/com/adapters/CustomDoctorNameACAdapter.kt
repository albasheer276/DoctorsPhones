package it.doctorphones.com.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import it.doctorphones.com.models.Doctor

class CustomDoctorNameACAdapter(
    mContext: Context,
    mLayoutResourceId: Int,
    doctors: ArrayList<Doctor>
) :
    ArrayAdapter<Doctor>(mContext, mLayoutResourceId, doctors) {
    private val doctor: MutableList<Doctor> = ArrayList(doctors)
    private var allDoctors: List<Doctor> = ArrayList(doctors)

    override fun getCount(): Int {
        return doctor.size
    }

    override fun getItem(position: Int): Doctor {
        return doctor[position]
    }

    @SuppressLint("ViewHolder")
    @Suppress("NAME_SHADOWING")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //var convertView = layoutInflater.inflate(mLayoutResourceId, parent, false)
        return super.getView(position, convertView, parent)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as Doctor).name ?: ""
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val doctorSuggestion: MutableList<Doctor> = ArrayList()
                    for (city in allDoctors) {
                        if (city.name!!.contains(constraint.toString())) {
                            doctorSuggestion.add(city)
                        }
                    }
                    filterResults.values = doctorSuggestion
                    filterResults.count = doctorSuggestion.size
                }
                return filterResults
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                doctor.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is Doctor) {
                            doctor.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    doctor.addAll(allDoctors)
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}