package wear.weather.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import okhttp3.OkHttpClient
import wear.weather.adapter.DetailRecyclerViewAdapter
import wear.weather.databinding.FragmentDetailBinding


class DetailViewFragment : Fragment() {

    var user: FirebaseUser? = null
    var okHttpClient: OkHttpClient? = null
    var mainView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentDetailBinding: FragmentDetailBinding = FragmentDetailBinding.inflate(inflater,container,false)
        mainView = fragmentDetailBinding.detailviewfragmentRecyclerview

        user = FirebaseAuth.getInstance().currentUser
        okHttpClient = OkHttpClient()


        return fragmentDetailBinding.root
    }

    override fun onResume() {
        super.onResume()
        mainView?.layoutManager = LinearLayoutManager(activity)
        mainView?.adapter = DetailRecyclerViewAdapter()

    }

    override fun onStop() {
        super.onStop()
    }


}
