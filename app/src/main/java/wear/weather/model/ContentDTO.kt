package wear.weather.model

import wear.weather.model.BrandTagModel
import java.util.HashMap

data class ContentDTO(var explain: String? = null,
                      var imageUrl: String? = null,
                      var uid: String? = null,
                      var nickname: String? = null,
                      var timestamp: Long? = null,
                      var filters : List<String>?,
                      var brandTagList: List<BrandTagModel>?,
                      var favoriteCount: Int = 0,
                      var favorites: MutableMap<String, Boolean> = HashMap()

) {

    data class Comment(var uid: String? = null,
                       var userId: String? = null,
                       var comment: String? = null,
                       var timestamp: Long? = null)
}
