package wear.weather.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignSelf
import com.google.android.flexbox.FlexboxLayoutManager
import wear.weather.R


class BoardInputRecyclerviewAdapter(val context: Context, val list: Array<String>, val filters:MutableList<String>):
    RecyclerView.Adapter<BoardInputRecyclerviewAdapter.ViewHolder>() {
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

        val view = LayoutInflater.from(context).inflate(
            R.layout.listview_button_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: Button = view.findViewById(R.id.list_item)

        fun bind(item: String) {
            button.text = item
            button.setOnClickListener {
                it.isSelected = !it.isSelected
                if(it.isSelected){
                    filters.add(button.text.toString())

                }else{
                    filters.remove(button.text.toString())

                }
//                if(button.text in  filters){
//                    filters.remove(button.text.toString())
//                }else{
//                    filters.add(button.text.toString())
//                }

            }
        }

    }

}
