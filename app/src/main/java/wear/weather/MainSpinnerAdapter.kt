package wear.weather

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MainSpinnerAdapter(context: Context, spinnerImage: Int, spinnerNames: Array<String>) :
    ArrayAdapter<String>(context, spinnerImage, spinnerNames) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent)
    }

    inner class ViewHolder {
        private lateinit var spinnerTv: TextView
        private lateinit var spinnerIv: ImageView
    }
}