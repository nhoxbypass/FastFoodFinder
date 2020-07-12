package com.iceteaviet.fastfoodfinder.ui.custom.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.R

/**
 * Created by taq on 3/11/2016.
 */

class CloseConfirmDialog : DialogFragment() {

    private var listener: OnClickListener? = null

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var message = ""

        arguments?.let {
            message = it.getString(MESSAGE, "")
        }

        val alertDialogBuilder = AlertDialog.Builder(activity!!)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(R.string.ok) { dialog, _ ->
            listener?.onOkClick(dialog)
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            listener?.onCancelClick(dialog)
        }
        return alertDialogBuilder.create()
    }

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    interface OnClickListener {
        fun onOkClick(dialog: DialogInterface)
        fun onCancelClick(dialog: DialogInterface)
    }

    companion object {
        private const val MESSAGE = "message"

        fun newInstance(message: String): CloseConfirmDialog {
            val args = Bundle()
            args.putString(MESSAGE, message)
            val fragment = CloseConfirmDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
