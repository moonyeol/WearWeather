package wear.weather.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import wear.weather.R
import wear.weather.databinding.FragmentTmpViewBinding


class MainTmpFragment : Fragment() {

    private lateinit var binding: FragmentTmpViewBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tmp_view, container, false)

        return binding.root
    }
}