package wear.weather.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import wear.weather.databinding.ItemDailyBestStyleBinding
import wear.weather.model.ContentDTO
import wear.weather.view.fragment.DetailViewFragment
import wear.weather.view.fragment.UserFragment
import java.util.*

class DailyBestStyleRecyclerViewAdapter : RecyclerView.Adapter<DailyBestStyleRecyclerViewAdapter.CustomViewHolder>() {
    var imagesSnapshot  : ListenerRegistration? = null
    var contentDTOs: ArrayList<ContentDTO> = ArrayList()

    init {

        imagesSnapshot = FirebaseFirestore
            .getInstance().collection("board").orderBy("favoriteCount").limit(10)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    Log.w("DailyBestStyle",snapshot.toString())
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                Log.w("D_B_Style contentDTOs",contentDTOs.toString())
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemDailyBestStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(contentDTOs[position].imageUrl)
            .apply(RequestOptions().centerCrop())
            .into(holder.getDetailviewitemImageviewContent())
        holder.bind(contentDTOs[position])
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }

    inner class CustomViewHolder(var binding: ItemDailyBestStyleBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(contentDTO : ContentDTO){
            binding.detailviewitemProfileTextview.text = contentDTO.nickname
            binding.detailviewitemImageviewContent.setOnClickListener {
                val fragment = DetailViewFragment()

                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTO.uid)
                bundle.putString("nickname", contentDTO.nickname)

                fragment.arguments = bundle
                (it.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, fragment)
                    .commit()
            }
        }
        fun getDetailviewitemImageviewContent() = binding.detailviewitemImageviewContent

    }


}