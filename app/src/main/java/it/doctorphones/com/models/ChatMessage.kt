package it.doctorphones.com.models

data class ChatMessage(
    val id: String? = "",
    val text: String? = "",
    val userId: String? = "",
    val userName: String? = "",
    var userNameDesc: String? = "",
    val userProfile: String? = "",
    val date: String? = "",
    var type: Int? = null,
    var isContinue:Boolean = false
)
