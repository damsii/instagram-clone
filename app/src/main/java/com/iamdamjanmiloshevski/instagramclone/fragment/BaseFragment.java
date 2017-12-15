package com.iamdamjanmiloshevski.instagramclone.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * --------------------------------------------
 * Author: Damjan Miloshevski
 * E-mail: damjan.milosevski@korvus.mk
 * Date: 15.12.2017
 * Project: instagram-clone
 * © Corvus Info d.o.o Zagreb, Croatia
 * --------------------------------------------
 */

public abstract class BaseFragment extends Fragment {

    public abstract void initFragmentUI(View view);

    public abstract int getLayoutId();

}
