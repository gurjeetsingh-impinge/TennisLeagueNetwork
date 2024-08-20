package com.tennisdc.tln.modules.tournament.updated;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tennisdc.tln.model.Tab;

import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private List<Tab> tabList;

    TabAdapter(FragmentManager fm, List<Tab> tabList) {
        super(fm);
        this.tabList = tabList;
    }

    @Override
    public Fragment getItem(int position) {
        return tabList.get(position).fragment;
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).title;
    }
}
