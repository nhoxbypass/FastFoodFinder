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
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.DialogStoreFilterBinding

/**
 * Created by taq on 8/12/2016.
 */

class DiscountNotifyDialog : DialogFragment(), DiscountNotifyContract.View {
    private lateinit var tagContainer: RelativeLayout
    override lateinit var presenter: DiscountNotifyContract.Presenter

    /**
     * Views Ref
     */
    private lateinit var binding: DialogStoreFilterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogStoreFilterBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.dialog_store_filter, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagContainer = binding.tagContainer

        dialog?.setTitle(R.string.subscription)
        presenter.onSetupTagContainer()
        setUpViewListener()
    }

    fun setUpViewListener() {
        binding.btnCancel.setOnClickListener { presenter.onCancelButtonClick() }
        binding.btnDone.setOnClickListener { presenter.onDoneButtonClick() }
    }

    override fun cancelDialog() {
        dismiss()
    }

    override fun doneDialog() {
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimationUpDown
    }

    override fun setupTagContainer(storeList: Array<String>) {
        val inflater = LayoutInflater.from(context)
        for (key in storeList) {
            val view = inflater.inflate(R.layout.view_store_tag, tagContainer, false)
            val holder = TagViewHolder(view)
            holder.setName(presenter.getStoreName(key))
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
            fragment.presenter = DiscountNotifyPresenter(fragment)
            return fragment
        }
    }
}