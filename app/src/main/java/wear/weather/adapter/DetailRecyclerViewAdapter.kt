package wear.weather.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import wear.weather.R
import wear.weather.databinding.ItemDetailBinding
import wear.weather.model.ContentDTO
import wear.weather.model.UserDTO
import wear.weather.view.CommentActivity
import wear.weather.view.fragment.UserFragment
import java.util.*


class DetailRecyclerViewAdapter : RecyclerView.Adapter<DetailRecyclerViewAdapter.CustomViewHolder>() {

    val contentDTOs: ArrayList<ContentDTO>
    val contentUidList: ArrayList<String>
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null

    init {
        firestore = FirebaseFirestore.getInstance()
        contentDTOs = ArrayList()
        contentUidList = ArrayList()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userDTO = task.result?.toObject(UserDTO::class.java)
                getCotents(userDTO?.followings)
            }
        }
    }

    fun getCotents(followers: MutableMap<String, Boolean>?) {
        imagesSnapshot = firestore?.collection("board")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    val item = snapshot.toObject(ContentDTO::class.java)!!
                    if (followers?.keys?.contains(item.uid)!!) {
                        contentDTOs.add(item)
                        contentUidList.add(snapshot.id)
                    }
                }
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(contentDTOs[position], contentUidList[position])
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }


//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class CustomViewHolder(private val binding: ItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var firestore: FirebaseFirestore? = null

        init {
            firestore = FirebaseFirestore.getInstance()

        }

        fun bind(contentDTO: ContentDTO, contentUid: String) {
            // Profile Image 가져오기
            contentDTO.uid?.let {
                firestore?.collection("users")?.document(it)
                    ?.get()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val url = task.result?.get("profileImage")
                            Glide.with(binding.itemImageWithProfile.detailviewitemProfileImage.context)
                                .load(url)
                                .apply(RequestOptions().circleCrop())
                                .into(binding.itemImageWithProfile.detailviewitemProfileImage)
                        }
                    }
            }

            //UserFragment로 이동
            binding.itemImageWithProfile.detailviewitemProfileImage.setOnClickListener {

                val fragment = UserFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTO.uid)
                bundle.putString("nickname", contentDTO.nickname)

                fragment.arguments = bundle

              /*  (binding.root.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_layout, fragment)
                .commit()*/
            }

            // 유저 닉네임
            binding.itemImageWithProfile.detailviewitemProfileTextview.text = contentDTO.nickname

            // 가운데 이미지
            Glide.with(binding.itemImageWithProfile.detailviewitemImageviewContent.context)
                .load(contentDTO.imageUrl)
                .into(binding.itemImageWithProfile.detailviewitemImageviewContent)

            // 설명 텍스트
            binding.detailviewitemExplainTextview.text = contentDTO.explain
            // 좋아요 이벤트
            binding.itemContentToolbar.detailviewitemFavoriteImageview.setOnClickListener { favoriteEvent(
                contentUid
            ) }

            //좋아요 버튼 설정
            if (contentDTO.favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                binding.itemContentToolbar.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite)

            } else {
                binding.itemContentToolbar.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite_border)
            }
            //좋아요 카운터 설정
            binding.itemContentToolbar.detailviewitemFavoritecounterTextview.text =
                "좋아요 " + contentDTO.favoriteCount + "개"

            binding.itemContentToolbar.detailviewitemCommentImageview.setOnClickListener {
                val intent = Intent(binding.root.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUid)
                intent.putExtra("destinationUid", contentDTO.uid)
                binding.root.context.startActivity(intent)

            }
        }

        //좋아요 이벤트 기능
        private fun favoriteEvent(contentUid: String) {
            val tsDoc = firestore?.collection("board")?.document(contentUid)
            firestore?.runTransaction { transaction ->

                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    // Unstar the post and remove self from stars
                    contentDTO.favoriteCount -= 1
                    contentDTO.favorites.remove(uid)

                } else {
                    // Star the post and add self to stars
                    contentDTO.favoriteCount += 1
                    contentDTO.favorites[uid] = true
                }
                transaction.set(tsDoc, contentDTO)
            }
        }
    }

}

