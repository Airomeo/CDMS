package app.i.cdms.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.view.PixelCopy
import androidx.fragment.app.Fragment

/**
 * Synchronize the color of NavigationBar with the color of the pixel in the bottom right corner from the visible display window.
 */
fun Fragment.syncNavigationBarColorWithPixel() {
    activity?.syncNavigationBarColorWithPixel()
}

/**
 * Synchronize the color of NavigationBar with the color of the pixel in the bottom right corner from the visible display window.
 */
fun Activity.syncNavigationBarColorWithPixel() {
    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)
    val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    PixelCopy.request(
        window,
        Rect(rect.right - 1, rect.bottom - 1, rect.right, rect.bottom),
        bitmap,
        { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                val color = bitmap.getPixel(0, 0)
                window.navigationBarColor = color
                bitmap.recycle()
            }
        },
        Handler(mainLooper)
    )
}