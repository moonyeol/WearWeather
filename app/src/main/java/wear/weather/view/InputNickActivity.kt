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
import com.google.firebase.database.*
import wear.weather.R
import wear.weather.model.UserModel


class InputNickActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private var uid:String = ""
    private var email:String = ""
    private var nick:String = ""
    private var editNick: EditText? = null
    private var userModel : UserModel? = null
    private var text_nick_verification : TextView? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nick_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nick_next_button -> {
                nick = editNick?.text.toString()
                userModel = UserModel(
                        uid,
                        email,
                        nick
                )

                database.push().setValue(userModel)
                updateUI()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_nick)
        if(intent.hasExtra("uid")){
            uid = intent.getStringExtra("uid")!!
        }else{
            Log.w("BoardInputActivity", "signInWithCredential:failure")
        }
        if(intent.hasExtra("email")){
            email = intent.getStringExtra("email")!!
        }else{
            Log.w("BoardInputActivity", "signInWithCredential:failure")
        }




        editNick = findViewById<EditText>(R.id.editNick)
        text_nick_verification = findViewById(R.id.text_nick_verification)
        database = FirebaseDatabase.getInstance().getReference().child("users")


/*
        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                //실패 시
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val modeResult = data.getValue(NickModel::class.java)
                    if (modeResult?.uid.toString() == uid)
                        updateUI()
                }

            }
        })
*/
        nick = editNick?.text.toString()

        editNick?.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                //Log.d("NICKs", "여기까진 된겨");
                selectUser(p0.toString())

            }

        }


        )
/*
        editNick?.setOnFocusChangeListener(OnFocusChangeListener { view, b ->
            if (!b) {
                Log.d("NICKs", "여기까진 된겨");
                selectUser()
            } else {
                Log.d("NICKs", "여기까진 된거임");
            }
        })*/
    }

    fun updateUI(){
        val intent = Intent(this, PermissionActivity::class.java)

        intent.putExtra("usermodel", userModel)
        startActivity(intent)
    }

    fun selectUser(s:String?){
        database.orderByChild("nick").equalTo(s).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("NICKs", "닉네임 존재함");
                    editNick?.setBackgroundResource(R.drawable.red_edittext)
                    editNick?.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.impossible,0)
                    text_nick_verification?.setText(R.string.nick_impossible)
                    text_nick_verification?.setTextColor(resources.getColor(R.color.nick_impossible))
                //editNick?.compoundDrawablePadding = 9
                } else {
                    Log.d("NICKs", "닉네임 사용가능");
                    editNick?.setBackgroundResource(R.drawable.blue_edittext)
                    //val img = getDrawable(R.drawable.possible)
                    //img?.setBounds(0,0,9,9)
                    //editNick?.setCompoundDrawables(null,null,img,null)
                    editNick?.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.possible,0)
                    //editNick?.compoundDrawablePadding = 9
                    text_nick_verification?.setText(R.string.nick_possible)
                    text_nick_verification?.setTextColor(resources.getColor(R.color.nick_possible))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        /*
        database.ch.equalTo("nick", nick).get().addOnCompleteListener(OnCompleteListener{task ->
            if (task.isSuccessful) {
                editNick?.setBackgroundResource(R.drawable.red_edittext)
            } else {
                editNick?.setBackgroundResource(R.drawable.white_edittext)
            }
        })*/
    }

    private fun UpdateUiFail() {
    }

}
