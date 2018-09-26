package com.iceteaviet.fastfoodfinder.ui.settings

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.custom.HorizontalFlowLayout
import kotlinx.android.synthetic.main.dialog_store_filter.*
import kotlinx.android.synthetic.main.view_store_tag.view.*

/**
 * Created by taq on 8/12/2016.
 */

class StoreFilterDialogFragment : DialogFragment() {
    lateinit var tagContainer: HorizontalFlowLayout

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_store_filter, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagContainer = tag_container

        dialog.setTitle(R.string.subscription)
        setupTagContainer()

        btnCancel!!.setOnClickListener { dismiss() }
        btnDone!!.setOnClickListener { dismiss() }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationUpDown
    }

    private fun setupTagContainer() {
        val inflater = LayoutInflater.from(context)
        for (key in LIST_STORES) {
            val view = inflater.inflate(R.layout.view_store_tag, tagContainer, false)
            val holder = TagViewHolder(view)
            holder.setName(getStoreName(key))
            tagContainer.addView(view)
        }
    }

    private fun getStoreName(key: String): String {
        return when (key) {
            KEY_CIRCLE_K -> "Cirle K"
            KEY_BSMART -> "B’s mart"
            KEY_FAMILY_MART -> "Family mart"
            KEY_MINI_STOP -> "Ministop"
            KEY_SHOP_N_GO -> "Shop & Go"
            else -> "Unknown"
        }
    }

    inner class TagViewHolder internal constructor(private val itemView: View) {
        val tag: TextView = itemView.tv_tag

        init {
            tag.setOnClickListener { v ->
                val animator = AnimatorInflater.loadAnimator(context, R.animator.zoom_in_out)
                animator.setTarget(itemView)
                animator.duration = 100
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        Toast.makeText(context, R.string.successfully, Toast.LENGTH_SHORT).show()
                    }
                })
                animator.start()
                v.isSelected = !v.isSelected
            }
        }

        fun setName(name: String) {
            tag.text = name
        }
    }

    companion object {
        private const val KEY_CIRCLE_K = "circle_k"
        private const val KEY_MINI_STOP = "mini_stop"
        private const val KEY_FAMILY_MART = "family_mark"
        private const val KEY_BSMART = "bsmart"
        private const val KEY_SHOP_N_GO = "shop_n_go"

        private val LIST_STORES = arrayOf(KEY_BSMART, KEY_CIRCLE_K, KEY_FAMILY_MART, KEY_MINI_STOP, KEY_SHOP_N_GO)

        fun newInstance(): StoreFilterDialogFragment {
            val args = Bundle()

            val fragment = StoreFilterDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
