package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.DialogCreateNewlistBinding

/**
 * Created by MyPC on 11/30/2016.
 */
class CreateListDialog : DialogFragment(), CreateListContract.View, View.OnClickListener {
    override lateinit var presenter: CreateListContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: DialogCreateNewlistBinding

    private var listener: OnCreateListListener? = null

    fun setOnButtonClickListener(listener: OnCreateListListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogCreateNewlistBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.dialog_create_newlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    private fun setupEventListeners() {
        binding.ivQuit.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)

        binding.icon1.setOnClickListener(this)
        binding.icon2.setOnClickListener(this)
        binding.icon3.setOnClickListener(this)
        binding.icon4.setOnClickListener(this)
        binding.icon5.setOnClickListener(this)
        binding.icon6.setOnClickListener(this)
        binding.icon7.setOnClickListener(this)
        binding.icon8.setOnClickListener(this)
        binding.icon9.setOnClickListener(this)
        binding.icon10.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivQuit -> {
                presenter.onCancelButtonClick()
            }

            R.id.btnDone -> {
                presenter.onDoneButtonClick(binding.edtName.text.toString())
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
        showAsNotSelectedIcon(binding.icon1)
        showAsNotSelectedIcon(binding.icon2)
        showAsNotSelectedIcon(binding.icon3)
        showAsNotSelectedIcon(binding.icon4)
        showAsNotSelectedIcon(binding.icon5)
        showAsNotSelectedIcon(binding.icon6)
        showAsNotSelectedIcon(binding.icon7)
        showAsNotSelectedIcon(binding.icon8)
        showAsNotSelectedIcon(binding.icon9)
        showAsNotSelectedIcon(binding.icon10)

        when (iconId) {
            1 -> {
                showAsSelectedIcon(binding.icon1)
            }

            2 -> {
                showAsSelectedIcon(binding.icon2)
            }

            3 -> {
                showAsSelectedIcon(binding.icon3)
            }

            4 -> {
                showAsSelectedIcon(binding.icon4)
            }

            5 -> {
                showAsSelectedIcon(binding.icon5)
            }

            6 -> {
                showAsSelectedIcon(binding.icon6)
            }

            7 -> {
                showAsSelectedIcon(binding.icon7)
            }

            8 -> {
                showAsSelectedIcon(binding.icon8)
            }

            9 -> {
                showAsSelectedIcon(binding.icon9)
            }

            10 -> {
                showAsSelectedIcon(binding.icon10)
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
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
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
            frag.presenter = CreateListPresenter(App.getDataManager(), App.getSchedulerProvider(), frag)
            return frag
        }
    }
}