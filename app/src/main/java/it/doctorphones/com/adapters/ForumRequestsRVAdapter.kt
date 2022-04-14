package it.doctorphones.com.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import it.doctorphones.com.databinding.ForumItemLayoutBinding
import it.doctorphones.com.models.ForumRequest
import it.doctorphones.com.utils.Utils

class ForumRequestsRVAdapter(mContext: Context, private val clickListener: (ForumRequest) -> Unit) : RecyclerArrayAdapter<ForumRequest>(mContext) {

    override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = ForumItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(mBinding = binding, clickListener = clickListener)
    }

    class MyViewHolder(private val mBinding: ForumItemLayoutBinding, private val clickListener: (ForumRequest) -> Unit) :
        BaseViewHolder<ForumRequest>(mBinding.root) {

        @SuppressLint("SetTextI18n")
        override fun setData(request: ForumRequest) {
            with(mBinding) {
                forumItemAvatarView.avatarInitials = request.userName
                forumItemTxtName.text = request.userName
                forumItemTxtTimeAgo.text = Utils.getTimeAgo(request.createdDate)
                forumItemTxtTitle.text = "أريد رقم " + request.doctorName + if (request.province != "0") " (${request.province})" else ""
                forumItemTxtSpecialization.text = if (request.specialization != "0") request.specialization else "لم يتم ذكر التخصص"
                forumItemTxtBellsCount.text = request.notificationsCount.toString()
                forumItemTxtCommentsCount.text = request.commentsCount.toString()
                forumItemLayout.setOnClickListener {
                    clickListener(request)
                }
            }
        }
    }
}