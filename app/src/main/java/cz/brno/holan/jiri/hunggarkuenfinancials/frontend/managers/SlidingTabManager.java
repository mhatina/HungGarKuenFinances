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

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators.MemberComparator;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.MemberListOnItemClickListener;

public class SlidingTabManager {

    public static final int MEMBER_LIST_INDEX = 1;
    public static final int PAYMENT_LIST_INDEX = 2;
    public static final int PRODUCT_LIST_INDEX = 0;

    private ListView mMemberList = null;
    private ListView mPaymentList = null;
    private ListView mProductList = null;

    ViewPager viewPager;

    private static SlidingTabManager ourInstance = new SlidingTabManager();

    public static SlidingTabManager createInstance(ViewPager viewPager) {
        ourInstance.viewPager = viewPager;
        return ourInstance;
    }

    public static SlidingTabManager createInstance() {
        return ourInstance;
    }

    public String getTabTitle(Context context, int position) {
        Resources resources = context.getResources();
        switch (position) {
            case MEMBER_LIST_INDEX:
                return resources.getString(R.string.members_title);
            case PAYMENT_LIST_INDEX:
                return resources.getString(R.string.payments_title);
            case PRODUCT_LIST_INDEX:
                return resources.getString(R.string.products_title);
            default:
                return resources.getString(R.string.unknown_title);
        }
    }

    public Object createInstance(FragmentActivity context, ViewGroup container, int position) {
        if (mMemberList == null) {
            mMemberList = prepareTabObject(context, container, new MembersAdapter(context, R.layout.layout_member, MemberManager.getInstance().getMembers()));
            mMemberList.setOnItemClickListener(new MemberListOnItemClickListener(mMemberList, context.getFragmentManager()));

            mProductList = prepareTabObject(context, container, new ProductsAdapter(context, R.layout.layout_product, ProductManager.getInstance().getProducts()));
            // TODO setOnItemClickListener
        }

        switch (position) {
            case MEMBER_LIST_INDEX:
                return mMemberList;
            case PAYMENT_LIST_INDEX:
                return null;
            case PRODUCT_LIST_INDEX:
                return mProductList;
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

    public ListView getMemberList() {
        if (mMemberList == null) {
            mMemberList = (ListView) viewPager.getChildAt(MEMBER_LIST_INDEX);
        }

        return mMemberList;
    }

    public ListView getPaymentList() {
        if (mPaymentList == null) {
            mPaymentList = (ListView) viewPager.getChildAt(PAYMENT_LIST_INDEX);
        }

        return mPaymentList;
    }

    public ListView getProductList() {
        if (mProductList == null) {
            mProductList = (ListView) viewPager.getChildAt(PRODUCT_LIST_INDEX);
        }

        return mProductList;
    }
}
