package wear.weather.model

import java.io.Serializable

class UserModel (
    var uid : String = "",
    var email : String = "",
    var nickname : String = ""
) : Serializable