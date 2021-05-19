package uz.creater.pdpgramm.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.creater.pdpgramm.R
import uz.creater.pdpgramm.adapters.GroupChatsAdapter
import uz.creater.pdpgramm.databinding.FragmentMessageGroupsBinding
import uz.creater.pdpgramm.databinding.InfoDialogBinding
import uz.creater.pdpgramm.databinding.SelectDialogBinding
import uz.creater.pdpgramm.models.ChatMessage
import uz.creater.pdpgramm.models.Group
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageGroupsFragment : Fragment() {

    private lateinit var binding: FragmentMessageGroupsBinding
    private lateinit var groupKey: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceMessage: DatabaseReference
    private lateinit var referenceGroup: DatabaseReference
    private lateinit var group: Group
    private var messageList: ArrayList<ChatMessage> = ArrayList()
    private lateinit var groupChatsAdapter: GroupChatsAdapter
    private var lastMessageKey = ""
    private lateinit var referenceLast: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            groupKey = it?.getString("groupKey").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageGroupsBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceMessage = firebaseDatabase.getReference("groupMessages")
        referenceGroup = firebaseDatabase.getReference("groups")
        referenceLast = firebaseDatabase.getReference("lastGroupMessages")
        referenceGroup.child(groupKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                group = snapshot.getValue(Group::class.java)!!
                binding.groupName.text = group.name

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        messageList = ArrayList()
        groupChatsAdapter = GroupChatsAdapter(messageList)
        binding.messageGroupsRv.adapter = groupChatsAdapter
        referenceMessage.child(groupKey).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                val children = snapshot.children
                for (child in children) {
                    val chatMessage = child.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        messageList.add(chatMessage)
                    }
                }
                groupChatsAdapter.notifyDataSetChanged()
                binding.messageGroupsRv.smoothScrollToPosition(messageList.size)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.infoImg.setOnClickListener {
            if (this::group.isInitialized) {
                val alertDialog = context?.let { AlertDialog.Builder(it) }
                alertDialog?.setCancelable(false)
                val dialog = alertDialog!!.create()
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                val dialogView: View = layoutInflater.inflate(
                    R.layout.info_dialog,
                    null,
                    false
                )
                dialog.setView(dialogView)
                val bindDialog = InfoDialogBinding.bind(dialogView)
                bindDialog.groupName.text = group.name
                bindDialog.groupInfo.text = group.desc
                bindDialog.okTv.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.addMessage.setOnClickListener {
            val m = binding.messageEdt.text.toString()
            if (m.isNotBlank()) {
                binding.messageEdt.setText("")
                val simpleDateFormat = SimpleDateFormat("HH:mm")
                val date = simpleDateFormat.format(Date())
                val key = referenceMessage.push().key
                val chatMessage = ChatMessage(m, date, firebaseAuth.uid)
                referenceMessage.child(groupKey).child(key!!)
                    .setValue(chatMessage)
            } else {
                Toast.makeText(requireContext(), "Your message is blank!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val lastQuery = referenceMessage.child(groupKey).limitToLast(1)
        lastQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    var key = it.key
                    if (key != null) {
                        lastMessageKey = key
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return binding.root
    }

    override fun onStop() {
        if (lastMessageKey.isNotBlank()) {
            referenceLast.child(groupKey).child(firebaseAuth.currentUser!!.uid)
                .setValue(lastMessageKey)
        }
        super.onStop()
    }
}