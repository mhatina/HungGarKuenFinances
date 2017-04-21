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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.EntityTabManager;

public class EntityTabsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_section, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SwipeViewAdapter());

        ((TabLayout) view.findViewById(R.id.sliding_tabs)).setupWithViewPager(viewPager);
    }

    private class SwipeViewAdapter extends PagerAdapter {
        private final EntityTabManager tabManager = EntityTabManager.getInstance();

        @Override
        public int getCount() {
            return Constant.NUMBER_OF_ENTITY_TABS;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (object == view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabManager.getTabTitle(getActivity(), position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return tabManager.createInstance(getActivity(), container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            tabManager.destroyList(container, position, object);
        }
    }
}
