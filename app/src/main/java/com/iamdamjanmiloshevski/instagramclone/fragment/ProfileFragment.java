package com.iamdamjanmiloshevski.instagramclone.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.activity.EditProfileActivity;
import com.iamdamjanmiloshevski.instagramclone.activity.PhotoActivity;
import com.iamdamjanmiloshevski.instagramclone.adapter.GridImagesAdapter;
import com.iamdamjanmiloshevski.instagramclone.adapter.UserAdapter;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * E-mail: damjan.milosevski@korvus.mk
 * Date: 15.12.2017
 * Project: instagram-clone
 * © Corvus Info d.o.o Zagreb, Croatia
 * --------------------------------------------
 */

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private TextView mFullName, mAboutMe;
    private GridView mImages;
    private RecyclerView mListImages;
    private ImageView mGrid, mList;
    private RelativeLayout progressView, profileView;
    private boolean imageView = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        initFragmentUI(view);

        return view;

    }

    @Override
    public void initFragmentUI(View view) {
        mProfileImage = view.findViewById(R.id.iv_profile_picture);
        mFullName = view.findViewById(R.id.tv_full_name);
        mAboutMe = view.findViewById(R.id.tv_about_me);
        mImages = view.findViewById(R.id.gv_images);
        mListImages = view.findViewById(R.id.lv_images);
        mGrid = view.findViewById(R.id.iv_grid_view);
        mList = view.findViewById(R.id.iv_list_view);
        mGrid.setOnClickListener(this);
        mList.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListImages.setLayoutManager(layoutManager);
        progressView = view.findViewById(R.id.progress_view);
        profileView = view.findViewById(R.id.profile);
        view.findViewById(R.id.bt_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        mImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iPhoto = new Intent(getContext(), PhotoActivity.class);
                ParseObject image = (ParseObject) parent.getAdapter().getItem(position);
                iPhoto.putExtra("username", ParseUser.getCurrentUser().getUsername());
                iPhoto.putExtra("imageId", image.getObjectId());
                startActivity(iPhoto);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillData();
        getUserPhotos();
    }

    private void getUserPhotos() {
        if (imageView) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            GridImagesAdapter adapter = new GridImagesAdapter(getContext(), objects);
                            mImages.setAdapter(adapter);
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            UserAdapter adapter = new UserAdapter(getContext(), objects);
                            mListImages.setAdapter(adapter);
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    }

    private void fillData() {
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
                        String fullName = user.getString("name") + " " + user.getString("surname");
                        mFullName.setText(fullName);
                        mAboutMe.setText(user.getString("about_me"));
                        if (mProfileImage.getDrawable() == null) {
                            mProfileImage.setImageResource(R.drawable.instagram);
                        } else {
                            displayImage(user);
                        }
                    }
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        profileView.setVisibility(show ? View.GONE : View.VISIBLE);
        profileView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                profileView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void displayImage(ParseUser user) {
        ParseFile file = user.getParseFile("profile_image");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mProfileImage.setImageBitmap(bitmap);
                    }
                }
            });
        } else {
            mProfileImage.setImageResource(R.drawable.instagram);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_profile;
    }

    @Override
    public void onClick(View view) {
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
