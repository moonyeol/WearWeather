package wear.weather.handler

import wear.weather.view.fragment.TagFragment

interface TagCallbackHandler {
    fun onTagEvent(tag: TagFragment?, eventName: String?, data: Any?): Boolean
    fun onTagCreated(tag: TagFragment?)
}