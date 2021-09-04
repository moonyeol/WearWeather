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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding: ActivityCommentBinding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        user = FirebaseAuth.getInstance().currentUser
        destinationUid = intent.getStringExtra("destinationUid")
        contentUid = intent.getStringExtra("contentUid")
        val comment_btn_send = activityMainBinding.commentBtnSend
        val comment_recyclerview = activityMainBinding.commentRecyclerview
        val comment_edit_message = activityMainBinding.commentEditMessage
        comment_btn_send.setOnClickListener {
            val comment = ContentDTO.Comment()

            comment.userId = FirebaseAuth.getInstance().currentUser!!.email
            comment.comment = comment_edit_message.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                    .collection("images")
                    .document(contentUid!!)
                    .collection("comments")
                    .document()
                    .set(comment)

            comment_edit_message.setText("")

        }

        comment_recyclerview.adapter = CommentRecyclerViewAdapter(contentUid)
        comment_recyclerview.layoutManager = LinearLayoutManager(this)

    }



    override fun onStop() {
        super.onStop()
    }





}
