package com.drdisagree.rushly.ui.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.drdisagree.rushly.R

fun Fragment.showAlertDialog(
    context: Context,
    title: Int,
    description: Int,
    positiveText: Int,
    onPositiveClick: () -> Unit,
    negativeText: Int,
    onNegativeClick: () -> Unit
) {
    showAlertDialog(
        context,
        context.getString(title),
        context.getString(description),
        context.getString(positiveText),
        onPositiveClick,
        context.getString(negativeText),
        onNegativeClick
    )
}

fun Fragment.showAlertDialog(
    context: Context,
    title: String,
    description: String,
    positiveText: String,
    onPositiveClick: () -> Unit,
    negativeText: String,
    onNegativeClick: () -> Unit
) {
    val builder = AlertDialog.Builder(context)
    val view = layoutInflater.inflate(R.layout.dialog_alert_dialog, null)
    builder.setView(view)

    val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
    val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
    val buttonPositive = view.findViewById<AppCompatButton>(R.id.buttonPositive)
    val buttonNegative = view.findViewById<AppCompatButton>(R.id.buttonNegative)

    tvTitle.text = title
    tvDescription.text = description
    buttonPositive.text = positiveText
    buttonNegative.text = negativeText

    val dialog = builder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    buttonPositive.setOnClickListener {
        onPositiveClick()
        dialog.dismiss()
    }

    buttonNegative.setOnClickListener {
        onNegativeClick()
        dialog.dismiss()
    }
}