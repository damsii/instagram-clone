package com.iamdamjanmiloshevski.instagramclone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

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
    private Context context;
    private List<ParseObject> users;

    public UserAdapter(Context context, List<ParseObject> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final ParseObject object = users.get(position);
        holder.username.setText(object.getString("username"));
        holder.description.setText(object.getString("description"));
        ParseFile file = object.getParseFile("image");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null && data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    holder.image.setImageBitmap(bitmap);
                    holder.profile_picture.equals(bitmap);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView profile_picture;
        private TextView username;
        private ImageView image;
        private TextView description;

        public UserViewHolder(View itemView) {
            super(itemView);
            profile_picture = itemView.findViewById(R.id.profile_picture);
            username = itemView.findViewById(R.id.tv_username);
            image = itemView.findViewById(R.id.iv_image);
            description = itemView.findViewById(R.id.tv_description);
        }
    }
}
