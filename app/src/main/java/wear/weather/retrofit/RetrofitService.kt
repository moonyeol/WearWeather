package wear.weather.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import wear.weather.main.model.CurrentWeatherData

interface RetrofitService {
    @GET("weather?")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("APPID") APPID: String
    ): Call<JsonObject>

    @GET("getMsrstnAcctoRltmMesureDnsty?")
    fun getCurrentDust(
        @Query("stationName") stationName:String,
        @Query("dataTerm") dataTerm:String,
        @Query("pageNo") pageNo:Int,
        @Query("numOfRows") numOfRows:Int,
        @Query("ServiceKey") serviceKey:String,
        @Query("ver") ver:String,
        @Query("_returnType") returnType:String
    ): Call<JsonObject>
}