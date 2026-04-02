package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.DialogCreateNewlistBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateListDialog : DialogFragment(), View.OnClickListener {

    private val viewModel: CreateListViewModel by viewModels()

    private lateinit var binding: DialogCreateNewlistBinding

    private var listener: OnCreateListListener? = null

    fun setOnButtonClickListener(listener: OnCreateListListener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogCreateNewlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    updateSelectedIconUI(state.selectedIconId)

                    when (val event = state.event) {
                        is CreateListEvent.Idle -> {}
                        is CreateListEvent.ShowEmptyNameWarning -> {
                            Toast.makeText(context, R.string.list_name_cannot_empty, Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is CreateListEvent.NotifyWithResult -> {
                            listener?.onCreateButtonClick(event.storeName, event.iconId, this@CreateListDialog)
                            viewModel.markEventConsumed()
                        }
                        is CreateListEvent.Cancel -> {
                            listener?.onCancel(this@CreateListDialog)
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
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
                viewModel.onCancelButtonClick()
            }

            R.id.btnDone -> {
                viewModel.onDoneButtonClick(binding.edtName.text.toString())
            }

            R.id.icon1 -> { viewModel.onListIconSelect(1) }
            R.id.icon2 -> { viewModel.onListIconSelect(2) }
            R.id.icon3 -> { viewModel.onListIconSelect(3) }
            R.id.icon4 -> { viewModel.onListIconSelect(4) }
            R.id.icon5 -> { viewModel.onListIconSelect(5) }
            R.id.icon6 -> { viewModel.onListIconSelect(6) }
            R.id.icon7 -> { viewModel.onListIconSelect(7) }
            R.id.icon8 -> { viewModel.onListIconSelect(8) }
            R.id.icon9 -> { viewModel.onListIconSelect(9) }
            R.id.icon10 -> { viewModel.onListIconSelect(10) }
        }
    }

    private fun updateSelectedIconUI(iconId: Int) {
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
            1 -> showAsSelectedIcon(binding.icon1)
            2 -> showAsSelectedIcon(binding.icon2)
            3 -> showAsSelectedIcon(binding.icon3)
            4 -> showAsSelectedIcon(binding.icon4)
            5 -> showAsSelectedIcon(binding.icon5)
            6 -> showAsSelectedIcon(binding.icon6)
            7 -> showAsSelectedIcon(binding.icon7)
            8 -> showAsSelectedIcon(binding.icon8)
            9 -> showAsSelectedIcon(binding.icon9)
            10 -> showAsSelectedIcon(binding.icon10)
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
            return frag
        }
    }
}