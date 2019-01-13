package com.iceteaviet.fastfoodfinder.ui.store

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_comment.*

/**
 * Created by binhlt on 29/11/2016.
 */

class CommentActivity : BaseActivity(), NoticeDialog.NoticeDialogListener {
    lateinit var etComment: EditText
    lateinit var tvRemainChar: TextView
    lateinit var btnPost: Button

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etComment = et_comment
        tvRemainChar = tv_remain_char
        btnPost = btn_post

        setupToolbar()
        setupViews()
    }

    override val layoutId: Int
        get() = R.layout.activity_comment

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_all_close)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setTitle(R.string.add_comment_or_photo)
        }
    }

    private fun setupViews() {
        etComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val remainChars = MAX_CHAR - etComment.text.length
                tvRemainChar.text = remainChars.toString()
                if (remainChars < 0) {
                    etComment.setTextColor(Color.RED)
                    tvRemainChar.setTextColor(Color.RED)
                } else {
                    etComment.setTextColor(Color.BLACK)
                    tvRemainChar.setTextColor(Color.BLACK)
                }
            }

            override fun afterTextChanged(s: Editable) {
                btnPost.isEnabled = etComment.text.toString().isNotEmpty()
            }
        })

        tvRemainChar.text = MAX_CHAR.toString()
        btnPost.isEnabled = false
        btnPost.setOnClickListener(View.OnClickListener {
            if (etComment.text.length > MAX_CHAR) {
                Toast.makeText(this@CommentActivity,
                        getString(R.string.cannot_post_comment, MAX_CHAR),
                        Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val comment = Comment(dataManager.getCurrentUser()!!.name, dataManager.getCurrentUser()!!.photoUrl,
                    etComment.text.toString(), "", System.currentTimeMillis())
            val data = Intent()
            val extras = Bundle()
            extras.putParcelable(KEY_COMMENT, comment)
            data.putExtras(extras)
            setResult(RESULT_OK, data)
            finish()
        })
    }

    private fun checkClose() {
        if (!etComment.text.toString().isEmpty()) {
            val fragmentManager = supportFragmentManager
            val noticeDialog = NoticeDialog.newInstance(getString(R.string.close_comment_editor))
            noticeDialog.show(fragmentManager, "notice_dialog")
        } else {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                checkClose()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        checkClose()
    }

    override fun onClickOk() {
        finish()
    }

    companion object {
        const val KEY_COMMENT = "comment"
        private const val MAX_CHAR = 140
    }
}
