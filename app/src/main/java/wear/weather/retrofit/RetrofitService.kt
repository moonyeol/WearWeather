package wear.weather.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import wear.weather.util.OPEN_AIR_KEY
import wear.weather.util.OPEN_WEATHER_KEY

interface RetrofitService {
    @GET("weather?")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("APPID") APPID: String?= OPEN_WEATHER_KEY
    ): Call<JsonObject>

    @GET("onecall?")
    fun getHourlyWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("APPID") APPID: String?= OPEN_WEATHER_KEY
    ): Call<JsonObject>


    @GET("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?")
    fun getCurDust(
        @Query("stationName") stationName: String,
        @Query("dataTerm") dataTerm: String?="daily",
        @Query("pageNo") pageNo: String? = "1",
        @Query("numOfRows")numOfRows:String?="1",
        @Query("returnType")returnType:String?="json",
        @Query("ver") ver: String? = "1.0",
        @Query("serviceKey") serviceKey:String? = OPEN_AIR_KEY,
    ): Call<JsonObject>
}