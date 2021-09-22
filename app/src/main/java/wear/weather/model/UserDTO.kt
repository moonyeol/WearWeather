package wear.weather.model

import java.io.Serializable
import java.util.HashMap

class UserDTO (
    var nickname : String = "",
    var followerCount: Int = 0,
    var followers: MutableMap<String, Boolean> = HashMap(),

    var followingCount: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap(),
    var profileImage : String =""
) : Serializable