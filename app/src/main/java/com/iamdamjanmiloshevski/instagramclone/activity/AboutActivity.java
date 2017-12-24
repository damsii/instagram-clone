package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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

import java.util.List;

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
            case R.id.fb:
                sendFbMessage();
                break;
            case R.id.instagram:
                openInstagram();
                break;
            case R.id.linkedIn:
                openLinkedIn();
                break;
        }
    }

    private void openLinkedIn() {
        Intent linkedinIntent = new Intent(Intent.ACTION_SEND);
        linkedinIntent.setType("text/plain");
        linkedinIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.content_share));

        boolean linkedinAppFound = false;
        List<ResolveInfo> matches2 = getPackageManager()
                .queryIntentActivities(linkedinIntent, 0);

        for (ResolveInfo info : matches2) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(
                    "com.linkedin")) {
                linkedinIntent.setPackage(info.activityInfo.packageName);
                linkedinAppFound = true;
                break;
            }
        }

        if (linkedinAppFound) {
            startActivity(linkedinIntent);
        } else {
            Toast.makeText(AboutActivity.this, "LinkedIn app not Insatlled in your mobile", Toast.LENGTH_SHORT).show();
        }
    }

    private void openInstagram() {
        Uri uri = Uri.parse("http://instagram.com/_u/iamdamjanmiloshevski");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/iamdamjanmiloshevski")));
        }
    }

    private void sendFbMessage() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent
                .putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.message));
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");
        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AboutActivity.this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
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
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.message));
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
