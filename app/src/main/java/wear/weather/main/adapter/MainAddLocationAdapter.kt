package wear.weather.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import wear.weather.R
import wear.weather.main.adapter.MainAddLocationAdapter.LocationItemViewHolder
import wear.weather.main.model.CurrentWeatherData

class MainAddLocationAdapter(private val items: ArrayList<CurrentWeatherData>) :
    RecyclerView.Adapter<LocationItemViewHolder>() {

    inner class LocationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var tvLocation: TextView
        private lateinit var tvTime: TextView
        private lateinit var tvTemp: TextView
        private lateinit var tvWeather: TextView
        fun bind(item: CurrentWeatherData) {
            tvLocation = itemView.findViewById(R.id.item_tv_location)
            tvTime = itemView.findViewById(R.id.item_tv_time)
            tvTemp = itemView.findViewById(R.id.item_tv_temp)
            tvWeather = itemView.findViewById(R.id.item_tv_weather)
            tvLocation.text = item.location
            tvTime.text = item.time
            tvTemp.text = item.temp.toString()
            tvWeather.text = item.weatherMain
            Log.d("TAG", "bind: fff")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationItemViewHolder =
        LocationItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_add_location, parent, false
            )
        )
    override fun onBindViewHolder(holder: LocationItemViewHolder, position: Int) {
        val item = items[position]
        holder.apply { bind(item) }
    }

    override fun getItemCount(): Int = items.size

}