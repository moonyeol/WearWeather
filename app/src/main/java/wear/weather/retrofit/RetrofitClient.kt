package wear.weather.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object{
        private const val OPEN_WEATHER_URL = "https://api.openweathermap.org/data/2.5/"
        private val retrofitClient: RetrofitClient = RetrofitClient()
        fun getInstance(): RetrofitClient = retrofitClient
    }
    fun buildRetrofit(): RetrofitService{
        val retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit!!.create(RetrofitService::class.java)
    }
}