package wear.weather.view
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import wear.weather.R
import wear.weather.databinding.ActivityLoginBinding
import wear.weather.model.UserDTO


class LoginActivity : AppCompatActivity() {
    var googleSignInClient : GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database : CollectionReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar:ActionBar? = supportActionBar
        actionBar?.setTitle("로그인")
        actionBar?.setDisplayHomeAsUpEnabled(true)
        // Google Sign-In Methods
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        auth = Firebase.auth
        database = FirebaseFirestore.getInstance().collection("users")
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInBtn = binding.googleSignInBtn
        signInBtn.setOnClickListener {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }



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
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            checkNick(currentUser)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser








                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }

            }
    }


    private fun checkNick(user: FirebaseUser?){
        database.document(user!!.uid).get().addOnSuccessListener {
            if(it.exists()){
                Log.d("NICKs", "닉네임 존재함");
                val userModel = it.toObject(UserDTO::class.java)
                updateUI(userModel)
            }else{
                Log.d("NICKs", "닉네임 입력해야됨");
                updateUI(user)
            }
        }.addOnFailureListener {
            Log.d("NICKs", "get failed with ", it)

        }
    }

    private fun updateUI (userDTO: UserDTO?) {
        val intent = Intent(this, PermissionActivity::class.java)
        intent.putExtra("usermodel",userDTO)
        startActivity(intent)
    }

    private fun updateUI (user: FirebaseUser?) {
        val intent = Intent(this, InputNickActivity::class.java)
        intent.putExtra("uid",user?.uid)
        intent.putExtra("email",user?.email)
        startActivity(intent)
    }
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}