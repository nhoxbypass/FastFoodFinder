package com.iceteaviet.fastfoodfinder.ui.store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by binhlt on 29/11/2016.
 */

public class CommentActivity extends AppCompatActivity implements NoticeDialog.NoticeDialogListener {

    public static final String KEY_COMMENT = "comment";
    private static final int MAX_CHAR = 140;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_remain_char)
    TextView tvRemainChar;
    @BindView(R.id.btn_post)
    Button btnPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        setupToolbar();
        setupViews();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_all_close);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_comment_or_photo);
        }
    }

    private void setupViews() {
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int remainChars = MAX_CHAR - etComment.getText().length();
                tvRemainChar.setText(String.valueOf(remainChars));
                if (remainChars < 0) {
                    etComment.setTextColor(Color.RED);
                    tvRemainChar.setTextColor(Color.RED);
                } else {
                    etComment.setTextColor(Color.BLACK);
                    tvRemainChar.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnPost.setEnabled(etComment.getText().toString().length() > 0);
            }
        });

        tvRemainChar.setText(String.valueOf(MAX_CHAR));
        btnPost.setEnabled(false);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etComment.getText().length() > MAX_CHAR) {
                    Toast.makeText(CommentActivity.this,
                            getString(R.string.cannot_post_comment, MAX_CHAR),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Comment comment = DataUtils.createUserComment(etComment.getText().toString());
                Intent data = new Intent();
                data.putExtra(KEY_COMMENT, comment);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void checkClose() {
        if (!etComment.getText().toString().isEmpty()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            NoticeDialog noticeDialog = NoticeDialog.newInstance(getString(R.string.close_comment_editor));
            noticeDialog.show(fragmentManager, "notice_dialog");
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkClose();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkClose();
    }

    @Override
    public void onClickOk() {
        finish();
    }
}
