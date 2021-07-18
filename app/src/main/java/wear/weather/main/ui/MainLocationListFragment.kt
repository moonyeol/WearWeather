package wear.weather.main.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import wear.weather.R
import wear.weather.databinding.FragmentLocationListBinding
import wear.weather.main.adapter.MainLocationAdapter
import wear.weather.main.model.LocationData
import kotlin.math.log

class MainLocationListFragment : Fragment() {

    private lateinit var binding: FragmentLocationListBinding
    private lateinit var mainLocationAdapter: MainLocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_list, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
        val tmpArr = ArrayList<LocationData>()
        tmpArr.add(LocationData("서울","2020-06-11","sunny","15˚"))
        tmpArr.add(LocationData("부산","2020-06-11","sunny","15˚"))
        tmpArr.add(LocationData("구미","2020-06-11","sunny","15˚"))
        mainLocationAdapter = MainLocationAdapter(tmpArr)
        binding.recyclerLocation.setHasFixedSize(true)
        binding.recyclerLocation.adapter = mainLocationAdapter
        binding.recyclerLocation.layoutManager= LinearLayoutManager(this@MainLocationListFragment.activity, RecyclerView.VERTICAL, false)
        binding.recyclerLocation.addItemDecoration(DividerItemDecoration(activity!!.applicationContext,DividerItemDecoration.VERTICAL))
//        binding.constraintLayoutRootView.setBackgroundColor(Color.parseColor("#1A000000"))


    }

    override fun onPause() {
        super.onPause()
//        binding.constraintLayoutRootView.setBackgroundColor(Color.parseColor("#00ffffff"))

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    companion object {
        private const val TAG = "LocationListFragment"
    }
}