import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import wear.weather.R

class MainAddLocationAdapter(
    val mContext: Context,
    val resource: Int,
    private val items: MutableList<String>
) : ArrayAdapter<String>(
    mContext, resource, items
), Filterable {
    private lateinit var suggestions : MutableList<String>

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_add_location, parent, false)

        val tvLocation = view.findViewById<TextView>(R.id.tv_location_name)

        tvLocation.text = items[position]
        
        return view
    }

    // 자동완성
    override fun getFilter() = mFilter

    private var mFilter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = FilterResults()
            suggestions = mutableListOf()
            if(constraint == null || constraint.isEmpty()){
                suggestions.addAll(items)
            }else{
                val filterPattern = constraint.toString().trim()
                items.forEach {
                    if (it.contains(filterPattern)){
                        suggestions.add(it)

                        Log.d(TAG, "performFiltering: true")
                    }
                    else{
                        Log.d(TAG, "performFiltering: false")
                        Log.d(TAG, "it.location: $it  filterPattern: $filterPattern")
                    }
                }
            }

            results.values = suggestions
            results.count = suggestions.size

            return results
        }


        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            Log.d(TAG, "publishResults: ")
            clear()
            addAll((results!!.values) as MutableList<String>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            Log.d(TAG, "convertResultToString resultValue: $resultValue")
            return resultValue as String
        }

    }

    companion object {
        private const val TAG = "MainAddLocationAdapter"
    }
}