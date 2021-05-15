package wear.weather.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import wear.weather.R
import wear.weather.adapter.BoardInputRecyclerviewAdapter
import wear.weather.model.ContentModel
import wear.weather.model.UserModel


class BoardInputActivity : AppCompatActivity() {
    private var uid: String = ""
    private lateinit var database: DatabaseReference
    private lateinit var text_form :EditText

    private lateinit var brand_tag_button :Button
    private lateinit var location_button :Button



    private val gender_array = arrayOf<String>("여자", "남자")
    private val season_array = arrayOf<String>("봄", "여름", "가을", "겨울")
    private val weather_array = arrayOf<String>("맑음", "흐림", "비", "눈", "바람")
    private val temperature_array = arrayOf<String>("더움", "따뜻", "선선", "쌀쌀", "추움")
    private val situation_array = arrayOf<String>(
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
    private val style_array = arrayOf<String>(
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
    var uriPhoto: Uri? = null


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.upload_next_button -> {
                val datInput = ContentModel(
                    uid,
                    text_form.text.toString(),
                    keywords
                )
                var post = database.push()
                post.setValue(datInput)
                post.key

                val intent = Intent(this,BoardListActivity::class.java)

                startActivity(intent)


                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        finish()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_input)
        if (intent.hasExtra("uid")) {
            uid = intent.getStringExtra("uid")!!
        } else {
            Log.w("BoardInputActivity", "signInWithCredential:failure")
        }

        keywords = mutableListOf<String>()
        text_form = findViewById<EditText>(R.id.post_edit_content)

        val itemGender = findViewById<RecyclerView>(R.id.list_view_gender)
        val itemSeason = findViewById<RecyclerView>(R.id.list_view_season)
        val itemWeather = findViewById<RecyclerView>(R.id.list_view_weather)
        val itemTemperature = findViewById<RecyclerView>(R.id.list_view_temperature)
        val itemSituation = findViewById<RecyclerView>(R.id.list_view_situation)
        val itemStyle = findViewById<RecyclerView>(R.id.list_view_style)

        val gender_adapter = BoardInputRecyclerviewAdapter(this, gender_array, keywords)
        val season_adapter = BoardInputRecyclerviewAdapter(this, season_array, keywords)
        val weather_adapter = BoardInputRecyclerviewAdapter(this, weather_array, keywords)
        val temperature_adapter = BoardInputRecyclerviewAdapter(this, temperature_array, keywords)
        val situation_adapter = BoardInputRecyclerviewAdapter(this, situation_array, keywords)
        val style_adapter = BoardInputRecyclerviewAdapter(this, style_array, keywords)

        brand_tag_button = findViewById<Button>(R.id.brand_tag_button)
        location_button = findViewById<Button>(R.id.location_button)


        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
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

        database = FirebaseDatabase.getInstance().getReference().child("board")
        fbStorage = FirebaseStorage.getInstance()




        brand_tag_button.setOnClickListener {
            val intent = Intent(this,ImageTagActivity::class.java)

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

//    private fun funImageUpload(){
//
//        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var imgFileName = "IMAGE_" + timeStamp + "_.png"
//        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)
//
//        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
//            Log.w("ImageUpload : ", "Success")
//            Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
//        }?.addOnFailureListener { Log.w("ImageUpload : ", "Fail")
//            Toast.makeText(this, "Image Fail", Toast.LENGTH_SHORT).show()}
//    }



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


