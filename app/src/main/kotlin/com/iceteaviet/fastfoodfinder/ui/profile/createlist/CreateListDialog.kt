package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import kotlinx.android.synthetic.main.dialog_create_newlist.*

/**
 * Created by MyPC on 11/30/2016.
 */
class CreateListDialog : DialogFragment(), CreateListContract.View, View.OnClickListener {
    override lateinit var presenter: CreateListContract.Presenter

    private var listener: OnCreateListListener? = null

    fun setOnButtonClickListener(listener: OnCreateListListener) {
        this.listener = listener
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_create_newlist, container, false)
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()
    }

    private fun setupEventListeners() {
        ivQuit!!.setOnClickListener(this)
        btnDone!!.setOnClickListener(this)

        icon1!!.setOnClickListener(this)
        icon2!!.setOnClickListener(this)
        icon3!!.setOnClickListener(this)
        icon4!!.setOnClickListener(this)
        icon5!!.setOnClickListener(this)
        icon6!!.setOnClickListener(this)
        icon7!!.setOnClickListener(this)
        icon8!!.setOnClickListener(this)
        icon9!!.setOnClickListener(this)
        icon10!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivQuit -> {
                presenter.onCancelButtonClick()
            }

            R.id.btnDone -> {
                presenter.onDoneButtonClick(edtName!!.text.toString())
            }

            R.id.icon1 -> {
                presenter.onListIconSelect(1)
            }

            R.id.icon2 -> {
                presenter.onListIconSelect(2)
            }

            R.id.icon3 -> {
                presenter.onListIconSelect(3)
            }

            R.id.icon4 -> {
                presenter.onListIconSelect(4)
            }

            R.id.icon5 -> {
                presenter.onListIconSelect(5)
            }

            R.id.icon6 -> {
                presenter.onListIconSelect(6)
            }

            R.id.icon7 -> {
                presenter.onListIconSelect(7)
            }

            R.id.icon8 -> {
                presenter.onListIconSelect(8)
            }

            R.id.icon9 -> {
                presenter.onListIconSelect(9)
            }

            R.id.icon10 -> {
                presenter.onListIconSelect(10)
            }
        }
    }

    override fun showEmptyNameWarning() {
        Toast.makeText(context, R.string.list_name_cannot_empty, Toast.LENGTH_SHORT).show()
    }

    override fun notifyWithResult(storeName: String, iconId: Int) {
        listener?.onCreateButtonClick(storeName, iconId, this)
    }

    override fun cancel() {
        listener?.onCancel(this)
    }

    override fun updateSelectedIconUI(iconId: Int) {
        // Clear old selected icons
        showAsNotSelectedIcon(icon1)
        showAsNotSelectedIcon(icon2)
        showAsNotSelectedIcon(icon3)
        showAsNotSelectedIcon(icon4)
        showAsNotSelectedIcon(icon5)
        showAsNotSelectedIcon(icon6)
        showAsNotSelectedIcon(icon7)
        showAsNotSelectedIcon(icon8)
        showAsNotSelectedIcon(icon9)
        showAsNotSelectedIcon(icon10)

        when (iconId) {
            1 -> {
                showAsSelectedIcon(icon1)
            }
            2 -> {
                showAsSelectedIcon(icon2)
            }
            3 -> {
                showAsSelectedIcon(icon3)
            }
            4 -> {
                showAsSelectedIcon(icon4)
            }
            5 -> {
                showAsSelectedIcon(icon5)
            }
            6 -> {
                showAsSelectedIcon(icon6)
            }
            7 -> {
                showAsSelectedIcon(icon7)
            }
            8 -> {
                showAsSelectedIcon(icon8)
            }
            9 -> {
                showAsSelectedIcon(icon9)
            }
            10 -> {
                showAsSelectedIcon(icon10)
            }
        }
    }

    private fun showAsSelectedIcon(view: View) {
        view.scaleX = 1.25f
        view.scaleY = 1.25f
        view.alpha = 1f
    }

    private fun showAsNotSelectedIcon(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
        view.alpha = 0.5f
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //request
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    interface OnCreateListListener {
        fun onCreateButtonClick(name: String, iconId: Int, dialog: CreateListDialog)
        fun onCancel(dialog: CreateListDialog)
    }

    companion object {
        fun newInstance(): CreateListDialog {
            val frag = CreateListDialog()
            val args = Bundle()
            frag.arguments = args
            frag.presenter = CreateListPresenter(App.getDataManager(), frag)
            return frag
        }
    }
}
