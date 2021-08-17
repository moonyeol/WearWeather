package wear.weather.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import wear.weather.adapter.GridFragmentRecyclerViewAdapter
import wear.weather.databinding.FragmentGridBinding

class GridFragment : Fragment() {

    var imagesSnapshot  : ListenerRegistration? = null
    lateinit var fragmentGridBinding: FragmentGridBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentGridBinding = FragmentGridBinding.inflate(layoutInflater)


        return fragmentGridBinding.root
    }

    override fun onResume() {
        super.onResume()
        fragmentGridBinding.gridfragmentRecyclerview.adapter = GridFragmentRecyclerViewAdapter()
        fragmentGridBinding.gridfragmentRecyclerview.layoutManager = GridLayoutManager(activity, 3)
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }


}