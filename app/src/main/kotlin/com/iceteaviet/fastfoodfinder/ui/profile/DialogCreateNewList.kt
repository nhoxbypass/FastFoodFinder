package com.iceteaviet.fastfoodfinder.ui.profile

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
import com.iceteaviet.fastfoodfinder.R
import kotlinx.android.synthetic.main.dialog_create_newlist.*
import java.util.*

/**
 * Created by MyPC on 11/30/2016.
 */
class DialogCreateNewList : DialogFragment() {
    private var listName: ArrayList<String>? = null
    private var mListener: OnCreateListListener? = null
    private var idIconSource = R.drawable.ic_profile_list_1

    fun setOnButtonClickListener(listener: OnCreateListListener) {
        mListener = listener
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null)
            arguments?.let { listName = it.getStringArrayList(KEY_LIST_NAME) }
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.dialog_create_newlist, container, false)
        return rootView
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivQuit!!.setOnClickListener { dismiss() }
        getIdIconSource()
        btnDone!!.setOnClickListener {
            if (edtName!!.text.isEmpty()) {
                Toast.makeText(context, R.string.list_name_cannot_empty, Toast.LENGTH_SHORT).show()
            } else {
                var check = true
                for (i in listName!!.indices) {
                    if (edtName!!.text.toString() == listName!![i]) {
                        Toast.makeText(context, R.string.list_name_already_exists, Toast.LENGTH_SHORT).show()
                        check = false
                    }
                }
                if (check) {
                    mListener!!.onButtonClick(edtName!!.text.toString(), idIconSource)
                    listName!!.add(edtName!!.text.toString())
                    dismiss()
                }
            }
        }


    }

    private fun getIdIconSource() {
        icon1!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_2
            icon1!!.scaleX = 1.25f
            icon1!!.scaleY = 1.25f
            icon1!!.alpha = 1f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon2!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_4
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1.25f
            icon2!!.scaleY = 1.25f
            icon2!!.alpha = 1f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon3!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_5
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1.25f
            icon3!!.scaleY = 1.25f
            icon3!!.alpha = 1f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon4!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_6
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1.25f
            icon4!!.scaleY = 1.25f
            icon4!!.alpha = 1f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon5!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_7
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1.25f
            icon5!!.scaleY = 1.25f
            icon5!!.alpha = 1f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon6!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_8
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1.25f
            icon6!!.scaleY = 1.25f
            icon6!!.alpha = 1f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon7!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_9
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1.25f
            icon7!!.scaleY = 1.25f
            icon7!!.alpha = 1f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon8!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_10
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1.25f
            icon8!!.scaleY = 1.25f
            icon8!!.alpha = 1f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon9!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_11
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1.25f
            icon9!!.scaleY = 1.25f
            icon9!!.alpha = 1f
            icon10!!.scaleX = 1f
            icon10!!.scaleY = 1f
            icon10!!.alpha = 0.5f
        }
        icon10!!.setOnClickListener {
            idIconSource = R.drawable.ic_profile_list_3
            icon1!!.scaleX = 1f
            icon1!!.scaleY = 1f
            icon1!!.alpha = 0.5f
            icon2!!.scaleX = 1f
            icon2!!.scaleY = 1f
            icon2!!.alpha = 0.5f
            icon3!!.scaleX = 1f
            icon3!!.scaleY = 1f
            icon3!!.alpha = 0.5f
            icon4!!.scaleX = 1f
            icon4!!.scaleY = 1f
            icon4!!.alpha = 0.5f
            icon5!!.scaleX = 1f
            icon5!!.scaleY = 1f
            icon5!!.alpha = 0.5f
            icon6!!.scaleX = 1f
            icon6!!.scaleY = 1f
            icon6!!.alpha = 0.5f
            icon7!!.scaleX = 1f
            icon7!!.scaleY = 1f
            icon7!!.alpha = 0.5f
            icon8!!.scaleX = 1f
            icon8!!.scaleY = 1f
            icon8!!.alpha = 0.5f
            icon9!!.scaleX = 1f
            icon9!!.scaleY = 1f
            icon9!!.alpha = 0.5f
            icon10!!.scaleX = 1.25f
            icon10!!.scaleY = 1.25f
            icon10!!.alpha = 1f
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //request
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    interface OnCreateListListener {
        fun onButtonClick(name: String, idIconSource: Int)
    }

    companion object {
        private const val KEY_LIST_NAME = "list_name"

        fun newInstance(listName: ArrayList<String>): DialogCreateNewList {
            val frag = DialogCreateNewList()
            val args = Bundle()
            args.putStringArrayList(KEY_LIST_NAME, listName)
            frag.arguments = args
            return frag
        }
    }
}
