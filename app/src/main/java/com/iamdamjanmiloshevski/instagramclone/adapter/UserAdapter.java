package com.iamdamjanmiloshevski.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.activity.ProfileActivity;
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
 * E-mail: d.miloshevski@gmail.com
 * Date: 15.12.2017
 * Project: instagram-clone
 * © iamdamjanmiloshevski.com
 * --------------------------------------------
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private static final String TAG = UserAdapter.class.getSimpleName();
    private Context context;
    private List<ParseObject> images;

    public UserAdapter(Context context, List<ParseObject> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final ParseObject object = images.get(position);
        holder.username.setText(object.getString("username"));
        holder.description.setText(object.getString("description"));
        ParseFile file = object.getParseFile("image");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        holder.image.setImageBitmap(bitmap);
                    } else {
                        holder.profile_picture.setImageResource(R.mipmap.ic_app_logo);
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        }
                        if (data == null) {
                            Log.e(TAG, "data is null");
                        }
                    }
                }
            });
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", object.get("username"));
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        ParseFile profile_image = objects.get(0).getParseFile("profile_image");
                        if (profile_image != null) {
                            profile_image.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        holder.profile_picture.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        } else {
                            holder.profile_picture.setImageResource(R.mipmap.ic_app_logo);
                        }

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView profile_picture;
        private TextView username;
        private ImageView image;
        private TextView description;

        UserViewHolder(View itemView) {
            super(itemView);
            profile_picture = itemView.findViewById(R.id.profile_picture);
            username = itemView.findViewById(R.id.tv_username);
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("username", username.getText().toString());
                    context.startActivity(intent);
                }
            });
            image = itemView.findViewById(R.id.iv_image);
            description = itemView.findViewById(R.id.tv_description);
        }
    }
}
