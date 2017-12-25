package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.ParseUser;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initUI();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        TextView mEditProfile = findViewById(R.id.tv_edit_profile);
        TextView mChangePassword = findViewById(R.id.tv_change_password);
        TextView mLanguage = findViewById(R.id.tv_language);
        TextView mAbout = findViewById(R.id.tv_about);
        TextView mLogout = findViewById(R.id.tv_log_out);
        TextView mShare = findViewById(R.id.tv_share_app);
        mEditProfile.setOnClickListener(this);
        mChangePassword.setOnClickListener(this);
        mLanguage.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        mShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_profile:
                Intent intent = new Intent(OptionsActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_change_password:
                Intent intent1 = new Intent(OptionsActivity.this, ChangePasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_language:
                Intent iOptions = new Intent(OptionsActivity.this, LanguageActivity.class);
                startActivity(iOptions);
                break;
            case R.id.tv_about:
                Intent intent2 = new Intent(OptionsActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_log_out:
                logout();
                break;
            case R.id.tv_share_app:
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:")); // only email apps should handle this
                email.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_share));
                email.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.content_share));
                if (email.resolveActivity(this.getPackageManager()) != null) {
                    startActivity(Intent.createChooser(email, "Send Email using..."));
                }
                startActivity(Intent.createChooser(email, "Send Email using..."));
                break;
        }

    }

    private void logout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.logout)
                .setMessage(R.string.logout_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
                        intent.putExtra("finish", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
