package wear.weather.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import wear.weather.databinding.ItemCommentBinding
import wear.weather.model.ContentDTO
import java.util.ArrayList

class CommentRecyclerViewAdapter(var contentUid: String?) : RecyclerView.Adapter<CommentRecyclerViewAdapter.CustomViewHolder>() {
    var comments: ArrayList<ContentDTO.Comment>
    lateinit var drawable: Drawable

    init {
        comments = ArrayList()
        FirebaseFirestore
            .getInstance()
            .collection("images")
            .document(contentUid!!)
            .collection("comments")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                comments.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                }
                notifyDataSetChanged()

            }
            .remove()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_comment, parent, false)
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // Profile Image
        FirebaseFirestore.getInstance()
            .collection("profileImages")
            .document(comments[position].uid!!)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot?.data != null) {

                    val url = documentSnapshot?.data!!["image"]

                        Glide.with(holder.itemView.context)
                        .load(url)
                        .apply(RequestOptions().circleCrop()).into(holder.getCommentviewitemImageviewProfile())
                }
            }
        holder.bind(comments[position])


    }

    override fun getItemCount(): Int {

        return comments.size
    }

    class CustomViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: ContentDTO.Comment){
            binding.commentviewitemTextviewProfile.text = data.userId
            binding.commentviewitemTextviewComment.text = data.comment
        }
        fun getCommentviewitemImageviewProfile()= binding.commentviewitemImageviewProfile

    }


}