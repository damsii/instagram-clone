package com.iamdamjanmiloshevski.instagramclone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditEmailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditEmailActivity.class.getSimpleName();
    private EditText mEmail;
    private SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        initUI();
        getEmail();
    }

    private void initUI() {
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView mExit = toolbar.findViewById(R.id.iv_exit_email);
        ImageView mConfirm = toolbar.findViewById(R.id.iv_confirm_email);
        mEmail = findViewById(R.id.et_email);
        mConfirm.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }

    public void getEmail() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        String email = parseUser.getEmail();
        mEmail.setText(email);
        mEmail.setSelection(email.length());
    }

    private void updateEmail() {
        boolean cancel = false;
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.empty_email));
            cancel = true;
        }
        if (!isValidEmail(email)) {
            mEmail.setError(getString(R.string.valid_email));
            cancel = true;
        }
        if (!cancel) {
            ParseUser user = ParseUser.getCurrentUser();
            user.put("email", email);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Success");
                        finish();
                    }
                }
            });
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9\\.]+@[a-zA-Z0-9\\-\\_\\.]+\\.[a-zA-Z0-9]{3}");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_confirm_email:
                updateEmail();
                break;
            case R.id.iv_exit_email:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
