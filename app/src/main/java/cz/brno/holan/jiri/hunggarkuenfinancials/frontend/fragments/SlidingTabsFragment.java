/*
 * Copyright (C) 2016  Martin Hatina
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.MemberListOnItemClickListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.SlidingTabLayout;

/**
 * Created by mhatina on 25/08/16.
 */
public class SlidingTabsFragment extends Fragment {

    public static final String TAG = "SlidingTabsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_section, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SwipeViewAdapter());

        ((SlidingTabLayout) view.findViewById(R.id.sliding_tabs)).setViewPager(viewPager);
    }

    public class SwipeViewAdapter extends PagerAdapter {

        public static final int NUM_OF_PAGES = 3;

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (object == view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListView listView;

            switch (position) {
                case 0:
                    listView = new ListView(getActivity());
                    listView.setAdapter(
                            new MembersAdapter(getContext(),
                                    R.layout.layout_member,
                                    MemberManager.getInstance().getMembers())
                    );
                    listView.setOnItemClickListener(new MemberListOnItemClickListener(listView, getActivity().getFragmentManager()));
                    container.addView(listView);
                    registerForContextMenu(listView);
                    return listView;
                default:
                    View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                            container, false);

                    container.addView(view);

                    TextView title = (TextView) view.findViewById(R.id.item_title);
                    title.setText(String.valueOf(position + 1));

                    return view;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
