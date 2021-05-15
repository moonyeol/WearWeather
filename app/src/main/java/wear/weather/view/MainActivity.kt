package wear.weather.view
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import wear.weather.R
import wear.weather.model.UserModel


class MainActivity : AppCompatActivity() {
    var googleSignInClient : GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var actionBar:ActionBar? = supportActionBar
        actionBar?.setTitle("로그인")
        actionBar?.setDisplayHomeAsUpEnabled(true)
        // Google Sign-In Methods
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference().child("users")
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInBtn = findViewById<View>(R.id.signInBtn)
        signInBtn.setOnClickListener {
            var signInIntent = googleSignInClient?.signInIntent
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
            updateUI(currentUser)
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
                    database.orderByChild("uid").equalTo(user?.uid).addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            //실패 시
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Log.d("NICKs", "닉네임 존재함");
                                val userModel = snapshot.getValue(UserModel::class.java)
                                updateUI(userModel)
                            } else {
                                Log.d("NICKs", "닉네임 입력해야됨");
                                updateUI(user)
                            }
                        }
                    })


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }

                // ...
            }
    }

    private fun updateUI (userModel: UserModel?) {
        val intent = Intent(this, BoardInputActivity::class.java)
        intent.putExtra("usermodel",userModel)
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