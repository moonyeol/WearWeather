package wear.weather.model

data class WeatherDTO(
    val curTemp: Int = 0,
    val feelsLikeTemp: Int = 0,
    val tempMax: Int = 0,
    val tempMin: Int = 0,
    val weather: String = ""
)
