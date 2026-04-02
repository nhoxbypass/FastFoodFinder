package com.iceteaviet.fastfoodfinder.ui.store.comment

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.databinding.ActivityCommentBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.dialog.CloseConfirmDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentActivity : BaseActivity() {

    private val viewModel: CommentViewModel by viewModels()

    private lateinit var binding: ActivityCommentBinding

    lateinit var etComment: EditText
    lateinit var tvRemainChar: TextView
    lateinit var btnPost: Button

    override val layoutId: Int
        get() = R.layout.activity_comment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etComment = binding.etComment
        tvRemainChar = binding.tvRemainChar
        btnPost = binding.btnPost

        setupToolbar()
        setupEventHandlers()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    tvRemainChar.text = state.remainCharCount
                    
                    @ColorInt val color = if (state.isOverLimit) Color.RED else Color.BLACK
                    tvRemainChar.setTextColor(color)
                    etComment.setTextColor(color)

                    btnPost.isEnabled = state.isPostButtonEnabled

                    when (state.event) {
                        is CommentEvent.Idle -> {}
                        is CommentEvent.ExitWithResult -> {
                            val data = Intent()
                            val extras = Bundle()
                            extras.putParcelable(KEY_COMMENT, state.event.comment)
                            data.putExtras(extras)
                            setResult(Activity.RESULT_OK, data)
                            finish()
                            viewModel.markEventConsumed()
                        }
                        is CommentEvent.Exit -> {
                            finish()
                            viewModel.markEventConsumed()
                        }
                        is CommentEvent.ShowCloseConfirmDialog -> {
                            showCloseConfirmDialog()
                            viewModel.markEventConsumed()
                        }
                        is CommentEvent.ShowCommentPostFailedWarning -> {
                            Toast.makeText(this@CommentActivity, getString(R.string.cannot_post_comment, CommentViewModel.MAX_CHAR), Toast.LENGTH_SHORT).show()
                            viewModel.markEventConsumed()
                        }
                        is CommentEvent.ShowGeneralErrorMessage -> {
                            Toast.makeText(this@CommentActivity, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    private fun showCloseConfirmDialog() {
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_all_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.add_comment_or_photo)
    }

    private fun setupEventHandlers() {
        etComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.afterCommentTextChanged(s.toString())
            }
        })

        btnPost.setOnClickListener {
            viewModel.onPostButtonClick(etComment.text)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                viewModel.onBackButtonClick(etComment.text)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.onBackButtonClick(etComment.text)
    }

    companion object {
        const val KEY_COMMENT = "comment"
    }
}