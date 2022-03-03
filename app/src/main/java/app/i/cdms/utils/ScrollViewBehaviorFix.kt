package app.i.cdms.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.core.view.updateLayoutParams
import app.i.cdms.R
import com.google.android.material.appbar.AppBarLayout


/**
 * Version of [ScrollingViewBehavior] that checks child size and look if it fits under [AppBarLayout]
 * and if it does, then it removes [AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL] from all its views.
 */
class ScrollViewBehaviorFix : AppBarLayout.ScrollingViewBehavior {
    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        if (child.layoutParams.height > 0) return false // do not serve views that have fixed size

        val appBar = parent.getDependencies(child).findFirstAppBarLayout() ?: return false
        if (!ViewCompat.isLaidOut(appBar)) return false

        var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
        if (0 == availableHeight) {
            availableHeight = parent.height
        }
        val height = availableHeight - appBar.measuredHeight
        var heightMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(availableHeight, View.MeasureSpec.AT_MOST)
        parent.onMeasureChild(
            child,
            parentWidthMeasureSpec,
            widthUsed,
            heightMeasureSpec,
            heightUsed
        )
        return if (child.measuredHeight <= height) {
            updateScrollFlags(
                parent,
                appBar,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed,
                false
            )
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            parent.onMeasureChild(
                child,
                parentWidthMeasureSpec,
                widthUsed,
                heightMeasureSpec,
                heightUsed
            )
            true
        } else {
            updateScrollFlags(
                parent,
                appBar,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed,
                true
            )
            super.onMeasureChild(
                parent,
                child,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed
            )
        }
    }

    private fun updateScrollFlags(
        parent: CoordinatorLayout,
        appBar: AppBarLayout,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int,
        scroll: Boolean
    ) {
        toggleScrollFlags(appBar, scroll)
        appBar.forceLayout()
        parent.onMeasureChild(
            appBar,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed
        )
    }

    private fun toggleScrollFlags(appBar: AppBarLayout, scroll: Boolean) {
        appBar.forEach { view ->
            view.updateLayoutParams<AppBarLayout.LayoutParams> {
                TODO()
                // you need to add identifier in xml, it is used to save previous value of scroll
                // and restore it when content height change
                val existingTag = view.getTag(R.id.action_bar)
                if (scroll) {
                    if (existingTag == AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) {
                        scrollFlags = scrollFlags or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    }
                } else {
                    if (null == existingTag) {
                        TODO()
                        view.setTag(
                            R.id.action_bar,
                            scrollFlags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        )
                    }
                    scrollFlags = scrollFlags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL.inv()
                }
            }
        }
    }

    private fun List<View>.findFirstAppBarLayout(): AppBarLayout? {
        return firstOrNull { it is AppBarLayout } as? AppBarLayout
    }
}