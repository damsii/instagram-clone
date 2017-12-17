package com.iamdamjanmiloshevski.instagramclone.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.activity.EditProfileActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

public class ProfileFragment extends BaseFragment {
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private TextView mFullName, mAboutMe;
    private GridView mImages;
    private ListView mListImages;
    private ImageView mGrid, mList;

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
        view.findViewById(R.id.bt_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillData();
    }

    private void fillData() {
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
    public int getLayoutId() {
        return R.layout.tab_profile;
    }
}
