package it.doctorphones.com.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import it.doctorphones.com.databinding.ChatInLayoutBinding
import it.doctorphones.com.databinding.ChatOutLayoutBinding
import it.doctorphones.com.databinding.DoctorPhoneItemLayoutBinding
import it.doctorphones.com.models.ChatMessage
import it.doctorphones.com.models.Doctor
import it.doctorphones.com.utils.IN_CHAT
import it.doctorphones.com.utils.Utils

class ChatForumRVAdapter(mContext: Context) : RecyclerArrayAdapter<ChatMessage>(mContext) {

    override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        if (viewType == IN_CHAT) {
            val binding = ChatInLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return ChatInViewHolder(mBinding = binding)
        } else {
            val binding = ChatOutLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return ChatOutViewHolder(mBinding = binding)
        }

    }

    override fun getViewType(position: Int): Int {
        return super.mObjects[position].type!!
    }

    class ChatInViewHolder(private val mBinding: ChatInLayoutBinding) :
        BaseViewHolder<ChatMessage>(mBinding.root) {

        @SuppressLint("SetTextI18n")
        override fun setData(message: ChatMessage) {
            with(mBinding) {
                chatItemTxtName.isVisible = !message.isContinue
                chatItemTxtNameDesc.isVisible = !message.isContinue
                chatItemAvatarView.isVisible = !message.isContinue
                chatItemTxtName.text = message.userName
                chatItemTxtNameDesc.text = message.userNameDesc
                chatItemAvatarView.avatarInitials = message.userName
                chatItemTxtContent.text = message.text
                chatItemTxtTimeAgo.text = Utils.getTime(message.date)
            }
        }
    }

    class ChatOutViewHolder(private val mBinding: ChatOutLayoutBinding) :
        BaseViewHolder<ChatMessage>(mBinding.root) {

        @SuppressLint("SetTextI18n")
        override fun setData(message: ChatMessage) {
            with(mBinding) {
                chatItemTxtContent.text = message.text
                chatItemTxtTimeAgo.text = Utils.getTime(message.date)
            }
        }
    }
}