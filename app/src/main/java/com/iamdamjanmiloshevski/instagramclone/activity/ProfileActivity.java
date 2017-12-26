package com.iamdamjanmiloshevski.instagramclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.adapter.GridImagesAdapter;
import com.iamdamjanmiloshevski.instagramclone.adapter.UserAdapter;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private TextView mFullName, mAboutMe, mTitle;
    private GridView mImages;
    private RecyclerView mListImages;
    private ImageView mGrid, mList;
    private Intent intent;
    private boolean imageView = true;
    private SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fillUI();
        fillData();
        getUserPhotos();
    }

    private void fillUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        mProfileImage = findViewById(R.id.iv_profile_picture);
        mFullName = findViewById(R.id.tv_full_name);
        mAboutMe = findViewById(R.id.tv_about_me);
        mImages = findViewById(R.id.gv_images);
        mListImages = findViewById(R.id.lv_images);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this);
        mListImages.setLayoutManager(layoutManager);
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
        mImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iPhoto = new Intent(ProfileActivity.this, PhotoActivity.class);
                ParseObject image = (ParseObject) parent.getAdapter().getItem(position);
                iPhoto.putExtra("username", intent.getStringExtra("username"));
                iPhoto.putExtra("imageId", image.getObjectId());
                startActivity(iPhoto);
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
                        String fullName = "";
                        if (user.getString("name") != null && user.getString("surname") != null) {
                            fullName = user.getString("name") + " " + user.getString("surname");
                        }
                        mFullName.setText(fullName);
                        mAboutMe.setText(user.getString("about_me"));
                        displayImage(user);
                    }
                }
            }
        });
    }


    private void getUserPhotos() {
        if (imageView) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
            query.whereEqualTo("username", intent.getStringExtra("username"));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            GridImagesAdapter adapter = new GridImagesAdapter(ProfileActivity.this, objects);
                            mImages.setAdapter(adapter);
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
            query.whereEqualTo("username", intent.getStringExtra("username"));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            UserAdapter adapter = new UserAdapter(ProfileActivity.this, objects);
                            mListImages.setAdapter(adapter);
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
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

    public void displayView(View view) {
        switch (view.getId()) {
            case R.id.iv_grid_view:
                imageView = true;
                mImages.setVisibility(View.VISIBLE);
                mListImages.setVisibility(View.GONE);
                mGrid.setImageResource(R.drawable.ic_grid_selected);
                mList.setImageResource(R.drawable.ic_list);
                getUserPhotos();
                break;
            case R.id.iv_list_view:
                imageView = false;
                mImages.setVisibility(View.GONE);
                mListImages.setVisibility(View.VISIBLE);
                mGrid.setImageResource(R.drawable.ic_grid);
                mList.setImageResource(R.drawable.ic_list_selected);
                getUserPhotos();
                break;
        }
    }
}
