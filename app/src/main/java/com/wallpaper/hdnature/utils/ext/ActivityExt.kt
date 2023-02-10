package com.wallpaper.hdnature.utils.ext

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.wallpaper.hdnature.R

fun AppCompatActivity.replaceFragmentInActivity(
    fragment: Fragment,
    @IdRes fragmentId: Int
){
    supportFragmentManager.transact { replace(fragmentId, fragment) }
}

fun AppCompatActivity.replaceFragmentInActivity(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String
){
    supportFragmentManager.transact {
        add(containerViewId, fragment, tag)
    }
}

fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String){
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun AppCompatActivity.setupActionBar(
    @IdRes toolbarId: Int,
    action: ActionBar.() -> Unit
){
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run { action() }
}

@Suppress("DEPRECATION")
fun Fragment.requestPermission(
    vararg permission: String,
    @IntRange(from = 0) requestCode: Int) =
    requestPermissions(permission, requestCode)

fun Activity.requestPermission(
    vararg permissions: String,
    @IntRange(from = 0) requestCode: Int) =
    ActivityCompat.requestPermissions(this, permissions, requestCode)

fun Activity.closeKeyboard() {
    currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
fun Activity.fitFullScreen(){
    WindowCompat.setDecorFitsSystemWindows(window, false)
}

fun Activity.fullStatusBar(){
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.decorView.systemUiVisibility =
        SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
}

fun Activity.fullScreenSystem(){
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

fun Activity.setTransparentStatusBar() {
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.setTransparentNavigation(){
    window.navigationBarColor = Color.parseColor("#01ffffff")
}

fun Activity.setNormalStatusBar() {
    window.statusBarColor = ContextCompat.getColor(this, R.color.colorOnPrimary)
//    window.navigationBarColor = Color.BLACK
}

fun Activity.setStatusBarSecondaryColor() {
    window.statusBarColor = ContextCompat.getColor(this, R.color.colorOnSecondary)
//    window.navigationBarColor = Color.BLACK
}

fun Activity.hideSystemUI() {
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
    }
}

fun Activity.lightStatusBar() {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
}
fun Activity.nonLightStatusBar() {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
}
fun Activity.lightNavigationBar() {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
}
fun Activity.nonLightNavigationBar() {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = false
}

fun Activity.showSystemUI() {
    WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
}
