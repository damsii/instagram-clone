package com.iamdamjanmiloshevski.instagramclone.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private final String TAG = SignUpActivity.class.getSimpleName();

    // UI references.
    private EditText mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegisterFormView;
    private SessionManagement session;
    private RelativeLayout relative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        // Set up the login form.
        mUsername = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        TextView mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(this);
        relative = findViewById(R.id.relative);
        mRegisterFormView = findViewById(R.id.login);
        mProgressView = findViewById(R.id.progress_view);
        View mLoginView = findViewById(R.id.login);
        mLoginView.setOnClickListener(this);
        View relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.

        mUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            mUsername.setError(getString(R.string.error_field_required));
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mUsername.setError(getString(R.string.error_invalid_email));
            focusView = mUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (!Utility.isNetworkAvailable(this)) {
                Toast.makeText(SignUpActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
            } else {
                Utility.hideSoftKeyboard(this, getCurrentFocus());
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                ParseUser user = new ParseUser();
                user.setUsername(email);
                user.setPassword(password);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            showProgress(false);
                            Log.i(TAG, "Registration successful");
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration failed. Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Registration failed. Error: " + e.getMessage());
                        }
                    }
                });
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("[a-zA-Z0-9\\.]+@[a-zA-Z0-9\\-\\_\\.]+\\.[a-zA-Z0-9]{3}");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        relative.setVisibility(show ? View.VISIBLE : View.GONE);
        relative.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                relative.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login:
                Intent gotoLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(gotoLogin);
                finish();
                break;
            case R.id.register_button:
                attemptRegistration();
                break;
            case R.id.relativeLayout:
                Utility.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                break;
            default:
                Utility.hideSoftKeyboard(getApplicationContext(), getCurrentFocus());
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
