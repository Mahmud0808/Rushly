package com.drdisagree.rushly.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.drdisagree.rushly.R
import com.drdisagree.rushly.ui.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.getBottomNavView(): BottomNavigationView {
    return (activity as ShoppingActivity).findViewById(R.id.bottomNavigation)
}

fun Fragment.hideBottomNavView() {
    getBottomNavView().visibility = View.GONE
}

fun Fragment.showBottomNavView() {
    getBottomNavView().visibility = View.VISIBLE
}

fun calculateOnViewColor(@ColorInt color: Int): Int {
    val darkness: Double =
        1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return if (darkness < 0.5) Color.BLACK else Color.WHITE
}

fun getPriceCalculatingOffer(price: Float, offerPercentage: Float?): Float {
    if (offerPercentage == null) {
        return price
    }

    val remainingPricePercentage = 100 - offerPercentage
    return price * remainingPricePercentage / 100
}

fun getPriceCalculatingOfferAsCurrency(price: Float, offerPercentage: Float?): String {
    if (offerPercentage == null) {
        return price.toString()
    }

    val amount = "$${String.format("%.2f", getPriceCalculatingOffer(price, offerPercentage))}"

    return if (amount.contains(".") && amount.indexOf('.') + 3 < amount.length) {
        amount.substring(0, amount.indexOf('.') + 3)
    } else amount
}

fun Context.toPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
)