package uz.gita.mytaskmanager.utils

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber


fun <T : ViewBinding> T.scope(f: T.() -> Unit) {
    f(this)
}

fun timber(message: String, tag: String = "TTT") {
    Timber.tag(tag).d(message)
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.requireContext(), message, duration).show()
}

fun ViewPropertyAnimator.addEndListener(endBlock: (Boolean) -> Unit): ViewPropertyAnimator {
    var isCancel = false
    this.setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
            endBlock(isCancel)
        }

        override fun onAnimationCancel(animation: Animator?) {
            isCancel = true
        }

        override fun onAnimationRepeat(animation: Animator?) {

        }
    })
    return this
}

fun hideKeyboardFrom(context: Context, view: View) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
}