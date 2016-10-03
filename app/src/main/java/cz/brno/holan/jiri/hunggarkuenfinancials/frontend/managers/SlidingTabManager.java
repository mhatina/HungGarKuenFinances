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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewMemberActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.MemberListOnItemClickListener;

/**
 * Created by mhatina on 02/10/16.
 */
public class SlidingTabManager {

    public static final int MEMBER_ACTIVITY_INDEX = 1;
    public static final int PAYMENT_ACTIVITY_INDEX = 2;
    public static final int PRODUCT_ACTIVITY_INDEX = 0;

    CreateNewMemberActivity memberActivity;

    public SlidingTabManager() {
        memberActivity = new CreateNewMemberActivity();
    }

    public String getTabTitle(int position) {
        switch (position) {
            case MEMBER_ACTIVITY_INDEX:
                return "Members";
            case PAYMENT_ACTIVITY_INDEX:
                return "Payments";
            case PRODUCT_ACTIVITY_INDEX:
                return "Products";
            default:
                return "Unknown";
        }
    }

    public Object getInstance(FragmentActivity context, ViewGroup container, int position) {
        ListView listView;

        switch (position) {
            case MEMBER_ACTIVITY_INDEX:
                listView = prepareTabObject(context, container, new MembersAdapter(context, R.layout.layout_member, MemberManager.getInstance().getMembers()));
                listView.setOnItemClickListener(new MemberListOnItemClickListener(listView, context.getFragmentManager()));
                return listView;
            case PAYMENT_ACTIVITY_INDEX:
                return null;
            case PRODUCT_ACTIVITY_INDEX:
                ListAdapter adapter = new ProductsAdapter(context, R.layout.layout_product, ProductManager.getInstance().getProducts());
                listView = prepareTabObject(context, container, adapter);
                return listView;
            default:
                View view = context.getLayoutInflater().inflate(R.layout.pager_item,
                        container, false);

                container.addView(view);

                TextView title = (TextView) view.findViewById(R.id.item_title);
                title.setText(String.valueOf(position + 1));

                return view;
        }
    }

    private ListView prepareTabObject(FragmentActivity context, ViewGroup container, ListAdapter adapter) {
        ListView listView;

        listView = new ListView(context);
        listView.setAdapter(adapter);
        context.registerForContextMenu(listView);

        container.addView(listView);
        return listView;
    }
}
