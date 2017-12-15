package com.iamdamjanmiloshevski.instagramclone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iamdamjanmiloshevski.instagramclone.R;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<ParseUser> users;

    public UserAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.username.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView username;

        public UserViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
        }
    }
}
