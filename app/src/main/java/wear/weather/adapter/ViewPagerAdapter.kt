package wear.weather.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import wear.weather.view.fragment.FirstIntrouduceFragment
import wear.weather.view.fragment.ForthIntroduceFragment
import wear.weather.view.fragment.SecondIntroduceFragment
import wear.weather.view.fragment.ThirdIntroduceFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val mFragmentList = ArrayList<Fragment>()

    override fun getCount(): Int{
        return mFragmentList.size
    }

    fun createFragment() {
        mFragmentList.add(FirstIntrouduceFragment())
        mFragmentList.add(SecondIntroduceFragment())
        mFragmentList.add(ThirdIntroduceFragment())
        mFragmentList.add(ForthIntroduceFragment())
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

}