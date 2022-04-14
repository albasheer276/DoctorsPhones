package it.doctorphones.com.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.databinding.FragmentForumChatBinding
import it.doctorphones.com.models.ChatMessage
import it.doctorphones.com.models.ForumRequest
import it.doctorphones.com.utils.CHATS_TABLE
import it.doctorphones.com.utils.DOCTORS_TABLE
import it.doctorphones.com.utils.FORUMS_TABLE
import it.doctorphones.com.utils.Utils
import kotlinx.coroutines.flow.emptyFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ForumChatFragment(private val request: ForumRequest) : Fragment() {
    private val _tag = "ForumChatFragment_DP"

    private lateinit var mBinding: FragmentForumChatBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

    private lateinit var edtMessage: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentForumChatBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set a custom toolbar
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(mBinding.forumChatToolbar)
        mBinding.forumChatToolbar.inflateMenu(R.menu.chat_menu)
        edtMessage = mBinding.forumChatEdtMessage

        with(mBinding) {
            forumItemAvatarView.avatarInitials = request.userName
            forumItemTxtName.text = request.userName
            forumItemTxtTimeAgo.text = Utils.getTimeAgo(request.createdDate)
            forumItemTxtTitle.text = "أريد رقم " + request.doctorName + if (request.province != "0") " (${request.province})" else ""
            forumItemTxtSpecialization.text = if (request.specialization != "0") request.specialization else "لم يتم ذكر التخصص"
            forumItemTxtBellsCount.text = request.notificationsCount.toString()
            forumItemTxtCommentsCount.text = request.commentsCount.toString()
        }

        mBinding.forumChatBtnSend.setOnClickListener {
            if (edtMessage.text.isNotBlank()) {
                val key = database.child(CHATS_TABLE).child(request.id!!).push().key
                if (key == null) {
                    Log.w(_tag, "Couldn't get push key for $DOCTORS_TABLE")
                    return@setOnClickListener
                }
                val message = ChatMessage(
                    id = key,
                    userId = auth.currentUser!!.uid,
                    date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    text = edtMessage.text.toString()
                )
                database.child(CHATS_TABLE).child(request.id).child(key).setValue(message)

                edtMessage.setText("")
                loadChats()
            }
        }
    }

    private fun loadChats() {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.chatMenu_deleteChat -> {
                Log.d(_tag, "onOptionsItemSelected: Delete")
                true
            }
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }

            else -> {
                false
            }
        }
    }
}