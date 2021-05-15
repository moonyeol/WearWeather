/*
package wear.weather.story.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import wear.weather.R
import wear.weather.main.model.CurrentWeatherData
import wear.weather.story.model.Story

class StoryViewHolder(private val items: ArrayList<Story>) :
    RecyclerView.Adapter<StoryViewHolder.StoryItemViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return if(position==0) 0
        else 1
    }

    inner class StoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var story_photo: ImageView
        private lateinit var story_plus: ImageView
        private lateinit var story_photo_seen: ImageView
        private lateinit var story_username: TextView
        private lateinit var addstory_text: TextView


        fun bind(item: Story) {
            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryItemViewHolder =
        if (viewType==0){
            StoryItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_add_story, parent, false
                )
            )
        }
        else
    {
        StoryItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_story, parent, false
            )
        )
    }
    override fun onBindViewHolder(holder: StoryItemViewHolder, position: Int) {
        val item = items[position]
        holder.apply { bind(item) }
    }

    override fun getItemCount(): Int = items.size

}*/
