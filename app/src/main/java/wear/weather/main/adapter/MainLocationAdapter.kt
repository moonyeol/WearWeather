package wear.weather.main.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import wear.weather.R

import wear.weather.main.model.LocationData

class MainLocationAdapter(private val items: ArrayList<LocationData>) :
    RecyclerView.Adapter<MainLocationAdapter.MainLocationViewHolder>() {

    inner class MainLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var tvLocation: TextView
        private lateinit var tvTime: TextView
        private lateinit var ivWeather: ImageView
        private lateinit var tvTemp: TextView

        fun bind(item: LocationData) {
            tvLocation = itemView.findViewById(R.id.item_tv_location)
            ivWeather = itemView.findViewById(R.id.item_iv_weather)
            tvTime = itemView.findViewById(R.id.item_tv_time)
            tvTemp = itemView.findViewById(R.id.item_tv_temp)

            tvLocation.text = item.location
            tvTime.text = item.time
//            ivWeather.setSr
            tvTemp.text = item.temp

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainLocationViewHolder =
        MainLocationViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_location_list, parent, false
        ))

    override fun onBindViewHolder(holder: MainLocationViewHolder, position: Int) {
            val item = items[position]
            holder.apply { bind(item) }
    }

    override fun getItemCount(): Int = items.size

}