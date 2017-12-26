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
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener, View.OnKeyListener {
    private final String TAG = LoginActivity.class.getSimpleName();
    private ArrayList<String> usernames = new ArrayList<>();
    // UI references.
    private AutoCompleteTextView mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private SessionManagement session;
    private RelativeLayout loginView;
    private TextView mEmailSignInButton;
    private TextView mRegisterView;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Set up the login form.
        Toast.makeText(LoginActivity.this, "Please login to continue", Toast.LENGTH_SHORT)
                .show();
        initUI();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        if (session.getUsernames() != null) {
            usernames.addAll(new ArrayList<>(session.getUsernames()));
            populateAutoComplete();
        }
        mEmailSignInButton.setOnClickListener(this);
        mRegisterView.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
    }

    private void initUI() {
        session = new SessionManagement(this);
        mUsername = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        loginView = findViewById(R.id.loginView);
        mProgressView = findViewById(R.id.progress_view);
        mRegisterView = findViewById(R.id.register);
        relativeLayout = findViewById(R.id.relativeLayout);
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
    }

    private void populateAutoComplete() {
        if (usernames != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, usernames);
            mUsername.setAdapter(adapter);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.

        mUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mUsername.getText().toString();
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
        }
        if (password.equals("")) {
            mPasswordView.setError(getResources().getString(R.string.blank_password));
            focusView = mUsername;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (!Utility.isNetworkAvailable(this)) {
                Toast.makeText(LoginActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
            } else {
                Utility.hideSoftKeyboard(this, mPasswordView);
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                loginView.setVisibility(View.GONE);
                mProgressView.setVisibility(View.VISIBLE);
                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            Log.i(TAG, "Login successful");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            loginView.setVisibility(View.VISIBLE);
                            mProgressView.setVisibility(View.GONE);
                            if (!usernames.contains(email)) {
                                usernames.add(email);
                                session.addUsernames(usernames);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Login failed. Error: " + e.getMessage());
                            switch (e.getMessage()) {
                                case "Invalid username/password.":
                                    loginView.setVisibility(View.VISIBLE);
                                    mProgressView.setVisibility(View.GONE);
                                    Log.e(TAG, e.getMessage());
                                    mUsername.setError(getResources().getString(R.string.error_username));
                                    mPasswordView.setError(getResources().getString(R.string.error_password));
                                    //showProgress(false);
                                    break;
                                case "password is required.":
                                    loginView.setVisibility(View.VISIBLE);
                                    mProgressView.setVisibility(View.GONE);
                                    Log.e(TAG, e.getMessage());
                                    mPasswordView.setError(getResources().getString(R.string.password_required));
                                    break;
                            }
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

//        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
//        });
        loginView.setVisibility(show ? View.VISIBLE : View.GONE);
        loginView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginView.setVisibility(show ? View.VISIBLE : View.GONE);
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
            case R.id.register:
                Intent gotoRegister = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(gotoRegister);
                finish();
                break;
            case R.id.email_sign_in_button:
                attemptLogin();
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
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            attemptLogin();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

