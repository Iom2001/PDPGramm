package uz.creater.pdpgramm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import uz.creater.pdpgramm.adapters.ChatsAdapter
import uz.creater.pdpgramm.databinding.FragmentMessageChatsBinding
import uz.creater.pdpgramm.models.ChatMessage
import uz.creater.pdpgramm.models.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "userUid"

class MessageChatsFragment : Fragment() {

    private lateinit var userUid: String
    private lateinit var binding: FragmentMessageChatsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var referenceMessage: DatabaseReference
    private lateinit var referenceUser: DatabaseReference
    private lateinit var mainPath: String
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var user: User
    private lateinit var currentUser: User
    private lateinit var referenceLast: DatabaseReference
    private var lastMessageKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userUid = it.getString(ARG_PARAM1).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageChatsBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceMessage = firebaseDatabase.getReference("messages")
        referenceUser = firebaseDatabase.getReference("users")
        referenceLast = firebaseDatabase.getReference("lastMessages")
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
        referenceUser.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.name.text = user.name
                Picasso.get().load(user.photoUrl).into(binding.imgPerson)
                FirebaseDatabase.getInstance().getReference("checkOnline").child(user.uid!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.getValue(Int::class.java)
                            if (value == 1) {
                                binding.lastBe.text = "Online"
                            } else {
                                binding.lastBe.text = "Last seen recently"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                referenceUser.child(firebaseAuth.currentUser!!.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            currentUser = snapshot.getValue(User::class.java)!!
                            referenceMessage.child(mainPath)
                                .addValueEventListener(object : ValueEventListener {

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val list = ArrayList<ChatMessage>()
                                        val children = snapshot.children
                                        for (child in children) {
                                            val value = child.getValue(ChatMessage::class.java)
                                            if (value != null) {
                                                list.add(value)
                                            }
                                        }
                                        chatsAdapter = ChatsAdapter(list, currentUser, user)
                                        binding.messageChatsRv.adapter = chatsAdapter
//                                        chatsAdapter.registerAdapterDataObserver(
//                                            MyScrollToBottomObserver(binding.messageChatsRv, chatsAdapter, manager)
//                                        )
                                        binding.messageChatsRv.smoothScrollToPosition(list.size)
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                })

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        generateMainPath()
        binding.addMessage.setOnClickListener {
            val m = binding.messageEdt.text.toString()
            if (m.isNotBlank()) {
                binding.messageEdt.setText("")
                val simpleDateFormat = SimpleDateFormat("HH:mm")
                val date = simpleDateFormat.format(Date())
                val key = referenceMessage.push().key
                val chatMessage = ChatMessage(m, date, firebaseAuth.uid)
                referenceMessage.child(mainPath).child(key!!)
                    .setValue(chatMessage)

            } else {
                Toast.makeText(requireContext(), "Your message is blank!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val lastQuery = referenceMessage.child(mainPath).limitToLast(1)
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

    private fun generateMainPath() {
        if (firebaseAuth.currentUser!!.uid.length > userUid.length) {
            mainPath = firebaseAuth.currentUser!!.uid + userUid
        } else if (firebaseAuth.currentUser!!.uid.length < userUid.length) {
            mainPath = userUid + firebaseAuth.currentUser!!.uid
        } else {
            for (i in 0..userUid.length) {
                val c: Char = firebaseAuth.currentUser!!.uid.substring(i, i + 1).single()
                val u = userUid.substring(i, i + 1).single()
                val cCode: Int = c.code
                val uCode: Int = u.code
                when {
                    cCode > uCode -> {
                        mainPath = firebaseAuth.currentUser!!.uid + userUid
                        break
                    }
                    cCode < uCode -> {
                        mainPath = userUid + firebaseAuth.currentUser!!.uid
                        break
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onStop() {
        if (lastMessageKey.isNotBlank()) {
            referenceLast.child(firebaseAuth.currentUser!!.uid).child(userUid)
                .setValue(lastMessageKey)
        }
        super.onStop()
    }
}