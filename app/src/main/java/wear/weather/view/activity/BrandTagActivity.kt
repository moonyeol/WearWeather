package wear.weather.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import wear.weather.R
import wear.weather.view.fragment.ImageTaggerFragment

class BrandTagActivity : AppCompatActivity(){
    private lateinit var imageTaggerFragment: ImageTaggerFragment
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.brand_tag_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.brand_tag_complete_button -> {

                val intent = Intent(this,BoardInputActivity::class.java)

                intent.putParcelableArrayListExtra("listBrandTagModel",
                    imageTaggerFragment.buildListBrandTagModel()
                )
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                System.out.println(imageTaggerFragment.buildListBrandTagModel())
//                setResult(RESULT_OK,intent)
                startActivity(intent)
//                finish()


                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_tag)
        imageTaggerFragment = ImageTaggerFragment()
        val photoUri = intent.getStringExtra("photoUri")
        val bundle = Bundle()
        bundle.putString("photoUri",photoUri)
        imageTaggerFragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.brand_tag_fragment, imageTaggerFragment).commit()

    }


}