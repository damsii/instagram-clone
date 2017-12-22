package com.iamdamjanmiloshevski.instagramclone.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = PhotoActivity.class.getSimpleName();
    private ImageView profile_picture;
    private TextView username;
    private ImageView image, more;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        initUI();
        displayData();
    }

    private void displayData() {
        username.setText(getIntent().getStringExtra("username"));
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("objectId", getIntent().getStringExtra("imageId"));
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseFile file = objects.get(0).getParseFile("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    image.setImageBitmap(bitmap);
                                }
                            }
                        });
                        description.setText(objects.get(0).getString("description"));
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", getIntent().getStringExtra("username"));
        userQuery.setLimit(1);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseFile profile_image = objects.get(0).getParseFile("profile_image");
                        profile_image.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profile_picture.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        more = toolbar.findViewById(R.id.iv_more);
        if (ParseUser.getCurrentUser().getUsername().equals(getIntent().getStringExtra("username"))) {
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.INVISIBLE);
        }
        profile_picture = findViewById(R.id.profile_picture);
        username = findViewById(R.id.tv_username);
        image = findViewById(R.id.iv_image);
        description = findViewById(R.id.tv_description);
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
