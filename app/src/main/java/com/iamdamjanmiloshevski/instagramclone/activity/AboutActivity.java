package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        initUI();

    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ImageView mFb = findViewById(R.id.fb);
        ImageView mInstagram = findViewById(R.id.instagram);
        ImageView mLinkedIn = findViewById(R.id.linkedIn);
        ImageView mWhatsapp = findViewById(R.id.whatsapp);
        ImageView mViber = findViewById(R.id.viber);
        ImageView mSms = findViewById(R.id.sms);
        TextView mContact = findViewById(R.id.contact1);
        TextView mComments = findViewById(R.id.comments);
        mFb.setOnClickListener(this);
        mInstagram.setOnClickListener(this);
        mLinkedIn.setOnClickListener(this);
        mWhatsapp.setOnClickListener(this);
        mViber.setOnClickListener(this);
        mSms.setOnClickListener(this);
        mContact.setOnClickListener(this);
        mComments.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.whatsapp:
                onWhatsappClick();
                break;
            case R.id.viber:
                onViberClick();
                break;
            case R.id.sms:
                sendSMS();
                break;
            case R.id.contact1:
                Utility.sendEmail(this, getResources().getString(R.string.developer_email),
                        getResources().getString(R.string.subject_hiring));
                break;
            case R.id.comments:
                Utility.sendEmail(this, getResources().getString(R.string.developer_email),
                        getResources().getString(R.string.subject_comments));
                break;
        }
    }

    public void sendSMS() {
        Uri uri = Uri.parse("smsto:+38978388643");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "Hello. I wanted to say hi, I like your app.");
        startActivity(it);
    }

    private void onViberClick() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setPackage("com.viber.voip");
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "Message body");
            i.setData(Uri.parse("sms:+38978388643"));
            i.putExtra("address", "+38978388643");
            i.putExtra("sms_body", "body  text");
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Viber not installed", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }
    }

    private void onWhatsappClick() {
        String smsNumber = "38978388643"; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello. I wanted to say hi, I like your app.");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");
        if (sendIntent.resolveActivity(this.getPackageManager()) == null) {
            Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(sendIntent);
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
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }
}
