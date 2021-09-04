package wear.weather.view


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import wear.weather.R
import wear.weather.databinding.ActivityInputNickBinding
import wear.weather.model.UserDTO


class InputNickActivity : AppCompatActivity() {
    private lateinit var database: CollectionReference
    private var uid: String = ""
    private var email: String = ""
    private var nick: String = ""
    private var editNick: EditText? = null
    private var userDTO: UserDTO? = null
    private var text_nick_verification: TextView? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nick_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nick_next_button -> {
                nick = editNick?.text.toString()
                userDTO = UserDTO(
                    nick
                )

                database.document(FirebaseAuth.getInstance().currentUser!!.uid).set(userDTO!!)
                updateUI()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInputNickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra("uid")) {
            uid = intent.getStringExtra("uid")!!
        } else {
            Log.w("BoardInputActivity", "signInWithCredential:failure")
        }
        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email")!!
        } else {
            Log.w("BoardInputActivity", "signInWithCredential:failure")
        }

        editNick = binding.editNick
        text_nick_verification = binding.textNickVerification

        database = FirebaseFirestore.getInstance().collection("users")

        nick = editNick?.text.toString()

        editNick?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                selectUser(p0.toString())

            }
        }
        )
    }

    fun updateUI() {
        val intent = Intent(this, PermissionActivity::class.java)

        startActivity(intent)
    }

    fun selectUser(s: String?) {
        var flag: Boolean = false
        database.get().addOnSuccessListener {
            for (document in it) {
                if (s!!.equals(document.get("nickname"))) {
                    flag = true
                    break
                }
            }
            if (flag) {
                Log.d("NICKs", "닉네임 존재함");
                editNick?.setBackgroundResource(R.drawable.red_edittext)
                editNick?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.impossible, 0)
                text_nick_verification?.setText(R.string.nick_impossible)
                text_nick_verification?.setTextColor(resources.getColor(R.color.nick_impossible))
            } else {
                Log.d("NICKs", "닉네임 사용가능");
                editNick?.setBackgroundResource(R.drawable.blue_edittext)
                //val img = getDrawable(R.drawable.possible)
                //img?.setBounds(0,0,9,9)
                //editNick?.setCompoundDrawables(null,null,img,null)
                editNick?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.possible, 0)
                //editNick?.compoundDrawablePadding = 9
                text_nick_verification?.setText(R.string.nick_possible)
                text_nick_verification?.setTextColor(resources.getColor(R.color.nick_possible))
            }

        }

    }

    private fun UpdateUiFail() {
    }

}
