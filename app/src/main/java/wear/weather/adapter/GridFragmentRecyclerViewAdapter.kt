package wear.weather.adapter

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import wear.weather.R
import wear.weather.model.ContentDTO
import wear.weather.view.fragment.DetailViewFragment
import wear.weather.view.fragment.UserFragment
import java.util.*

class GridFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imagesSnapshot  : ListenerRegistration? = null
    var contentDTOs: ArrayList<ContentDTO>
    init {

        contentDTOs = ArrayList()
        imagesSnapshot = FirebaseFirestore
            .getInstance().collection("board").orderBy("timestamp")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    Log.w("Grid",snapshot.toString())
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                Log.w("Grid contentDTOs",contentDTOs.toString())
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        //현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값을 가지고 오기
        val width = parent.resources.displayMetrics.widthPixels / 3

        val imageView = ImageView(parent.context)
        imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

        return CustomViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val imageView = (holder as CustomViewHolder).imageView

        Glide.with(holder.itemView.context)
            .load(contentDTOs[position].imageUrl)
            .apply(RequestOptions().centerCrop())
            .into(imageView)

        imageView.setOnClickListener {
//            val fragment = UserFragment()
            val fragment = DetailViewFragment()

            val bundle = Bundle()

            bundle.putString("destinationUid", contentDTOs[position].uid)
            bundle.putString("nickname", contentDTOs[position].nickname)

            fragment.arguments = bundle
            (it.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }

    inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)


}