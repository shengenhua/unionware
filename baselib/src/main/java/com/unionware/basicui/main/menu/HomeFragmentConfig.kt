package com.unionware.basicui.main.menu

import androidx.fragment.app.Fragment

/**
 * Author: sheng
 * Date:2025/5/9
 */
class HomeFragmentConfig {
    companion object {
        private var firstFragment: Fragment? = null
        private var meFragment: Fragment? = null

        @JvmStatic
        fun getFirstFragment(): Fragment? {
            return firstFragment
        }

        @JvmStatic
        fun setFirstFragment(fragment: Fragment) {
            firstFragment = fragment
        }

        @JvmStatic
        fun getMeFragment(): Fragment? {
            return meFragment
        }

        @JvmStatic
        fun setMeFragment(fragment: Fragment) {
            meFragment = fragment
        }
    }
}