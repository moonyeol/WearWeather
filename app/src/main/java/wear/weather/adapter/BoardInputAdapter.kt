package wear.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import wear.weather.R

class BoardInputAdapter(val context: Context, val list: Array<String>): BaseAdapter() {
    override fun getCount(): Int {

        return list.size
    }

    override fun getItem(p0: Int): Any {
        return 0
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.listview_button_item,null)
        val item = view.findViewById<Button>(R.id.list_item)
        item.text = list[position]

        return view
    }

}