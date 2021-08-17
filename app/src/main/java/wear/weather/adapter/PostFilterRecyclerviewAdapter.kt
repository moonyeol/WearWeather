package wear.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import wear.weather.databinding.ItemPostDetailFilterBinding


class PostFilterRecyclerviewAdapter(val context: Context, val list: Array<String>, val filters:MutableList<String>):
    RecyclerView.Adapter<PostFilterRecyclerviewAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lp: ViewGroup.LayoutParams = holder.itemView.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.flexGrow = 0F
        }

        holder.bind(list[position])

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostDetailFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemPostDetailFilterBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: String) {
            binding.detailviewitemFilterTextview.text = item
        }

    }

}
