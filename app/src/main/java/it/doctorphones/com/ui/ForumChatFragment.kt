package it.doctorphones.com.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.AndroidEntryPoint
import it.doctorphones.com.R
import it.doctorphones.com.adapters.ChatForumRVAdapter
import it.doctorphones.com.adapters.ViewDoctorPhonesRVAdapter
import it.doctorphones.com.databinding.FragmentForumChatBinding
import it.doctorphones.com.dialogs.AppAlertDialog
import it.doctorphones.com.dialogs.DoctorDetailsBottomDialog
import it.doctorphones.com.managers.RtlGridLayoutManager
import it.doctorphones.com.models.ChatMessage
import it.doctorphones.com.models.ForumRequest
import it.doctorphones.com.models.User
import it.doctorphones.com.utils.*
import kotlinx.coroutines.flow.emptyFlow
import xyz.teamgravity.checkinternet.CheckInternet
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ForumChatFragment(private val request: ForumRequest) : Fragment() {
    private val _tag = "ForumChatFragment_DP"

    private lateinit var mBinding: FragmentForumChatBinding
    private lateinit var mDialog: AppAlertDialog
    private val messages = mutableListOf<ChatMessage>()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: DatabaseReference

    private lateinit var edtMessage: EditText
    private lateinit var mAdapter: ChatForumRVAdapter
    private val mSharedPref by lazy { AppSharedPref(requireContext(), LOGIN_SHARED_DATA) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.md_theme_light_secondary, requireActivity().theme)
        mBinding = FragmentForumChatBinding.inflate(inflater)
        mDialog = AppAlertDialog(requireContext())
        initRecyclerView()
        setHasOptionsMenu(true)
        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNewChats()

        // set a custom toolbar
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(mBinding.forumChatToolbar)
        mBinding.forumChatToolbar.inflateMenu(R.menu.chat_menu)
        edtMessage = mBinding.forumChatEdtMessage

        with(mBinding) {
            forumItemAvatarView.avatarInitials = request.userName
            forumItemTxtName.text = request.userName
            forumItemTxtTimeAgo.text = Utils.getTimeAgo(request.createdDate)
            forumItemTxtTitle.text = "???????? ?????? " + request.doctorName + if (request.province != "0") " (${request.province})" else ""
            forumItemTxtSpecialization.text = if (request.specialization != "0") request.specialization else "???? ?????? ?????? ????????????"
            forumItemTxtBellsCount.text = request.notificationsCount.toString()
            forumItemTxtCommentsCount.text = request.commentsCount.toString()
        }

        database.child(FORUMS_TABLE).child(request.id!!).child("chatMessages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.childrenCount >= 10) {
                        mBinding.forumChatChatLayout.isVisible = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        mBinding.forumChatBtnSend.setOnClickListener {
            // check for network
            CheckInternet().check { connected ->
                if (!connected) {
                    // there is no internet
                    mDialog.showErrorMessage("??????", "???? ???????? ?????????? ??????????????????!")
                    return@check
                }
                // there is no internet
                if (edtMessage.text.isNotBlank()) {
                    database.child(FORUMS_TABLE).child(request.id!!).child("chatMessages")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.childrenCount >= 10) {
                                    mDialog.showErrorMessage("??????", "?????? ???????? ?????????????? ?????? ???????? ?????????????? ???????????? ?????????? ?????????? ??????????")
                                } else {
                                    val key = database.child(FORUMS_TABLE).child(request.id).child("chatMessages").push().key
                                    if (key == null) {
                                        Log.w(_tag, "Couldn't get push key for $DOCTORS_TABLE")
                                        return
                                    }
                                    val message = ChatMessage(
                                        id = key,
                                        userId = mSharedPref.getString(USER_ID),
                                        userName = mSharedPref.getString(USER_NAME),
                                        userProfile = mSharedPref.getString(USER_PROFILE),
                                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                                        text = edtMessage.text.toString()
                                    )
                                    database.child(FORUMS_TABLE).child(request.id).child("chatMessages").child(key).setValue(message)

                                    edtMessage.setText("")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })

                }
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.forumChatRvMessages.apply {
            // check if the device is a tablet
            setLayoutManager(LinearLayoutManager(context))
            mAdapter = ChatForumRVAdapter(context)
            adapter = mAdapter
        }
    }

    private fun loadNewChats() {
        database.child(FORUMS_TABLE).child(request.id!!).child("chatMessages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mBinding.forumItemTxtCommentsCount.text = snapshot.childrenCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        database.child(FORUMS_TABLE).child(request.id).child("chatMessages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue<ChatMessage>()
                    if (message != null) {
                        message.type = if (message.userId == mSharedPref.getString(USER_ID)) OUT_CHAT else IN_CHAT
                        message.userNameDesc = if (message.userId == request.userId) "(???????? ??????????)" else ""
                        if (messages.size > 0) {
                            if (messages[messages.lastIndex].userId == message.userId) {
                                message.isContinue = true
                            }
                        }
                        mAdapter.add(message)
                        messages.add(message)
                        mBinding.forumChatRvMessages.scrollToPosition(mAdapter.count - 1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_menu, menu)
        menu.findItem(R.id.chatMenu_deleteChat).isVisible = mSharedPref.getString(USER_ID) == request.userId
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.chatMenu_deleteChat -> {
                mDialog.showConfirmCancelMessage("?????? ??????????", "???? ?????? ?????????? ???? ?????? ????????????"){
                    database.child(FORUMS_TABLE).child(request.id!!).removeValue { error, ref ->
                        mDialog.dismiss()
                        parentFragmentManager.popBackStack()
                    }
                }
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