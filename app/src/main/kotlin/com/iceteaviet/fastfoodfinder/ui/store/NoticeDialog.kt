package com.iceteaviet.fastfoodfinder.ui.store

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.R

/**
 * Created by taq on 3/11/2016.
 */

class NoticeDialog : DialogFragment() {

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var message: String? = ""

        if (arguments != null)
            arguments?.let { message = it.getString(MESSAGE) }

        val alertDialogBuilder = AlertDialog.Builder(activity!!)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(R.string.ok) { dialog, which ->
            val listener = activity as NoticeDialogListener
            listener.onClickOk()
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        return alertDialogBuilder.create()
    }

    interface NoticeDialogListener {
        fun onClickOk()
    }

    companion object {
        private const val MESSAGE = "message"

        fun newInstance(message: String): NoticeDialog {
            val args = Bundle()
            args.putString(MESSAGE, message)
            val fragment = NoticeDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
