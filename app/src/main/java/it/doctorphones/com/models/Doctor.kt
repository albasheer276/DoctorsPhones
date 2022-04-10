package it.doctorphones.com.models

data class Doctor(
    val id: String? = "",
    val name: String? = "",
    val phone1: String? = "",
    val phone1ByUserId: String? = "",
    val phone2: String? = "",
    val phone2ByUserId: String? = "",
    val province: String? = "",
    val specialization: String? = "",
    val province_specialization: String? = "",
    val city: String? = "",
    val street: String? = "",
    val building: String? = "",
    val lat: String? = "",
    val lng: String? = "",


    val createdByUserId: String? = "",
    val createdDate: String? = "",
    val updatedByUserIds: List<String>? = emptyList(),
    val updatedDate: String? = "",
    val updatedDetails: List<Doctor>? = emptyList(),
    val status: String? = "waiting"
) {
    override fun toString(): String {
        return "$name: $province - $specialization"
    }
}
