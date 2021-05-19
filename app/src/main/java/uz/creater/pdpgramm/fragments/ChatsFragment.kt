package uz.creater.pdpgramm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.creater.pdpgramm.R
import uz.creater.pdpgramm.adapters.UserAdapter
import uz.creater.pdpgramm.databinding.FragmentChatsBinding
import uz.creater.pdpgramm.models.CheckVisibility
import uz.creater.pdpgramm.models.User

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var list: ArrayList<User>
    lateinit var userAdapter: UserAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        list = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        userAdapter = UserAdapter(list, object : UserAdapter.OnItemClickListener {
            override fun onItemClick(user: User) {
                val navOption = NavOptions.Builder()
                navOption.setEnterAnim(R.anim.pop_enter_anim)
                navOption.setExitAnim(R.anim.exit_anim)
                navOption.setPopEnterAnim(R.anim.enter_anim)
                navOption.setPopExitAnim(R.anim.pop_exit_anim)
                var bundle = Bundle()
                bundle.putString("userUid", user.uid)
                findNavController().navigate(
                    R.id.messageChatsFragment,
                    bundle,
                    navOption.build()
                )
            }
        })
        binding.rvChats.adapter = userAdapter
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value != null && value.uid != currentUser?.uid) {
                        list.add(value)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        CheckVisibility.isChat = true
    }

    override fun onStop() {
        CheckVisibility.isChat = false
        super.onStop()
    }

    companion object {
        private const val TAG = "ChatsFragment"
    }
}