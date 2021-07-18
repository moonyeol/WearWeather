package wear.weather.view

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import wear.weather.R
import wear.weather.adapter.MainListAdapter

class BoardListActivity : AppCompatActivity() {
    private var uid:String? = null
    private lateinit var database : DatabaseReference
    private val title_array = ArrayList<String>()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_list)
        val list_adapter = MainListAdapter(this,title_array)
        val listview_firebase = findViewById<ListView>(R.id.list_view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference().child("board")
        listview_firebase.adapter = list_adapter
        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                //실패 시
            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                for(data in snapshot.children){
//                    val modeResult = data.getValue(ContentModel::class.java)
//                    title_array.add(modeResult?.title.toString())
//                }
//                list_adapter.notifyDataSetChanged()
            }
        })





    }
}