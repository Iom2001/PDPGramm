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
import uz.creater.pdpgramm.databinding.ItemGroupBinding
import uz.creater.pdpgramm.models.CheckVisibility
import uz.creater.pdpgramm.models.Group

class GroupAdapter(var list: List<Group>, var onClick: OnClick) :
    RecyclerView.Adapter<GroupAdapter.Vh>() {

    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private var referenceLast = firebaseDatabase.getReference("lastGroupMessages")
    private var referenceMessage = firebaseDatabase.getReference("groupMessages")

    inner class Vh(var binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(group: Group) {
            binding.name.text = group.name
            binding.root.setOnClickListener {
                onClick.onItemClick(group)
            }

            referenceLast.child(group.groupKey!!).child(currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val key = snapshot.getValue(String::class.java)
                        referenceMessage.child(group.groupKey!!)
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
                                    if (CheckVisibility.isGroup && a > 0) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnClick {
        fun onItemClick(group: Group)
    }
}