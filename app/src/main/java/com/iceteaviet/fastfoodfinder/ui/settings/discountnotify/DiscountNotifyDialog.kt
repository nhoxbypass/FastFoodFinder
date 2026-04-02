package com.iceteaviet.fastfoodfinder.ui.settings.discountnotify

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.DialogStoreFilterBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiscountNotifyDialog : DialogFragment() {

    private val viewModel: DiscountNotifyViewModel by viewModels()

    private lateinit var tagContainer: RelativeLayout
    private lateinit var binding: DialogStoreFilterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogStoreFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagContainer = binding.tagContainer

        dialog?.setTitle(R.string.subscription)
        setupTagContainer(viewModel.getStoreList())
        setUpViewListener()
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state.event) {
                        is DiscountNotifyEvent.Idle -> {}
                        is DiscountNotifyEvent.CancelDialog -> {
                            dismiss()
                            viewModel.markEventConsumed()
                        }
                        is DiscountNotifyEvent.DoneDialog -> {
                            dismiss()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun setUpViewListener() {
        binding.btnCancel.setOnClickListener { viewModel.onCancelButtonClick() }
        binding.btnDone.setOnClickListener { viewModel.onDoneButtonClick() }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimationUpDown
    }

    private fun setupTagContainer(storeList: Array<String>) {
        val inflater = LayoutInflater.from(context)
        for (key in storeList) {
            val view = inflater.inflate(R.layout.view_store_tag, tagContainer, false)
            val holder = TagViewHolder(view)
            holder.setName(viewModel.getStoreName(key))
            tagContainer.addView(view)
        }
    }

    inner class TagViewHolder internal constructor(private val itemView: View) {
        val tag: TextView = itemView.findViewById(R.id.tv_tag)

        init {
            tag.setOnClickListener { v ->
                playAnimation()
                v.isSelected = !v.isSelected
            }
        }

        private fun playAnimation() {
            val animator = AnimatorInflater.loadAnimator(context, R.animator.zoom_in_out)
            animator.setTarget(itemView)
            animator.duration = 100
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    Toast.makeText(context, R.string.successfully, Toast.LENGTH_SHORT).show()
                }
            })
            animator.start()
        }

        fun setName(name: String) {
            tag.text = name
        }
    }

    companion object {
        fun newInstance(): DiscountNotifyDialog {
            val args = Bundle()
            val fragment = DiscountNotifyDialog()
            fragment.arguments = args
            return fragment
        }
    }
}