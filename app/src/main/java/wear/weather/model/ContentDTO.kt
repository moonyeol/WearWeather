package wear.weather.model

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.MutableMap

data class ContentDTO(var explain: String? = null,
                      var imageUrl: String? = null,
                      var uid: String? = null,
                      var nickname: String? = null,
                      var timestamp: Long? = null,
                      var filters : List<String>? = ArrayList<String>(),
                      var brandTagList: List<BrandTagModel>? = ArrayList<BrandTagModel>(),
                      var favoriteCount: Int = 0,
                      var favorites: MutableMap<String, Boolean> = HashMap()

) {

    data class Comment(var uid: String? = null,
                       var nickname: String? = null,
                       var comment: String? = null,
                       var timestamp: Long? = null)
}
