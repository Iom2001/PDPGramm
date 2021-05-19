package uz.creater.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import uz.creater.pdpgramm.databinding.ItemMyMessageBinding
import uz.creater.pdpgramm.databinding.ItemYourMessageBinding
import uz.creater.pdpgramm.models.ChatMessage
import uz.creater.pdpgramm.models.User

class ChatsAdapter(var list: List<ChatMessage>, var toUser: User, var fromUser: User) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(var itemYourBinding: ItemYourMessageBinding) :
        RecyclerView.ViewHolder(itemYourBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemYourBinding.message.text = chatMessage.message
            itemYourBinding.timeTv.text = chatMessage.date
            Picasso.get().load(fromUser.photoUrl).into(itemYourBinding.userImg)
        }
    }

    inner class ToVh(var itemMyBinding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(itemMyBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemMyBinding.message.text = chatMessage.message
            itemMyBinding.timeTv.text = chatMessage.date
            Picasso.get().load(toUser.photoUrl).into(itemMyBinding.userImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return ToVh(
                ItemMyMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return FromVh(
            ItemYourMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        } else {
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].sentUserUid == toUser.uid) {
            return 1
        }
        return 2
    }

    override fun getItemCount(): Int = list.size
}