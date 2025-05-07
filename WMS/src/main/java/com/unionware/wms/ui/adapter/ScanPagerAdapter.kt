@file:Suppress("DEPRECATION")

package com.unionware.wms.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ScanPagerAdapter(fm: FragmentManager, titles: MutableList<String>) :
    FragmentStatePagerAdapter(fm) {
    var fragments: MutableList<Fragment> = arrayListOf()
    var titles: MutableList<String> = titles

    override fun getCount() = fragments.size
    override fun getItem(postion: Int) = fragments[postion]

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}