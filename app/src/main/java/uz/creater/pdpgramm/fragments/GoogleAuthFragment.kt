package uz.creater.pdpgramm.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.creater.pdpgramm.R
import uz.creater.pdpgramm.databinding.FragmentGoogleAuthBinding
import uz.creater.pdpgramm.models.User

class GoogleAuthFragment : Fragment() {

    private lateinit var binding: FragmentGoogleAuthBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        if (auth.currentUser != null) {
            firebaseDatabase.getReference("checkOnline")
                .child(auth.currentUser!!.uid).setValue(1)
            findNavController().popBackStack(R.id.googleAuthFragment, true)
            findNavController().navigate(
                R.id.mainFragment,
                Bundle()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoogleAuthBinding.inflate(inflater, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.googleButton.setOnClickListener {
            signIn()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val currentUser = auth.currentUser
                    val user = User(
                        currentUser?.displayName,
                        currentUser?.photoUrl.toString(),
                        currentUser?.email,
                        currentUser?.uid
                    )
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            var b = false
                            for (child in children) {
                                val value = child.getValue(User::class.java)
                                if (value != null && value.uid == currentUser?.uid) {
                                    b = true
                                    break
                                }
                            }
                            if (!b) {
                                reference.child(currentUser!!.uid).setValue(user)
                            }
                            firebaseDatabase.getReference("checkOnline")
                                .child(currentUser!!.uid).setValue(1)
                            Log.d(TAG, "onDataChange: Utish>>>>>>>")
                            Toast.makeText(context, "Registration is completed", Toast.LENGTH_SHORT)
                                .show()
                            val navOption = NavOptions.Builder()
                            navOption.setEnterAnim(R.anim.pop_enter_anim)
                            navOption.setExitAnim(R.anim.exit_anim)
                            navOption.setPopEnterAnim(R.anim.enter_anim)
                            navOption.setPopExitAnim(R.anim.pop_exit_anim)
                            var bundle = Bundle()
                            findNavController().popBackStack(R.id.googleAuthFragment, true)
                            findNavController().navigate(
                                R.id.mainFragment,
                                bundle,
                                navOption.build()
                            )
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(
                        binding.root,
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}