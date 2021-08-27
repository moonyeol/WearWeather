package wear.weather.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import wear.weather.databinding.FragmentTmpViewBinding


class MainTmpFragment : Fragment() {

    private lateinit var binding: FragmentTmpViewBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTmpViewBinding.inflate(layoutInflater, container, false)


        return binding.root
    }
}