package wear.weather.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import wear.weather.R
import wear.weather.databinding.FragmentUserBinding
import wear.weather.model.ContentDTO
import wear.weather.model.FollowDTO
import wear.weather.view.LoginActivity
import java.util.*

class UserFragment : Fragment() {

    val PICK_PROFILE_FROM_ALBUM = 10

    // Firebase
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    //private String destinationUid;
    var uid: String? = null
    var currentUserUid: String? = null

    var fragmentView: View? = null


    var followListenerRegistration: ListenerRegistration? = null
    var followingListenerRegistration: ListenerRegistration? = null
    var imageprofileListenerRegistration: ListenerRegistration? = null
    var recyclerListenerRegistration: ListenerRegistration? = null
    lateinit var fragmentGridBinding: FragmentUserBinding


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentView = inflater.inflate(R.layout.fragment_user, container, false)
        fragmentGridBinding= FragmentUserBinding.inflate(layoutInflater)
        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        currentUserUid = auth?.currentUser?.uid

        if (arguments != null) {

            uid = arguments!!.getString("destinationUid")

            // 본인 계정인 경우 -> 로그아웃, Toolbar 기본으로 설정
            if (uid != null && uid == currentUserUid) {
                fragmentGridBinding.accountBtnFollowSignout.text = getString(R.string.signout)
                fragmentGridBinding.accountBtnFollowSignout.setOnClickListener {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                    auth?.signOut()
                }
            } else {
                fragmentGridBinding.accountBtnFollowSignout.text = getString(R.string.follow)
                //view.account_btn_follow_signout.setOnClickListener{ requestFollow() }
                // TODO 뭔가 후처리~~~~
                fragmentGridBinding.accountBtnFollowSignout.setOnClickListener{
                    requestFollow()

                }

            }


        }

        // Profile Image Click Listener
        fragmentGridBinding.accountIvProfile.setOnClickListener{
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                //앨범 오픈
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                activity!!.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
            }
        }
        getFollowing()
        getFollower()
        fragmentGridBinding.accountRecyclerview.layoutManager = GridLayoutManager(activity!!, 3)
        fragmentGridBinding.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()


        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        getProfileImage()
    }

    fun getProfileImage() {
        imageprofileListenerRegistration = firestore?.collection("profileImages")?.document(uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                    if (documentSnapshot?.data != null) {
                        val url = documentSnapshot.data!!["image"]
                        activity?.let {
                            Glide.with(it)
                                .load(url)
                                .apply(RequestOptions().circleCrop()).into(fragmentGridBinding.accountIvProfile)
                        }
                    }
                }

    }


    fun getFollowing() {
        followingListenerRegistration = firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)
            if (followDTO == null) return@addSnapshotListener
            fragmentGridBinding.accountTvFollowerCount.text = followDTO?.followingCount.toString()

        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFollower() {

        followListenerRegistration = firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)
            if (followDTO == null) return@addSnapshotListener
            fragmentGridBinding.accountTvFollowerCount.text = followDTO?.followerCount.toString()

            if (followDTO?.followers?.containsKey(currentUserUid)!!) {
                fragmentGridBinding.accountBtnFollowSignout.text = getString(R.string.follow_cancel)
                fragmentGridBinding.accountBtnFollowSignout
                    .background
                    .colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(activity!!, R.color.colorLightGray
                ), BlendModeCompat.MULTIPLY)

            } else {

                if (uid != currentUserUid) {
                    fragmentGridBinding.accountBtnFollowSignout.text = getString(R.string.follow)
                    fragmentGridBinding.accountBtnFollowSignout.background.colorFilter = null

                }
            }

        }

    }


    fun requestFollow() {


        var tsDocFollowing = firestore!!.collection("users").document(currentUserUid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }
            // Unstar the post and remove self from stars
            if (followDTO?.followings?.containsKey(uid)!!) {

                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {

                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        var tsDocFollower = firestore!!.collection("users").document(uid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true


                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO?.followers?.containsKey(currentUserUid!!)!!) {


                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            } else {

                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true

            }// Star the post and add self to stars

            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }

    }



    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs: ArrayList<ContentDTO>

        init {

            contentDTOs = ArrayList()

            // 나의 사진만 찾기
            recyclerListenerRegistration = firestore?.collection("images")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentGridBinding.accountTvFollowerCount.text = contentDTOs.size.toString()
                notifyDataSetChanged()

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent.context)

            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            return CustomViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageView
            Glide.with(holder.itemView.context)
                    .load(contentDTOs[position].imageUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(imageview)
        }

        override fun getItemCount(): Int {

            return contentDTOs.size
        }

        // RecyclerView Adapter - View Holder
        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }

    override fun onStop() {
        super.onStop()
        followListenerRegistration?.remove()
        followingListenerRegistration?.remove()
        imageprofileListenerRegistration?.remove()
        recyclerListenerRegistration?.remove()
    }

}
