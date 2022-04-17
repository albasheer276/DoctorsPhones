package it.doctorphones.com.models

import com.google.firebase.database.Exclude

data class ForumRequest(
    val id: String? = "",
    val userId: String? = "",
    var userName: String? = "",
    var userProfile: String? = "",
    val createdDate: String? = "",
    val province: String? = "",
    val specialization: String? = "",
    val doctorName: String? = "",
    val chatMessages: HashMap<String, ChatMessage>? = null,

    @Exclude @JvmField
    val notificationsCount: Int = 0,

    @Exclude @JvmField
    var commentsCount: Int = 0
) {
}
