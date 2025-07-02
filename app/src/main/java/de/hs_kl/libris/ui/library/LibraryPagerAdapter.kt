package de.hs_kl.libris.ui.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.hs_kl.libris.data.model.ReadingStatus

class LibraryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = ReadingStatus.entries.size

    override fun createFragment(position: Int): Fragment {
        val status = ReadingStatus.entries[position]
        return BookListFragment.newInstance(status)
    }
}