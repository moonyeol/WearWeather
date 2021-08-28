package wear.weather.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import wear.weather.view.fragment.MainFragment00
import wear.weather.view.fragment.MainFragment01
import wear.weather.view.fragment.MainFragmentError

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MainFragment00()
            }
            1 -> {
                MainFragment01()
            }
            else -> MainFragmentError()
        }

    }
}