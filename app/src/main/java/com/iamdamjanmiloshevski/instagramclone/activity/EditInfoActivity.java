package com.iamdamjanmiloshevski.instagramclone.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class EditInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditInfoActivity.class.getSimpleName();
    private ImageView profile_picture;
    private TextView username;
    private ImageView image, more;
    private EditText description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        initUI();
        displayData();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView mExit = toolbar.findViewById(R.id.iv_exit);
        ImageView mConfirm = toolbar.findViewById(R.id.iv_confirm);
        mExit.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        profile_picture = findViewById(R.id.profile_picture);
        username = findViewById(R.id.tv_username);
        image = findViewById(R.id.iv_image);
        description = findViewById(R.id.et_description);

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
                        String desc = objects.get(0).getString("description");
                        description.setText(desc);
                        description.setSelection(desc.length());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit:
                onBackPressed();
                break;
            case R.id.iv_confirm:
                saveInfo();
                break;
        }
    }

    private void saveInfo() {
        final String desc = description.getText().toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("objectId", getIntent().getStringExtra("imageId"));
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        objects.get(0).put("description", desc);
                        objects.get(0).saveInBackground();
                        finish();
                    }
                }
            }
        });
    }
}
