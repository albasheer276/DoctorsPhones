package it.doctorphones.com.models

data class ForumRequest(
    val id: String? = "",
    val userId: String? = "",
    val userName: String? = "بشير قيس",
    val createdDate: String? = "",
    val province: String? = "",
    val specialization: String? = "",
    val doctorName: String? = "",
    val notificationsCount: Int = 0,
    val commentsCount: Int = 0
) {
}
