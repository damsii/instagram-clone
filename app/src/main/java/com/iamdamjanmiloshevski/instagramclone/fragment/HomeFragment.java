package com.iamdamjanmiloshevski.instagramclone.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iamdamjanmiloshevski.instagramclone.R;
import com.iamdamjanmiloshevski.instagramclone.adapter.UserAdapter;
import com.iamdamjanmiloshevski.instagramclone.utility.Utility;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView mUsers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout progressView, feedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        initFragmentUI(view);

        return view;
    }


    @Override
    public void initFragmentUI(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mUsers = view.findViewById(R.id.rv_users);
        mUsers.setLayoutManager(layoutManager);
        progressView = view.findViewById(R.id.progress_view);
        feedView = view.findViewById(R.id.feed);
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private void getFeed() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.orderByDescending("createdAt");
        showProgress(true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        showProgress(false);
                        UserAdapter adapter = new UserAdapter(getContext(), objects);
                        mUsers.setAdapter(adapter);
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

        feedView.setVisibility(show ? View.GONE : View.VISIBLE);
        feedView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                feedView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    private void displayCachedUsers() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_home;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (Utility.isNetworkAvailable(getContext())) {
            getFeed();
        } else {
            Toast.makeText(getContext(), "Couldn't refresh feed", Toast.LENGTH_SHORT).show();
            displayCachedUsers();
        }


    }
}
