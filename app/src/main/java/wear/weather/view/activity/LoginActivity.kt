package wear.weather.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.data.OAuthLoginState
import wear.weather.R
import wear.weather.databinding.ActivityLoginBinding
import wear.weather.model.UserDTO

class LoginActivity : AppCompatActivity() {
    //google
    var auth: FirebaseAuth? = null
    val GOOGLE_REQUEST_CODE = 99
    private lateinit var googleSignInClient: GoogleSignInClient
    //naver
    private lateinit var nhnOAuthLoginModule: OAuthLogin
    lateinit var mOAuthLoginModule: OAuthLogin
    lateinit var mContext: Context
    private lateinit var database : CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseFirestore.getInstance().collection("users")
        auth = FirebaseAuth.getInstance()
        var actionBar: ActionBar? = supportActionBar
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        val googleSignInBtn =  binding.googleSignInBtn
        googleSignInBtn.setOnClickListener {
            signIn()
        }
        //kakao
        val kakaoSignInBtn = binding.kakaoSignInBtn
        //로그인 공통 콜백 구성
        val callback: ((OAuthToken?, Throwable?) -> Unit) = { token, error ->
            if (error != null) { //Login Fail
                Log.e(k_TAG, "Kakao Login Failed :", error)
            } else if (token != null) { //Login Success
                startMainActivity()
            }
        }

        kakaoSignInBtn.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            LoginClient.instance.run {
                if (isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }
        }


        //naver
        nhnOAuthLoginModule = OAuthLogin.getInstance()
        nhnOAuthLoginModule.init(this, getString(wear.weather.R.string.naver_client_id)
            ,getString(wear.weather.R.string.naver_client_secret)
            ,getString(wear.weather.R.string.naver_client_name))
        initOnClickListener()

    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /*
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            checkNick(currentUser)
        }
    }*/

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "로그인 성공")
                    val user = auth!!.currentUser
                    loginSuccess()
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
        finish()
    }
    private fun updateUI (user: FirebaseUser?) {
        val intent = Intent(this, InputNickActivity::class.java)
        intent.putExtra("uid",user?.uid)
        intent.putExtra("email",user?.email)
        startActivity(intent)
        finish()
    }
    private fun loginSuccess(){
        val intent = Intent(this,InputNickActivity::class.java)
        startActivity(intent)
        finish()
    }

    //kakao
    private fun startMainActivity() {
        startActivity(Intent(this, InputNickActivity::class.java))
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    //naver
    private fun initOnClickListener(){ //네이버 로그인
        val naverSignInBtn = findViewById<View>(wear.weather.R.id.naverSignInBtn)
        naverSignInBtn.setOnClickListener { nhnSignIn() }
    }
    fun successLogin(){
        val intent = Intent(this, InputNickActivity::class.java)
        startActivity(intent)
        finish()
    }
    /** * 네이버 로그인 콜백 */
    private fun nhnSignIn(){
        // 연동 해제 시 주석 풀기 (클라이언트, 서버 모두 삭제)
        // val isSuccessDeleteToken = nhnOAuthLoginModule.logoutAndDeleteToken(this@LoginActivity)
        // nhnOAuthLoginModule.logout(this@LoginActivity)
        if(nhnOAuthLoginModule.getState(this@LoginActivity) == OAuthLoginState.OK){
            Log.d(TAG, "Nhn status don't need login")
            RequestNHNLoginApiTask().execute()
        }else{
            Log.d(TAG, "Nhn status need login")
            nhnOAuthLoginModule.startOauthLoginActivity(this, @SuppressLint("HandlerLeak")
            object: OAuthLoginHandler(){
                override fun run(success: Boolean) {
                    if (success) {
                        val accessToken = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
                        val refreshToken = nhnOAuthLoginModule.getRefreshToken(this@LoginActivity)
                        val expiresAt = nhnOAuthLoginModule.getExpiresAt(this@LoginActivity)
                        val tokenType = nhnOAuthLoginModule.getTokenType(this@LoginActivity)
                        Log.i(TAG, "nhn Login Access Token : $accessToken")
                        Log.i(TAG, "nhn Login refresh Token : $refreshToken")
                        Log.i(TAG, "nhn Login expiresAt : $expiresAt")
                        Log.i(TAG, "nhn Login Token Type : $tokenType")
                        Log.i(TAG, "nhn Login Module State : " + nhnOAuthLoginModule.getState(this@LoginActivity).toString())
                        successLogin()
                    } else {
                        val errorCode = nhnOAuthLoginModule.getLastErrorCode(this@LoginActivity).getCode()
                        val errorDesc = nhnOAuthLoginModule.getLastErrorDesc(this@LoginActivity)
                        Toast.makeText(this@LoginActivity, "errorCode:$errorCode, errorDesc:$errorDesc", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    } /** * NHN Access Token 으로 앱 로그인 */

    inner class RequestNHNLoginApiTask : AsyncTask<Void, Void, String>() {
        override fun onPostExecute(result: String?) {
            Log.d(TAG, "onPreExecute : $result")

            val startToken = "<message>"
            val endToken = "</message>"

            val startIndex = result?.indexOf(startToken) ?: -1
            val endIndex = result?.indexOf(endToken) ?: -1

            if (startIndex == -1 || endIndex <= 0){
                return }
            val message = result?.substring(startIndex + startToken.length, endIndex)?.trim()
            if(message.equals("success")){
                Log.d(TAG, "Success RequestNHNLoginApiTask")
                successLogin()
            }else{
                Log.e(TAG, "Login Fail")
            }
        }

        override fun doInBackground(vararg params: Void?): String {
            val url = "https://apis.naver.com/nidlogin/nid/getHashId_v2.xml"
            val at = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
            return nhnOAuthLoginModule.requestApi(this@LoginActivity, at, url)
        }
        override fun onPreExecute() {

        }
    }

    private fun naverLogout(){
        mOAuthLoginModule.logout(mContext)
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val k_TAG = "LoginActivity"

    }

}