package com.amp.nvamp.bottombar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlayerBottomSheetBehaviour<T : View>(context: Context, attributeSet: AttributeSet) : BottomSheetBehavior<T>(context, attributeSet) {
    companion object {
        fun <T : View> from(v: T): PlayerBottomSheetBehaviour<T>  {
            return BottomSheetBehavior.from<T>(v) as PlayerBottomSheetBehaviour<T>
        }
    }

    init {
        state = STATE_COLLAPSED
        maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        isGestureInsetBottomIgnored = true
    }

    @SuppressLint("RestrictedApi")
    override fun isHideableWhenDragging(): Boolean {
        return false
    }

    @SuppressLint("RestrictedApi")
    override fun handleBackInvoked() {
        if (state != STATE_HIDDEN) {
            setHideableInternal(false)
        }
        super.handleBackInvoked()
        setHideableInternal(true)
    }
}
