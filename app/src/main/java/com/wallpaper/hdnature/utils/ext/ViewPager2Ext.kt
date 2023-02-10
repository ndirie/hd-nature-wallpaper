package com.wallpaper.hdnature.utils.ext

import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.autoScroll(interval: Long){
    val handle = Handler(Looper.getMainLooper())
    var scrollPosition = 0

    val runnable = Runnable { currentItem = ++currentItem }

    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            handle.removeMessages(0)

            if (position < (adapter?.itemCount ?: 0))
                handle.postDelayed(runnable, interval)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_DRAGGING)
                handle.removeMessages(0)
        }
    })
    handle.post(runnable)
}
