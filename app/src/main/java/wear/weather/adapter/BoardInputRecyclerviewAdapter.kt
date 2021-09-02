package wear.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import wear.weather.R
import wear.weather.databinding.ItemListviewButtonBinding


class BoardInputRecyclerviewAdapter(val context: Context, val list: Array<String>, val filters:MutableList<String>):
    RecyclerView.Adapter<BoardInputRecyclerviewAdapter.CustomViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val lp: ViewGroup.LayoutParams = holder.itemView.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.flexGrow = 0F
        }
        holder.bind(list[position])

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemListviewButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    inner class CustomViewHolder(private val binding: ItemListviewButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val button: Button = binding.listItem

        fun bind(item: String) {
            button.text = item
            button.setOnClickListener {
                it.isSelected = !it.isSelected
                if(it.isSelected){
                    filters.add(button.text.toString())

                }else{
                    filters.remove(button.text.toString())

                }

            }
        }

    }

}
