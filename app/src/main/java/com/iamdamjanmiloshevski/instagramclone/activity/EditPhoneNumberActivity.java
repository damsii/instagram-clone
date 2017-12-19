package com.iamdamjanmiloshevski.instagramclone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditPhoneNumberActivity extends AppCompatActivity {
    private EditText mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_number);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPhone = findViewById(R.id.et_phone_number);
        mPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    changeNumber();
                    return true;
                }
                return false;
            }
        });
        getPhone();
    }

    private void changeNumber() {
        boolean cancel = false;
        String phone = mPhone.getText().toString();
        if (!isValidPhoneNumber(phone)) {
            mPhone.setError("Please provide a correct phone number: Format +XXX XXX XXX or country specified");
            cancel = true;
        }
        if (!cancel) {
            ParseUser user = ParseUser.getCurrentUser();
            user.put("phone", phone);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean isValidPhoneNumber(String phone) {
        return true;
    }

    private void getPhone() {
        ParseUser user = ParseUser.getCurrentUser();
        String phone = user.getString("phone");
        mPhone.setText(phone);
        mPhone.setSelection(phone.length());
    }

}
