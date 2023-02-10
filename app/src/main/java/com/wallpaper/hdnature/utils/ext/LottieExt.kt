package com.wallpaper.hdnature.utils

import android.animation.Animator
import android.view.View
import android.view.View.GONE
import com.airbnb.lottie.LottieAnimationView

fun LottieAnimationView.playForward(animSpeed: Float = 1f){
    speed = animSpeed
    playAnimation()
}

fun LottieAnimationView.playReverse(animSpeed: Float = 1f){
    speed = -animSpeed
    playAnimation()
}

fun LottieAnimationView.playAndHideToggle(dur: Long = 200){
    visibility = View.VISIBLE
    playAnimation()
    animate()
        .scaleX(0f)
        .scaleY(0f)
        .setStartDelay(5)
        .setStartDelay(duration)
        .setDuration(dur)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                scaleX = 1f
                scaleY = 1f
                visibility = GONE
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        }).start()
}