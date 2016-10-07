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

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.SlidingTabManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.SlidingTabLayout;

/**
 * Created by mhatina on 25/08/16.
 */
public class SlidingTabsFragment extends Fragment {

    public static final String TAG = "SlidingTabsFragment";
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_section, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SwipeViewAdapter());

        ((SlidingTabLayout) view.findViewById(R.id.sliding_tabs)).setViewPager(viewPager);
    }

    public class SwipeViewAdapter extends PagerAdapter {

        public static final int NUM_OF_PAGES = 3;

        private SlidingTabManager tabManager = SlidingTabManager.createInstance(viewPager);

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
            return tabManager.getTabTitle(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return tabManager.createInstance(getActivity(), container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
