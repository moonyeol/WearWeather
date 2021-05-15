package wear.weather.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import wear.weather.main.model.CurDustData
import wear.weather.main.ui.MainFragment00
import wear.weather.main.ui.MainFragment01
import wear.weather.main.ui.MainFragmentError

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