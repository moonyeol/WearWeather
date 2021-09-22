package wear.weather.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import wear.weather.adapter.CommentRecyclerViewAdapter
import wear.weather.databinding.ActivityCommentBinding
import wear.weather.model.ContentDTO

class CommentActivity : AppCompatActivity() {

    var contentUid: String? = null
    var user: FirebaseUser? = null
    var destinationUid: String? = null
    lateinit var adapter: CommentRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding: ActivityCommentBinding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        user = FirebaseAuth.getInstance().currentUser
        destinationUid = intent.getStringExtra("destinationUid")
        contentUid = intent.getStringExtra("contentUid")
        val commentBtnSend = activityMainBinding.commentBtnSend
        val commentRecyclerview = activityMainBinding.commentRecyclerview
        val commentEditMessage = activityMainBinding.commentEditMessage
        var nickname : String? = null
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(user!!.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    nickname = task.result?.get("nickname") as String
                }
            }
        commentBtnSend.setOnClickListener {
            val comment = ContentDTO.Comment()

            comment.nickname = nickname
            comment.comment = commentEditMessage.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                    .collection("board")
                    .document(contentUid!!)
                    .collection("comments")
                    .document()
                    .set(comment)
                .addOnSuccessListener {
                    adapter.notifyDataSetChanged()
                }

            commentEditMessage.setText("")

        }
        adapter = CommentRecyclerViewAdapter(contentUid)
        commentRecyclerview.adapter = adapter
        commentRecyclerview.layoutManager = LinearLayoutManager(this)

    }


}
