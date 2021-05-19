package uz.creater.pdpgramm.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.creater.pdpgramm.R
import uz.creater.pdpgramm.adapters.GroupAdapter
import uz.creater.pdpgramm.databinding.FragmentGroupsBinding
import uz.creater.pdpgramm.databinding.SelectDialogBinding
import uz.creater.pdpgramm.models.CheckVisibility
import uz.creater.pdpgramm.models.Group

class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var groupList: ArrayList<Group>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("groups")
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        binding.actionBtn.setOnClickListener {
            val alertDialog = context?.let { AlertDialog.Builder(it) }
            alertDialog?.setCancelable(false)
            val dialog = alertDialog!!.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            val dialogView: View = layoutInflater.inflate(
                R.layout.select_dialog,
                null,
                false
            )
            dialog.setView(dialogView)
            val bindDialog = SelectDialogBinding.bind(dialogView)

            bindDialog.createTv.setOnClickListener {
                val name = bindDialog.groupName.text.toString()
                val desc = bindDialog.groupDesc.text.toString()
                if (name.isNotBlank() && desc.isNotBlank()) {
                    val key = reference.push().key
                    val group = Group(name, desc, key)
                    reference.child(key!!).setValue(group)
                    Toast.makeText(context, "Group added successfully!!!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Please fill the blanks!!!", Toast.LENGTH_SHORT).show()
                }
            }

            bindDialog.cancelTv.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        groupList = ArrayList()
        groupAdapter = GroupAdapter(groupList, object : GroupAdapter.OnClick {
            override fun onItemClick(group: Group) {
                val navOption = NavOptions.Builder()
                navOption.setEnterAnim(R.anim.pop_enter_anim)
                navOption.setExitAnim(R.anim.exit_anim)
                navOption.setPopEnterAnim(R.anim.enter_anim)
                navOption.setPopExitAnim(R.anim.pop_exit_anim)
                var bundle = Bundle()
                bundle.putString("groupKey", group.groupKey)
                findNavController().navigate(
                    R.id.messageGroupsFragment,
                    bundle,
                    navOption.build()
                )
            }
        })
        binding.rvGroups.adapter = groupAdapter
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                val children = snapshot.children
                for (child in children) {
                    val group = child.getValue(Group::class.java)
                    if (group != null) {
                        groupList.add(group)
                    }
                }
                groupAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        CheckVisibility.isGroup = true
    }

    override fun onStop() {
        CheckVisibility.isGroup = false
        super.onStop()
    }
}