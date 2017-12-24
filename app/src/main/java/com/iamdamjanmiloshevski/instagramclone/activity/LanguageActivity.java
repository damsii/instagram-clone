package com.iamdamjanmiloshevski.instagramclone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.Lang;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = LanguageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        initUI();

    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        TextView mEnglish = findViewById(R.id.tv_english);
        TextView mMacedonian = findViewById(R.id.tv_macedonian);

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

    public void changeLanguage(View view) {
        switch (view.getId()) {
            case R.id.tv_english:
                setApplicationLanguage(Lang.EN);
            case R.id.tv_macedonian:
                setApplicationLanguage(Lang.MK);
        }
    }

    private void setApplicationLanguage(Lang lang) {
        Log.i(TAG, lang.getLanguage());
       /* Locale myLocale = new Locale(lang.getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, AboutActivity.class);
        startActivity(refresh);
        finish();*/
    }
}
