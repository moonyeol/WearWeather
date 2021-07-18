package wear.weather.model

import java.io.Serializable

data class ContentModel(
    var uid : String? = "",
    var contents : String? = "",
    var filters : List<String>?,
    var brandTagList: List<BrandTagModel>?
): Serializable
