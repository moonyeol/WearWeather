package wear.weather.main.ui

import MainAddLocationAdapter
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.databinding.ActivityMainAddLocationBinding
import wear.weather.main.model.LocationData
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainAddLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainAddLocationBinding
    lateinit var adapter: MainAddLocationAdapter
    lateinit var items: MutableList<String>
    lateinit var autoCompleteTvSearch: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_add_location)
        binding.activity = this

        items = getLocationList()

        adapter = MainAddLocationAdapter(this, R.layout.item_add_location, items)
        autoCompleteTvSearch = binding.autoCompleteTvSearch

        binding.autoCompleteTvSearch.addTextChangedListener(
            CustomAutoCompleteTextChangedListener(
                this
            )

        )
        binding.autoCompleteTvSearch.setAdapter(adapter)
    }


    companion object {
        private const val TAG = "MainAddLocationActivity"
    }
}