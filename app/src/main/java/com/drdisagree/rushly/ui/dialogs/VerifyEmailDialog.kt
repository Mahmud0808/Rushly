package com.drdisagree.rushly.ui.dialogs

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.drdisagree.rushly.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("InflateParams")
fun Fragment.showVerifyEmailDialog(
    onResendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.dialog_verify_email, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edEmailAddress)
    val buttonResend = view.findViewById<AppCompatButton>(R.id.buttonSendVerifyEmail)
    val buttonClose = view.findViewById<AppCompatButton>(R.id.buttonCancelVerifyEmail)

    buttonResend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onResendClick(email)
        dialog.dismiss()
    }

    buttonClose.setOnClickListener {
        dialog.dismiss()
    }
}