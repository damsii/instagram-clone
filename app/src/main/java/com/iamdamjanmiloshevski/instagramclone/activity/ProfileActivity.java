package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private TextView mFullName, mAboutMe, mTitle;
    private GridView mImages;
    private ListView mListImages;
    private ImageView mGrid, mList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fillUI();
        fillData();

    }

    private void fillUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProfileImage = findViewById(R.id.iv_profile_picture);
        mFullName = findViewById(R.id.tv_full_name);
        mAboutMe = findViewById(R.id.tv_about_me);
        mImages = findViewById(R.id.gv_images);
        mListImages = findViewById(R.id.lv_images);
        mGrid = findViewById(R.id.iv_grid_view);
        mList = findViewById(R.id.iv_list_view);
        intent = getIntent();
        mTitle = toolbar.findViewById(R.id.title);
        mTitle.setText(intent.getStringExtra("username"));
        findViewById(R.id.bt_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillData() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(1);
        query.whereEqualTo("username", intent.getStringExtra("username"));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseUser user = objects.get(0);
                        String fullName = user.getString("name") + " " + user.getString("surname");
                        mFullName.setText(fullName);
                        mAboutMe.setText(user.getString("about_me"));
                        displayImage(user);
                    }
                }
            }
        });
    }

    private void displayImage(ParseUser user) {
        ParseFile file = user.getParseFile("profile_image");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null && data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    mProfileImage.setImageBitmap(bitmap);
                }
            }
        });
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
}
