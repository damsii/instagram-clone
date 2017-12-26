package com.iamdamjanmiloshevski.instagramclone.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.utility.SessionManagement;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private ScrollView mEditProfileView;
    private RelativeLayout mProgressView;
    private Toolbar toolbar;
    private String gender = "";
    private SessionManagement session;
    private String oldUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = findViewById(R.id.toolbar);
        mExit = toolbar.findViewById(R.id.iv_exit);
        mConfirm = toolbar.findViewById(R.id.iv_confirm);
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
        mGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getData() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(1);
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        showProgress(true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        showProgress(false);
                        ParseUser user = objects.get(0);
                        String fullName = "";
                        if (user.getString("name") != null && user.getString("surname") != null) {
                            fullName = user.getString("name") + " " + user.getString("surname");
                        }
                        mFullName.setText(fullName);
                        mUsername.setText(user.getUsername());
                        oldUsername = user.getUsername();
                        mWeb.setText(user.getString("web"));
                        mBio.setText(user.getString("about_me"));
                        mEmail.setText(user.getEmail());
                        mPhone.setText(user.getString("phone"));
                        int gender = (int) user.getNumber("gender");
                        Log.d(TAG, String.valueOf(gender));
                        switch (gender) {
                            case 0:
                                mGender.setSelection(1);
                                break;
                            case 1:
                                mGender.setSelection(2);
                                break;
                            case 2:
                                mGender.setSelection(0);
                                break;
                        }
                        displayImage(user);
                    }
                }
            }
        });
    }

    private void displayImage(ParseUser user) {
        ParseFile file = user.getParseFile("profile_image");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mProfilePhoto.setImageBitmap(bitmap);
                    }
                }
            });
        } else {
            mProfilePhoto.setImageResource(R.drawable.instagram);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initUI() {
        session = new SessionManagement(this);
        if (session.getLanguage() != null) {
            Utility.setApplicationLanguage(session.getLanguage(), this);
        }
        mProfilePhoto = findViewById(R.id.iv_profile_picture);
        mChangePhoto = findViewById(R.id.tv_change_photo);
        mFullName = findViewById(R.id.et_full_name);
        mUsername = findViewById(R.id.et_username);
        mWeb = findViewById(R.id.et_web);
        mBio = findViewById(R.id.et_bio);
        mEmail = findViewById(R.id.et_email);
        mPhone = findViewById(R.id.et_phone_number);
        mGender = findViewById(R.id.sp_gender);
        mEditProfileView = findViewById(R.id.edit_profile_view);
        mProgressView = findViewById(R.id.progress_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_spinner_item, getGender());
        mGender.setAdapter(adapter);
    }

    private List<String> getGender() {
        List<String> gender = new ArrayList<>();
        gender.add(getResources().getString(R.string.sex1));
        gender.add(getResources().getString(R.string.sex2));
        gender.add(getResources().getString(R.string.sex3));

        return gender;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mEditProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
        // mConfirm.setVisibility(show ? View.GONE : View.VISIBLE);
        mEditProfileView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEditProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        mConfirm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mConfirm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_exit:
                onBackPressed();
                break;
            case R.id.iv_confirm:
                saveData();
                Intent intent1 = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.tv_change_photo:
                processImage();
                break;
            case R.id.et_email:
                Intent intent2 = new Intent(EditProfileActivity.this, EditEmailActivity.class);
                startActivity(intent2);
                break;
            case R.id.et_phone_number:
                Intent intent3 = new Intent(EditProfileActivity.this, EditPhoneNumberActivity.class);
                startActivity(intent3);
                break;
        }
    }

    private String generateImageName() {
        Random random = new Random();
        int i = random.nextInt(1000);
        return "profile_img" + Integer.toString(i) + ".png";
    }

    private void getPhoto() {
        Intent iPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(iPhoto, "Select Image"), REQUEST_CODE_PHOTO);
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
        final String username = mUsername.getText().toString();
        String website = mWeb.getText().toString();
        String bio = mBio.getText().toString();
        Drawable drawable = mProfilePhoto.getDrawable();
        if (TextUtils.isEmpty(fullName)) {
            mFullName.setError("Name cannot be empty!");
        }
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username cannot be empty!");
        } else {
            try {
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
                switch (gender) {
                    case "Not Specified":
                        user.put("gender", null);
                    case "Male":
                        user.put("gender", "m");
                    case "Female":
                        user.put("gender", "f");
                }
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
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
                query.whereEqualTo("username", oldUsername);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                for (ParseObject object : objects) {
                                    object.put("username", username);
                                    object.saveInBackground();
                                }
                            }
                        } else {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            } catch (IllegalArgumentException e) {
                showProgress(false);
                Log.e(TAG, e.getMessage());
                String error = "Image too large.\nPlease select image lower than 10.48MB";
                Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_LONG).show();
            }

        }
    }
}
