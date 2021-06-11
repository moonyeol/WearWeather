package wear.weather.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import wear.weather.R
import wear.weather.databinding.FragmentLocationListBinding
import wear.weather.main.adapter.MainLocationAdapter
import wear.weather.main.model.LocationData

class MainLocationListFragment : Fragment() {

    private lateinit var binding: FragmentLocationListBinding
    private lateinit var mainLocationAdapter: MainLocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_list, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val tmpArr = ArrayList<LocationData>()
        tmpArr.add(LocationData("서울","2020-06-11","sunny","15˚"))
        tmpArr.add(LocationData("부산","2020-06-11","sunny","15˚"))
        tmpArr.add(LocationData("구미","2020-06-11","sunny","15˚"))
        mainLocationAdapter = MainLocationAdapter(tmpArr)
        binding.recyclerLocation.setHasFixedSize(true)
        binding.recyclerLocation.adapter = mainLocationAdapter


    }

    companion object {
        private const val TAG = "LocationListFragment"
    }
}