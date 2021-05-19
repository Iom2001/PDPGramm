package uz.creater.pdpgramm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import uz.creater.pdpgramm.databinding.ItemMyMessageBinding
import uz.creater.pdpgramm.databinding.ItemYourMessageBinding
import uz.creater.pdpgramm.models.ChatMessage
import uz.creater.pdpgramm.models.User
import kotlin.random.Random

class GroupChatsAdapter(var messageList: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //    var random = Random(256)
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val reference = FirebaseDatabase.getInstance().getReference("users")
//    var oldUserUid = ""
//    var oldColor: Int = 0


    inner class FromVh(var binding: ItemYourMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(chatMessage: ChatMessage) {
//            var color: Int = 0
//            if (oldUserUid == chatMessage.sentUserUid) {
//                color = oldColor
//            } else {
//                while (true) {
//                    color = Color.argb(
//                        255,
//                        random.nextInt(150, 256),
//                        random.nextInt(150, 256),
//                        random.nextInt(150, 256)
//                    )
//                    if (color != oldColor) {
//                        oldColor = color
//                        break
//                    }
//                }
//            }
//            binding.messageLayout.setBackgroundColor(color)
            binding.message.text = chatMessage.message
            binding.timeTv.text = chatMessage.date
            chatMessage.sentUserUid?.let {
                reference.child(it).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        Picasso.get().load(user?.photoUrl).into(binding.userImg)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
//            oldUserUid = chatMessage.sentUserUid!!
        }
    }

    inner class ToVh(var itemMyBinding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(itemMyBinding.root) {
        fun onBind(chatMessage: ChatMessage) {
            itemMyBinding.message.text = chatMessage.message
            itemMyBinding.timeTv.text = chatMessage.date
            Picasso.get().load(currentUser?.photoUrl).into(itemMyBinding.userImg)
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
            toVh.onBind(messageList[position])
        } else {
            val fromVh = holder as FromVh
            fromVh.onBind(messageList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (messageList[position].sentUserUid == currentUser?.uid) {
            return 1
        }
        return 2
    }

    override fun getItemCount(): Int = messageList.size
}