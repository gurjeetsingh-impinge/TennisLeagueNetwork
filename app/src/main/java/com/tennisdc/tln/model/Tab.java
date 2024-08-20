package com.tennisdc.tln.model;

import androidx.fragment.app.Fragment;

public class Tab {
    public Fragment fragment;
    public String title;

    public Tab(Fragment fragment, String title) {
        this.title = title;
        this.fragment = fragment;
    }
}
