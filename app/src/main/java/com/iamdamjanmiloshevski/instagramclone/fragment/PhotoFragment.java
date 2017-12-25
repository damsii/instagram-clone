package com.iamdamjanmiloshevski.instagramclone.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.activity.MainActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * E-mail: damjan.milosevski@korvus.mk
 * Date: 15.12.2017
 * Project: instagram-clone
 * © Corvus Info d.o.o Zagreb, Croatia
 * --------------------------------------------
 */

public class PhotoFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = PhotoFragment.class.getSimpleName();
    private static final long MAX_SIZE = 10485760;
    private static final int REQUEST_CODE_PHOTO = 100;
    private TextView mTap;
    private EditText mDescription;
    private ImageView mPhoto;
    private int EXTERNAL_STORAGE_REQUEST_CODE = 200;
    private RelativeLayout imageView;
    private RelativeLayout progressView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        initFragmentUI(view);
        return view;
    }

    @Override
    public void initFragmentUI(View view) {
        mPhoto = view.findViewById(R.id.iv_image);
        mDescription = view.findViewById(R.id.et_photo_description);
        mTap = view.findViewById(R.id.tv_tap);
        String tapText = getResources().getString(R.string.tap_to_add_photo) + "\n (max 10MB)";
        mTap.setText(tapText);
        imageView = view.findViewById(R.id.post_image);
        progressView = view.findViewById(R.id.progress_view);
        mTap.setOnClickListener(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem settingsMenuItem = menu.findItem(R.id.action_post_image);
        SpannableString s = new SpannableString(settingsMenuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        settingsMenuItem.setTitle(s);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tab_camera_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_post_image:
                if (mPhoto.getDrawable() == null) {
                    processImage();
                } else {
                    uploadImageToServer(mPhoto.getDrawable());
                }
                break;
            case R.id.action_delete:
                mDescription.setText("");
                mPhoto.setImageBitmap(null);
                mPhoto.setVisibility(View.GONE);
                mTap.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String generateImageName() {
        Random random = new Random();
        int i = random.nextInt(1000);
        return "image" + Integer.toString(i) + ".png";
    }

    private void uploadImageToServer(Drawable drawable) {
        if (drawable != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap image = bitmapDrawable.getBitmap();
            if (image != null) {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    ParseFile file = new ParseFile(generateImageName(), byteArray);
                    ParseObject object = new ParseObject("Image");
                    object.put("image", file);
                    object.put("description", mDescription.getText().toString());
                    object.put("username", ParseUser.getCurrentUser().getUsername());
                    showProgress(true);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                showProgress(false);
                                Toast.makeText(getContext(), "Image shared successfully", Toast.LENGTH_SHORT).show();
                                mDescription.setText("");
                                mPhoto.setImageBitmap(null);
                                mTap.setVisibility(View.VISIBLE);
                                mPhoto.setVisibility(View.GONE);
                                MainActivity.pager.setCurrentItem(0);
                            } else {
                                showProgress(false);
                                mDescription.setText("");
                                mPhoto.setImageBitmap(null);
                                mPhoto.setVisibility(View.GONE);
                                mTap.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Image sharing failed. Please try again later!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IllegalArgumentException e) {
                    showProgress(false);
                    mDescription.setText("");
                    mPhoto.setImageBitmap(null);
                    mPhoto.setVisibility(View.GONE);
                    mTap.setVisibility(View.VISIBLE);
                    Log.e(TAG, e.getMessage());
                    String error = "Image too large.\nPlease select image lower than 10.48MB";
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Shows the progress UI and hides the login formiam
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        imageView.setVisibility(show ? View.GONE : View.VISIBLE);
        // mConfirm.setVisibility(show ? View.GONE : View.VISIBLE);
        imageView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_camera;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_tap:
                mPhoto.setVisibility(View.VISIBLE);
                mTap.setVisibility(View.GONE);
                mPhoto.setImageBitmap(null);
                processImage();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                mPhoto.setVisibility(View.VISIBLE);
                mTap.setVisibility(View.GONE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                mPhoto.setImageBitmap(bitmap);
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
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                getPhoto();
            }
        } else {
            getPhoto();
        }
    }

    private void getPhoto() {
        Intent iPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(iPhoto, "Select Image"), REQUEST_CODE_PHOTO);
    }
}
