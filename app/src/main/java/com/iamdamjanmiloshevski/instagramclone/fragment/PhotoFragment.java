package com.iamdamjanmiloshevski.instagramclone.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
    private static final int REQUEST_CODE_PHOTO = 100;
    private View mTap;
    private EditText mDescription;
    private ImageView mPhoto;
    private TextView mPost;
    private int EXTERNAL_STORAGE_REQUEST_CODE = 200;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        initFragmentUI(view);
        return view;
    }

    @Override
    public void initFragmentUI(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        mPhoto = view.findViewById(R.id.iv_image);
        mDescription = view.findViewById(R.id.et_photo_description);
        mTap = view.findViewById(R.id.tv_tap);
        mPhoto.setOnClickListener(this);
        mTap.setOnClickListener(this);
        mPost = toolbar.findViewById(R.id.tv_post);
        mPost.setOnClickListener(this);
        //setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                uploadImageToServer(mPhoto.getDrawable());
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
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                ParseFile file = new ParseFile(generateImageName(), byteArray);
                ParseObject object = new ParseObject("Image");
                object.put("image", file);
                object.put("description", mDescription.getText().toString());
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getContext(), "Image shared successfully", Toast.LENGTH_SHORT).show();
                            mDescription.setText("");
                            mTap.setVisibility(View.VISIBLE);
                            mPhoto.setVisibility(View.GONE);
                            MainActivity.pager.setCurrentItem(0);
                        } else {
                            Toast.makeText(getContext(), "Image sharing failed. Please try again later!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_camera_nav;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_tap:
                mPhoto.setVisibility(View.VISIBLE);
                mTap.setVisibility(View.GONE);
            case R.id.iv_image:
                processImage();
            case R.id.tv_post:
                uploadImageToServer(mPhoto.getDrawable());
            default:
                processImage();
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
        startActivityForResult(iPhoto, REQUEST_CODE_PHOTO);
    }
}
