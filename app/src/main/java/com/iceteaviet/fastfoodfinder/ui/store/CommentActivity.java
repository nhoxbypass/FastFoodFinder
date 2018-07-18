package com.iceteaviet.fastfoodfinder.ui.store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by binhlt on 29/11/2016.
 */

public class CommentActivity extends AppCompatActivity implements NoticeDialog.NoticeDialogListener {

    private final int MAX_CHAR = 500;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_remain_char)
    TextView tvRemainChar;
    @BindView(R.id.btn_post)
    Button btnPost;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        return intent;
    }

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_all_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add comment or photo");
    }

    private void setupViews() {
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnPost.setEnabled(etComment.getText().toString().length() > 0);
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
        });

        tvRemainChar.setText(String.valueOf(MAX_CHAR));
        btnPost.setEnabled(false);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etComment.getText().length() > MAX_CHAR) {
                    Toast.makeText(CommentActivity.this,
                            "Can not post the tweet. Your content is more " + MAX_CHAR + " characters.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Comment comment = DataUtils.createUserComment(etComment.getText().toString());
                Intent data = new Intent();
                data.putExtra(Constant.COMMENT, comment);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void checkClose() {
        if (!etComment.getText().toString().isEmpty()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            NoticeDialog noticeDialog = NoticeDialog.newInstance("Are you sure?");
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
