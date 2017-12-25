package com.iamdamjanmiloshevski.instagramclone.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.DeleteCallback;
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
    private SessionManagement session;

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

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
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
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PhotoActivity.this);
                dialog.setContentView(R.layout.photo_options);


                TextView mEdit = dialog.findViewById(R.id.tv_edit_photo);
                TextView mDelete = dialog.findViewById(R.id.tv_delete_photo);

                mEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iEditPhoto = new Intent(PhotoActivity.this, EditInfoActivity.class);
                        iEditPhoto.putExtra("username", getIntent().getStringExtra("username"));
                        iEditPhoto.putExtra("imageId", getIntent().getStringExtra("imageId"));
                        startActivity(iEditPhoto);
                        dialog.dismiss();
                    }
                });
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog confirmDeletionDialog = new Dialog(PhotoActivity.this);
                        confirmDeletionDialog.setContentView(R.layout.confirm_deletion);

                        Button mDelete = confirmDeletionDialog.findViewById(R.id.bt_delete);
                        Button mCancel = confirmDeletionDialog.findViewById(R.id.bt_cancel);

                        mDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteImage();
                            }
                        });
                        mCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDeletionDialog.dismiss();
                                dialog.dismiss();
                            }
                        });
                        confirmDeletionDialog.show();
                    }
                });
                dialog.show();
            }
        });
    }

    private void deleteImage() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("objectId", getIntent().getStringExtra("imageId"));
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        objects.get(0).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(PhotoActivity.this, "Image deleted successfully",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
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
