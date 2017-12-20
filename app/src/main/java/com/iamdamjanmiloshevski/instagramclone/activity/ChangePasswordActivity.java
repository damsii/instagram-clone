package com.iamdamjanmiloshevski.instagramclone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText mNewPass, mVerifyPass, mCurrentPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        initUI();
        getComponentsActions();
    }

    private void getComponentsActions() {
        mNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNewPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock, 0, 0, 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVerifyPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mVerifyPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock, 0, 0, 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVerifyPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    changePassword();
                    return true;
                }
                return false;
            }
        });
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCurrentPass = findViewById(R.id.et_current_pass);
        mNewPass = findViewById(R.id.et_new_pass);
        mVerifyPass = findViewById(R.id.et_verify_pass);
        mCurrentPass.setOnClickListener(this);
        mVerifyPass.setOnClickListener(this);
        ImageView mExit = toolbar.findViewById(R.id.iv_exit);
        mExit.setOnClickListener(this);
        ImageView mConfirm = toolbar.findViewById(R.id.iv_confirm);
        mConfirm.setOnClickListener(this);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(ChangePasswordActivity.this, message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_current_pass:
                showToast("Passwords must be at least 6 characters");
                break;
            case R.id.et_verify_pass:
                showToast("Passwords must be at least 6 characters");
                break;
            case R.id.iv_exit:
                onBackPressed();
                break;
            case R.id.iv_confirm:
                changePassword();
                break;
        }
    }

    private void changePassword() {
        boolean cancel = false;
//        final String currPassword = mCurrentPass.getText().toString();
        final String password = mNewPass.getText().toString();
        String verifiedPass = mVerifyPass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            showToast("Passwords don't match!");
            mNewPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
            cancel = true;
        }
        if (TextUtils.isEmpty(verifiedPass)) {
            showToast("Passwords don't match!");
            cancel = true;
            mVerifyPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
        }
        if (!verifiedPass.equalsIgnoreCase(password)) {
            cancel = true;
            showToast("Passwords don't match!");
            mVerifyPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
        } else if (!password.equals(verifiedPass)) {
            cancel = true;
            showToast("Passwords don't match!");
            mNewPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
        }
        if (!cancel) {
            ParseUser user = ParseUser.getCurrentUser();
            user.put("password", password);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Password change failed!", Toast.LENGTH_SHORT)
                                .show();
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
