@file:Suppress("DEPRECATION")

package com.unionware.wms.ui.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var fragments: MutableList<Fragment> = arrayListOf()

    override fun getCount() = fragments.size
    override fun getItem(postion: Int) = fragments[postion]

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
    }
}