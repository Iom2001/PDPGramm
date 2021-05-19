package uz.creater.pdpgramm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import uz.creater.pdpgramm.databinding.ItemUserBinding
import uz.creater.pdpgramm.models.ChatMessage
import uz.creater.pdpgramm.models.CheckVisibility
import uz.creater.pdpgramm.models.User

class UserAdapter(var list: List<User>, var itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<UserAdapter.Vh>() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private var reference = firebaseDatabase.getReference("checkOnline")
    private val currentUser = FirebaseAuth.getInstance().currentUser
    val databaseReference = firebaseDatabase.getReference("messages")

    var referenceLast = firebaseDatabase.getReference("lastMessages")

    inner class Vh(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(user: User) {
            Picasso.get().load(user.photoUrl).into(binding.imgPerson)
            binding.name.text = user.name
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(user)
            }
            reference.child(user.uid!!).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Int::class.java)
                    if (value == 1) {
                        binding.onlineCard.visibility = View.VISIBLE
                    } else {
                        binding.onlineCard.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            var mainPath = ""
            var userUid = user.uid!!
            if (currentUser!!.uid.length > userUid.length) {
                mainPath = currentUser!!.uid + userUid
            } else if (currentUser!!.uid.length < userUid.length) {
                mainPath = userUid + currentUser!!.uid
            } else {
                for (i in 0..userUid.length) {
                    val c: Char = currentUser!!.uid.substring(i, i + 1).single()
                    val u = userUid.substring(i, i + 1).single()
                    val cCode: Int = c.code
                    val uCode: Int = u.code
                    when {
                        cCode > uCode -> {
                            mainPath = currentUser!!.uid + userUid
                            break
                        }
                        cCode < uCode -> {
                            mainPath = userUid + currentUser!!.uid
                            break
                        }
                        else -> {

                        }
                    }
                }
            }
            val lastQuery = databaseReference.child(mainPath).limitToLast(1)
            lastQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var a = true
                    dataSnapshot.children.forEach {
                        var message = it.getValue(ChatMessage::class.java)
                        if (message != null) {
                            a = false
                            binding.lastMassage.visibility = View.VISIBLE
                            binding.lastMassage.text = message.message
                            binding.time.text = message.date
                        }
                    }
                    if (a) {
                        binding.lastMassage.visibility = View.GONE
                        binding.time.text = ""
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            referenceLast.child(currentUser!!.uid).child(user.uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val key = snapshot.getValue(String::class.java)
                        databaseReference.child(mainPath)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val children = snapshot.children
                                    var value = 0
                                    var count = 0
                                    for (child in children) {
                                        val getKey = child.key
                                        count++
                                        if (key == getKey) {
                                            value = count
                                        }
                                    }
                                    var a = count - value
                                    if (CheckVisibility.isChat && a > 0) {
                                        binding.countCard.visibility = View.VISIBLE
                                        binding.countTv.text = (a).toString()
                                    } else {
                                        binding.countCard.visibility = View.GONE
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }


    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    private fun generateMainPath(userUid: String) {
    }
}