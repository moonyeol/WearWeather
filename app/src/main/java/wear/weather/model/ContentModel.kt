package wear.weather.model

data class ContentModel(
    var uid : String = "",
    var contents : String = "",
    var filters : List<String>
)
