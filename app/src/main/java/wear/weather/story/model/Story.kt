package wear.weather.story.model

data class Story(
    val imageUrl: String,
    val timeStart: Long,
    val timeEnd: Long,
    val storyId: String,
    val userId: String
)
