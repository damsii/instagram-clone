package com.iamdamjanmiloshevski.instagramclone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.Constants;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PHOTO = 100;
    private int EXTERNAL_STORAGE_REQUEST_CODE = 200;
    private ImageView mProfilePhoto, mExit, mConfirm;
    private TextView mChangePhoto;
    private EditText mFullName, mUsername, mWeb, mBio, mEmail, mPhone;
    private Spinner mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
        getData();
        getComponentsActions();
    }

    private void getComponentsActions() {
        mConfirm.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mChangePhoto.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mPhone.setOnClickListener(this);
        //mGender;
    }

    private void getData() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(1);
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseUser user = objects.get(0);
                        String fullName = user.getString("name") + " " + user.getString("surname");
                        mFullName.setText(fullName);
                        mUsername.setText(user.getUsername());
                        mWeb.setText(user.getString("web"));
                        mBio.setText(user.getString("about_me"));
                        mEmail.setText(user.getEmail());
                        mPhone.setText(user.getString("phone"));
                        int gender = (int) user.getNumber("gender");
                        Log.d(TAG, String.valueOf(gender));
                        switch (gender) {
                            case 0:
                                mGender.setSelection(1);
                            case 1:
                                mGender.setSelection(2);
                        }
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
                    mProfilePhoto.setImageBitmap(bitmap);
                }
            }
        });
    }

    private void initUI() {
        mProfilePhoto = findViewById(R.id.iv_profile_picture);
        mChangePhoto = findViewById(R.id.tv_change_photo);
        mFullName = findViewById(R.id.et_full_name);
        mUsername = findViewById(R.id.et_username);
        mWeb = findViewById(R.id.et_web);
        mBio = findViewById(R.id.et_bio);
        mEmail = findViewById(R.id.et_email);
        mPhone = findViewById(R.id.et_phone_number);
        mGender = findViewById(R.id.sp_gender);
        mExit = findViewById(R.id.iv_exit);
        mConfirm = findViewById(R.id.iv_confirm);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_spinner_item, Constants.gender);
        mGender.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_exit:
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            case R.id.iv_confirm:
                saveData();
            case R.id.tv_change_photo:
                processImage();
            case R.id.et_email:
                Intent intent1 = new Intent(EditProfileActivity.this, EditEmailActivity.class);
                startActivity(intent1);
        }
    }

    private String generateImageName() {
        Random random = new Random();
        int i = random.nextInt(1000);
        return "profile_img" + Integer.toString(i) + ".png";
    }

    private void getPhoto() {
        Intent iPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iPhoto, REQUEST_CODE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                mProfilePhoto.setImageBitmap(bitmap);
                Log.i(TAG, "Photo received");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    private void processImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                getPhoto();
            }
        } else {
            getPhoto();
        }
    }

    private void saveData() {
        String fullName = mFullName.getText().toString();
        String username = mUsername.getText().toString();
        String website = mWeb.getText().toString();
        String bio = mBio.getText().toString();
        Drawable drawable = mProfilePhoto.getDrawable();
        if (TextUtils.isEmpty(fullName)) {
            mFullName.setError("Name cannot be empty!");
        }
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username cannot be empty!");
        } else {

            String[] parts = fullName.split(" ");
            String name = parts[0];
            String surname = parts[1];
            ParseUser user = ParseUser.getCurrentUser();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap image = bitmapDrawable.getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ParseFile file = new ParseFile(generateImageName(), byteArray);
            user.put("name", name);
            user.put("surname", surname);
            user.put("username", username);
            user.put("about_me", bio);
            user.put("web", website);
            user.put("profile_image", file);
            switch (mGender.getSelectedItem().toString()) {
                case "Not Specified":
                    user.put("gender", 2);
                case "Male":
                    user.put("gender", 0);
                case "Female":
                    user.put("gender", 1);
            }
//                    user.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                Toast.makeText(EditProfileActivity.this, "Image shared successfully",
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.e(TAG, e.getMessage());
//                                Toast.makeText(EditProfileActivity.this,
//                                        "Image sharing failed. Please try again later!",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(EditProfileActivity.this, "Profile edited successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, e.getMessage());
                        switch (e.getMessage()) {
                            case "Account already exists for this username.":
                                mUsername.setError("Username already taken");
                        }
                    }
                }
            });

        }
    }
}
