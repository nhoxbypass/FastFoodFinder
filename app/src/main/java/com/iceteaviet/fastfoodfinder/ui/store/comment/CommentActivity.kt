package com.iceteaviet.fastfoodfinder.ui.store.comment

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.dialog.CloseConfirmDialog
import kotlinx.android.synthetic.main.activity_comment.*

/**
 * Created by binhlt on 29/11/2016.
 */

class CommentActivity : BaseActivity(), CommentContract.View {
    override lateinit var presenter: CommentContract.Presenter

    lateinit var etComment: EditText
    lateinit var tvRemainChar: TextView
    lateinit var btnPost: Button

    override val layoutId: Int
        get() = R.layout.activity_comment

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = CommentPresenter(App.getDataManager(), App.getSchedulerProvider(), this)

        etComment = et_comment
        tvRemainChar = tv_remain_char
        btnPost = btn_post

        setupToolbar()
        setupEventHandlers()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun setRemainCharCountText(remainCharCount: String) {
        tvRemainChar.text = remainCharCount
    }

    override fun updateTextColor(overLimit: Boolean) {
        @ColorInt val color = if (overLimit) Color.RED else Color.BLACK
        tvRemainChar.setTextColor(color)
        etComment.setTextColor(color)
    }

    override fun setPostButtonEnabled(enabled: Boolean) {
        btnPost.isEnabled = enabled
    }

    override fun showCommentPostFailedWarning() {
        Toast.makeText(this,
                getString(R.string.cannot_post_comment, CommentPresenter.MAX_CHAR),
                Toast.LENGTH_SHORT).show()
    }

    override fun exitWithResult(comment: Parcelable) {
        val data = Intent()
        val extras = Bundle()
        extras.putParcelable(KEY_COMMENT, comment)
        data.putExtras(extras)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun exit() {
        finish()
    }

    override fun showCloseConfirmDialog() {
        val noticeDialog = CloseConfirmDialog.newInstance(getString(R.string.close_comment_editor))
        noticeDialog.setOnClickListener(object : CloseConfirmDialog.OnClickListener {
            override fun onOkClick(dialog: DialogInterface) {
                finish()
            }

            override fun onCancelClick(dialog: DialogInterface) {
                dialog.dismiss()
            }

        })
        noticeDialog.show(supportFragmentManager, "notice_dialog")
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_all_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.add_comment_or_photo)
    }

    private fun setupEventHandlers() {
        etComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                presenter.afterCommentTextChanged(s.toString())
            }
        })

        btnPost.setOnClickListener {
            presenter.onPostButtonClick(etComment.text)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                presenter.onBackButtonClick(etComment.text)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        presenter.onBackButtonClick(etComment.text)
    }

    companion object {
        const val KEY_COMMENT = "comment"
    }
}
