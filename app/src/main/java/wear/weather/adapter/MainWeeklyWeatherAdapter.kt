package wear.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import wear.weather.R
import wear.weather.model.WeeklyWeatherData

class MainWeeklyWeatherAdapter(private val items: ArrayList<WeeklyWeatherData>) :
    RecyclerView.Adapter<MainWeeklyWeatherAdapter.WeeklyWeatherViewHolder>() {

    inner class WeeklyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var tvDate: TextView
        private lateinit var ivWeather: ImageView
        private lateinit var tvMaxTemp: TextView
        private lateinit var tvMinTemp: TextView

        fun bind(item: WeeklyWeatherData) {
            tvDate = itemView.findViewById(R.id.item_tv_weekly_date)
            ivWeather = itemView.findViewById(R.id.item_iv_weekly_weather)
            tvMaxTemp = itemView.findViewById(R.id.item_tv_weekly_max_temp)
            tvMinTemp = itemView.findViewById(R.id.item_tv_weekly_min_temp)

            tvDate.text = item.date
            tvMaxTemp.text = item.maxTemp
            tvMinTemp.text = item.minTemp

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyWeatherViewHolder =
        WeeklyWeatherViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_weekly_weather, parent, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WeeklyWeatherViewHolder, position: Int) {
        val item = items[position]
        holder.apply { bind(item) }
    }
}

