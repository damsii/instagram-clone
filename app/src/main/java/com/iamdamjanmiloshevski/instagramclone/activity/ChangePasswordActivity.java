package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.Intent;
import android.net.Uri;
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
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText mNewPass, mVerifyPass, mCurrentPass;
    private SessionManagement session;


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
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCurrentPass = findViewById(R.id.et_current_pass);
        mNewPass = findViewById(R.id.et_new_pass);
        mVerifyPass = findViewById(R.id.et_verify_pass);
        mCurrentPass.setOnClickListener(this);
        mVerifyPass.setOnClickListener(this);
        TextView mEmail = findViewById(R.id.tv_email);
        mEmail.setOnClickListener(this);
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
                showToast(getString(R.string.password_error));
                break;
            case R.id.et_verify_pass:
                showToast(getString(R.string.password_error));
                break;
            case R.id.iv_exit:
                onBackPressed();
                break;
            case R.id.iv_confirm:
                changePassword();
                break;
            case R.id.tv_email:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.developer_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_forgot_password));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.message_forgot_pass) + " " +
                        ParseUser.getCurrentUser().getUsername());
                if (intent.resolveActivity(this.getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Send Email using..."));
                }
                startActivity(Intent.createChooser(intent, "Send Email using..."));
                break;
        }
    }

    private void changePassword() {
        boolean cancel = false;
//        final String currPassword = mCurrentPass.getText().toString();
        final String password = mNewPass.getText().toString();
        String verifiedPass = mVerifyPass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.password_match));
            mNewPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
            cancel = true;
        }
        if (TextUtils.isEmpty(verifiedPass)) {
            showToast(getString(R.string.password_match));
            cancel = true;
            mVerifyPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
        }
        if (!verifiedPass.equalsIgnoreCase(password)) {
            cancel = true;
            showToast(getString(R.string.password_match));
            mVerifyPass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_lock_red, 0, 0, 0);
        } else if (!password.equals(verifiedPass)) {
            cancel = true;
            showToast(getString(R.string.password_match));
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
