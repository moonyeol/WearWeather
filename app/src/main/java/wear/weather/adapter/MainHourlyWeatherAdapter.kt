package wear.weather.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import wear.weather.R
import wear.weather.model.HourlyWeatherData

class MainHourlyWeatherAdapter(private val items: ArrayList<HourlyWeatherData>) :
    RecyclerView.Adapter<MainHourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    inner class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var tvHour: TextView
        private lateinit var ivWeather: ImageView
        private lateinit var tvPop: TextView
        private lateinit var tvTemp: TextView

        fun bind(item: HourlyWeatherData) {
            tvHour = itemView.findViewById(R.id.item_tv_hourly_time)
            ivWeather = itemView.findViewById(R.id.item_iv_hourly_weather)
            tvPop = itemView.findViewById(R.id.item_tv_hourly_pop)
            tvTemp = itemView.findViewById(R.id.item_tv_hourly_temp)

            tvHour.text = item.time
//            ivWeather.setSr
            tvPop.text = "10%"
            tvTemp.text = item.temp
            Log.d("TAG", "bind: fff")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder =
        HourlyWeatherViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_hourly_weather, parent, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val item = items[position]
        holder.apply { bind(item) }
    }
}