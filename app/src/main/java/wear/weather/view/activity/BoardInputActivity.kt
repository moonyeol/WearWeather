package wear.weather.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import wear.weather.R
import wear.weather.adapter.BoardInputRecyclerviewAdapter
import wear.weather.databinding.ActivityBoardInputBinding
import wear.weather.model.BrandTagModel
import wear.weather.model.ContentDTO
import wear.weather.model.UserDTO
import wear.weather.view.activity.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class BoardInputActivity : AppCompatActivity() {
    private var uid: String = ""
    private lateinit var database: CollectionReference
    private lateinit var text_form: EditText

    private lateinit var brand_tag_button: Button
    private lateinit var location_button: Button
    private var brandTagList: ArrayList<BrandTagModel>? = null
    var photoUri: Uri? = null
    var uploadUri: Uri? = null

    var nickname: String? = null

    //todo 필터링 목록도 db에 저장해서 받아오기
    private val gender_array = arrayOf("여자", "남자")
    private val season_array = arrayOf("봄", "여름", "가을", "겨울")
    private val weather_array = arrayOf("맑음", "흐림", "비", "눈", "바람")
    private val temperature_array = arrayOf("더움", "따뜻", "선선", "쌀쌀", "추움")
    private val situation_array = arrayOf(
        "데일리",
        "출근",
        "데이트",
        "트레이닝",
        "등산",
        "커플",
        "트윈",
        "여행",
        "경조사"
    )
    private val style_array = arrayOf(
        "프레피",
        "미니멀",
        "스트릿",
        "레트로",
        "아웃도어",
        "홈웨어",
        "놈코어",
        "테크웨어",
        "클래식",
        "고프코어",
        "비즈니스캐주얼",
        "아메카지",
        "시티보이",
        "원마일웨어",
        "모던",
        "빈티지",
        "페미닌",
        "젠더리스",
        "그래니",
        "예술레저",
        "레이어드",
        "스포티",
        "에스닉"
    )


    private lateinit var keywords: MutableList<String>


    private var fbStorage: FirebaseStorage? = null


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.upload_next_button -> {
                funImageUpload()
                val inputData = ContentDTO(
                    text_form.text.toString(),
                    uploadUri.toString(),
                    uid,
                    nickname,
                    System.currentTimeMillis(),
                    keywords,
                    brandTagList
                )
                database.document().set(inputData)


                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)


                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        brandTagList = intent?.getParcelableArrayListExtra("listBrandTagModel")

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        brandTagList = intent?.getParcelableArrayListExtra<BrandTagModel>("listBrandTagModel")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (intent.hasExtra("uid")) {
//            uid = intent.getStringExtra("uid")!!
//        } else {
//            Log.w("BoardInputActivity", "signInWithCredential:failure")
//        }
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        keywords = mutableListOf()

        val binding = ActivityBoardInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        text_form = binding.postEditContent


        val itemGender = binding.listViewGender
        val itemSeason = binding.listViewSeason
        val itemWeather = binding.listViewWeather
        val itemTemperature = binding.listViewTemperature
        val itemSituation = binding.listViewSituation
        val itemStyle = binding.listViewStyle

        photoUri = Uri.parse("android.resource://wear.weather/drawable/image_temp2");
//      photoUri = Uri.parse(intent.getStringExtra("image"))
//      binding.postEditImage.setImageURI(photoUri)
        Glide.with(this)
            .load(photoUri)
            .apply(RequestOptions().centerCrop())
            .into(binding.postEditImage)
        val gender_adapter = BoardInputRecyclerviewAdapter(this, gender_array, keywords)
        val season_adapter = BoardInputRecyclerviewAdapter(this, season_array, keywords)
        val weather_adapter = BoardInputRecyclerviewAdapter(this, weather_array, keywords)
        val temperature_adapter = BoardInputRecyclerviewAdapter(this, temperature_array, keywords)
        val situation_adapter = BoardInputRecyclerviewAdapter(this, situation_array, keywords)
        val style_adapter = BoardInputRecyclerviewAdapter(this, style_array, keywords)

        brand_tag_button = binding.brandTagButton
        location_button = binding.locationButton


        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START

        }.let {
            itemGender.layoutManager = it
            itemGender.adapter = gender_adapter
        }

        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START


        }.let {
            itemSeason.layoutManager = it
            itemSeason.adapter = season_adapter

        }


        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }.let {
            itemWeather.layoutManager = it
            itemWeather.adapter = weather_adapter
        }

        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }.let {
            itemTemperature.layoutManager = it
            itemTemperature.adapter = temperature_adapter
        }

        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }.let {
            itemSituation.layoutManager = it
            itemSituation.adapter = situation_adapter
        }

        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }.let {
            itemStyle.layoutManager = it
            itemStyle.adapter = style_adapter
        }


        //val form_button = findViewById<Button>(R.id.form_button)
        //val context_text = findViewById<EditText>(R.id.context_text_form)
        //val button_upload_image = findViewById<Button>(R.id.button_upload_image)

        database = FirebaseFirestore.getInstance().collection("board")
        fbStorage = FirebaseStorage.getInstance()
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener {
                nickname = it.toObject(UserDTO::class.java)?.nickname
            }


        brand_tag_button.setOnClickListener {
            val intent = Intent(this, BrandTagActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("photoUri",photoUri.toString())
            startActivity(intent)
        }
        location_button.setOnClickListener {

        }


    }


//        database.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                //실패 시
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(data in snapshot.children){
//                    val modeResult = data.getValue(ContentModel::class.java)
//                    title_array.add(modeResult?.title.toString())
//                }
//
//            }
//        })
//
//
//        button_upload_image.setOnClickListener{
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
//            var photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, 0)
//        }
//    }

    private fun funImageUpload() {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imgFileName = "IMAGE_" + timeStamp + "_.png"
        val storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)
        storageRef?.putFile(photoUri!!)?.continueWithTask {
            if (!it.isSuccessful) {
                it.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }?.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.w("ImageUpload : ", "Success")
                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                uploadUri = it.result
            } else {
                Log.w("ImageUpload : ", "Fail")
                Toast.makeText(this, "Image Fail", Toast.LENGTH_SHORT).show()
            }

        }
    }




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode == 0){
//            if(resultCode == Activity.RESULT_OK){
//                uriPhoto = data?.data
//                Log.w("ImageUpload : ", uriPhoto.toString())
//                if(ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) == PackageManager.PERMISSION_GRANTED){
//                    funImageUpload()
//                }else{
//                    Log.w("ImageUpload : ", "No Permission")
//                }
//            }
//
//        }
//
//    }
}


