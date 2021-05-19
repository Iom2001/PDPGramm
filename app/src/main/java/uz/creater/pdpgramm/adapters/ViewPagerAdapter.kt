package uz.creater.pdpgramm.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.creater.pdpgramm.fragments.ChatsFragment
import uz.creater.pdpgramm.fragments.GroupsFragment

class ViewPagerAdapter(private var list: List<Int>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        if (list[position] == 1) {
            return ChatsFragment()
        }
        return GroupsFragment()
    }
}