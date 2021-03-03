package wear.weather.main.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CurrentWeatherData(
    @SerializedName("main")
    val weatherMain: String, // 현재 날씨
    @SerializedName("temp")
    val temp:Double, // 현재 온도
    @SerializedName("temp_max")
    val tempMax:Double,
    @SerializedName("temp_min")
    val tempMin:Double, // 일교차 -> temp_max-temp_min
    @SerializedName("feels_like")
    val feelsLike:Double, // 체감온도
    @Expose
    val fineDustValue:Double?, //미세먼지 양
    @Expose
    val fineDustGrade:Int?, // 미세먼지 등급
    @Expose
    val ultraDustValue:Double?, // 초미세먼지 양
    @Expose
    val ultraDustGrade:Int? // 초미세먼지 등급
)
