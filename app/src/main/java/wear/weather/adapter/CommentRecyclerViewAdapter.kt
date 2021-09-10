package wear.weather.adapter

import android.graphics.drawable.Drawable
import android.util.Log
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
    private var comments: ArrayList<ContentDTO.Comment> = ArrayList()
    lateinit var drawable: Drawable

    init {
        contentUid?.let { Log.w("Comments", it) }

        FirebaseFirestore
            .getInstance()
            .collection("board")
            .document(contentUid!!)
            .collection("comments")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener {
                comments.clear()
                for(document in it){
                    Log.w("Comments",document.toString())
                    comments.add(document.toObject(ContentDTO.Comment::class.java)!!)

                }
                notifyDataSetChanged()

            }
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                comments.clear()
//                querySnapshot?.let { Log.w("Comments", it.toString()) }
//
//                if (querySnapshot == null) return@addSnapshotListener
//                for (snapshot in querySnapshot.documents) {
//                    Log.w("Comments",snapshot.toString())
//                    comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
//                }
//                notifyDataSetChanged()
//
//            }
//            .remove()
        Log.w("Comments",comments.toString())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // Profile Image
//        FirebaseFirestore.getInstance()
//            .collection("users")
//            .document(comments[position].uid!!)
//            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
//                if (documentSnapshot?.data != null) {
//                    val url = documentSnapshot.data!!["profileImage"]
//
//                        Glide.with(holder.itemView.context)
//                        .load(url)
//                        .apply(RequestOptions().circleCrop()).into(holder.getCommentviewitemImageviewProfile())
//                }
//            }
        holder.bind(comments[position])


    }

    override fun getItemCount(): Int {

        return comments.size
    }

    class CustomViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: ContentDTO.Comment){
            binding.commentviewitemTextviewProfile.text = data.nickname
            binding.commentviewitemTextviewComment.text = data.comment
            // Profile Image
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(data.uid!!)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot?.data != null) {
                        val url = documentSnapshot.data!!["profileImage"]

                        Glide.with(binding.root)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(binding.commentviewitemImageviewProfile)
                    }
                }
        }

    }


}